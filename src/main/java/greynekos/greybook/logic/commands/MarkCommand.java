package greynekos.greybook.logic.commands;

import static greynekos.greybook.logic.commands.UnmarkCommand.MESSAGE_USAGE;
import static greynekos.greybook.logic.parser.CliSyntax.PREFIX_ABSENT;
import static greynekos.greybook.logic.parser.CliSyntax.PREFIX_EXCUSED;
import static greynekos.greybook.logic.parser.CliSyntax.PREFIX_LATE;
import static greynekos.greybook.logic.parser.CliSyntax.PREFIX_PRESENT;
import static greynekos.greybook.logic.parser.CliSyntax.PREFIX_STUDENTID;
import static greynekos.greybook.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static java.util.Objects.requireNonNull;

import java.util.List;

import greynekos.greybook.logic.Messages;
import greynekos.greybook.logic.commands.exceptions.CommandException;
import greynekos.greybook.logic.commands.util.CommandUtil;
import greynekos.greybook.logic.parser.ArgumentParseResult;
import greynekos.greybook.logic.parser.GreyBookParser;
import greynekos.greybook.logic.parser.ParserUtil;
import greynekos.greybook.logic.parser.commandoption.RequiredMutuallyExclusivePrefixOption;
import greynekos.greybook.logic.parser.commandoption.SinglePreambleOption;
import greynekos.greybook.model.Model;
import greynekos.greybook.model.person.All;
import greynekos.greybook.model.person.AttendanceStatus;
import greynekos.greybook.model.person.Person;
import greynekos.greybook.model.person.PersonIdentifier;
import greynekos.greybook.model.person.PersonIdentifierOrAll;

/**
 * The MarkCommand marks a club member's attendance (e.g. as Present, Absent,
 * Late, Excused).
 */
public class MarkCommand extends Command {

    public static final String COMMAND_WORD = "mark";

    /** Message shown when marking is successful */
    public static final String MESSAGE_MARK_PERSON_SUCCESS = "Marked %1$s's Attendance: %2$s";
    public static final String MESSAGE_MARK_ALL_SUCCESS = "All attendance status have been marked as \"%1$s\".";
    public static final String MESSAGE_MARK_FLAGS_AND_EXAMPLES = "Flags: " + PREFIX_PRESENT + " for Present, "
            + PREFIX_ABSENT + " for Absent, " + PREFIX_LATE + " for Late, " + PREFIX_EXCUSED + " for Excused\n"
            + "Example: " + COMMAND_WORD + " 1 " + PREFIX_PRESENT + " OR " + COMMAND_WORD + " " + PREFIX_STUDENTID
            + "A0123456X " + PREFIX_ABSENT + " OR " + COMMAND_WORD + " all" + PREFIX_LATE;
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Marks a club member's attendance.\n"
            + "Parameters: INDEX (must be a positive integer) OR STUDENT_ID (format: A0000000Y)\n"
            + MESSAGE_MARK_FLAGS_AND_EXAMPLES;

    public static final String MESSAGE_MISSING_ATTENDANCE_FLAG =
            "Missing attendance flag. Please provide a valid flag\n" + MESSAGE_MARK_FLAGS_AND_EXAMPLES;

    private static final String PREFIX_GROUP_STRING = "ATTENDANCE";

    /**
     * Mark Command Preamble and Prefix Options
     */
    private final SinglePreambleOption<PersonIdentifierOrAll> identifierOrAllOption =
            SinglePreambleOption.of("ALL or INDEX or STUDENTID", ParserUtil::parsePersonIdentifierOrAll);

    private final RequiredMutuallyExclusivePrefixOption<AttendanceStatus.Status> presentOption =
            RequiredMutuallyExclusivePrefixOption.of(PREFIX_GROUP_STRING, PREFIX_PRESENT, "Present",
                    s -> AttendanceStatus.Status.PRESENT);

    private final RequiredMutuallyExclusivePrefixOption<AttendanceStatus.Status> absentOption =
            RequiredMutuallyExclusivePrefixOption.of(PREFIX_GROUP_STRING, PREFIX_ABSENT, "Absent",
                    s -> AttendanceStatus.Status.ABSENT);

    private final RequiredMutuallyExclusivePrefixOption<AttendanceStatus.Status> lateOption =
            RequiredMutuallyExclusivePrefixOption.of(PREFIX_GROUP_STRING, PREFIX_LATE, "Late",
                    s -> AttendanceStatus.Status.LATE);

    private final RequiredMutuallyExclusivePrefixOption<AttendanceStatus.Status> excusedOption =
            RequiredMutuallyExclusivePrefixOption.of(PREFIX_GROUP_STRING, PREFIX_EXCUSED, "Excused",
                    s -> AttendanceStatus.Status.EXCUSED);

    @Override
    public void addToParser(GreyBookParser parser) {
        parser.newCommand(COMMAND_WORD, MESSAGE_USAGE, this).addOptions(identifierOrAllOption, presentOption,
                absentOption, lateOption, excusedOption);
    }

    @Override
    public CommandResult execute(Model model, ArgumentParseResult arg) throws CommandException {
        requireNonNull(model);

        PersonIdentifierOrAll identifier = getParseResult(arg);
        AttendanceStatus.Status attendanceStatus = getAttendanceStatus(arg);
        if (attendanceStatus == null) {
            throw new CommandException(MESSAGE_MISSING_ATTENDANCE_FLAG);
        }

        if (identifier instanceof All) {
            return executeMarkAll(model, attendanceStatus);
        }

        Person personToMark = CommandUtil.resolvePerson(model, (PersonIdentifier) identifier);

        Person markedPerson = createMarkedPerson(personToMark, attendanceStatus);
        model.setPerson(personToMark, markedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        return new CommandResult(String.format(MESSAGE_MARK_PERSON_SUCCESS, markedPerson.getName(), attendanceStatus,
                Messages.format(markedPerson)));
    }

    private CommandResult executeMarkAll(Model model, AttendanceStatus.Status attendanceStatus) {
        List<Person> personList = model.getFilteredPersonList();

        for (Person person : personList) {
            Person markedPerson = createMarkedPerson(person, attendanceStatus);
            model.setPerson(person, markedPerson);
        }

        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_MARK_ALL_SUCCESS, attendanceStatus));
    }

    /**
     * Retrieves the attendance status specified by the prefix option.
     *
     * @param arg
     *            parsed argument values
     * @return the attendance status, or null if none provided
     */
    private AttendanceStatus.Status getAttendanceStatus(ArgumentParseResult arg) {
        if (arg.getOptionalValue(presentOption).isPresent()) {
            return AttendanceStatus.Status.PRESENT;
        }
        if (arg.getOptionalValue(absentOption).isPresent()) {
            return AttendanceStatus.Status.ABSENT;
        }
        if (arg.getOptionalValue(lateOption).isPresent()) {
            return AttendanceStatus.Status.LATE;
        }
        if (arg.getOptionalValue(excusedOption).isPresent()) {
            return AttendanceStatus.Status.EXCUSED;
        }
        return null;
    }

    /**
     * Creates a copy of the given person with the new attendance status.
     *
     * @param personToEdit
     *            the original person
     * @param attendanceStatus
     *            the new attendance status
     * @return a new Person instance with updated attendance
     */
    private static Person createMarkedPerson(Person personToEdit, AttendanceStatus.Status attendanceStatus) {
        assert personToEdit != null;
        AttendanceStatus newAttendanceStatus = new AttendanceStatus(attendanceStatus);
        return new Person(personToEdit.getName(), personToEdit.getPhone(), personToEdit.getEmail(),
                personToEdit.getStudentID(), personToEdit.getTags(), newAttendanceStatus);
    }

    @Override
    public PersonIdentifierOrAll getParseResult(ArgumentParseResult argResult) {
        return argResult.getValue(identifierOrAllOption);
    }
}
