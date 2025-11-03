package greynekos.greybook.logic.commands;

import static greynekos.greybook.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static greynekos.greybook.logic.commands.CommandTestUtil.assertCommandFailure;
import static greynekos.greybook.logic.commands.CommandTestUtil.assertCommandSuccess;
import static greynekos.greybook.logic.commands.MarkCommand.MESSAGE_MARK_ALL_SUCCESS;
import static greynekos.greybook.logic.commands.MarkCommand.MESSAGE_USAGE;
import static greynekos.greybook.logic.commands.util.CommandUtil.MESSAGE_PERSON_NOT_FOUND;
import static greynekos.greybook.logic.parser.CliSyntax.PREFIX_ABSENT;
import static greynekos.greybook.logic.parser.CliSyntax.PREFIX_PRESENT;
import static greynekos.greybook.logic.parser.CommandParserTestUtil.assertParseFailure;
import static greynekos.greybook.logic.parser.ParserUtil.MESSAGE_UNKNOWN_FOLLOWING_IDENTIFIER;
import static greynekos.greybook.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static greynekos.greybook.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static greynekos.greybook.testutil.TypicalPersons.getTypicalGreyBook;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

import greynekos.greybook.logic.Messages;
import greynekos.greybook.logic.parser.ArgumentParseResult;
import greynekos.greybook.logic.parser.GreyBookParser;
import greynekos.greybook.model.GreyBook;
import greynekos.greybook.model.History;
import greynekos.greybook.model.Model;
import greynekos.greybook.model.ModelManager;
import greynekos.greybook.model.UserPrefs;
import greynekos.greybook.model.person.AttendanceStatus;
import greynekos.greybook.model.person.Person;
import greynekos.greybook.model.person.StudentID;
import greynekos.greybook.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * MarkCommand.
 */
public class MarkCommandTest {

    private Model model = new ModelManager(getTypicalGreyBook(), new UserPrefs(), new History());

    @Test
    public void execute_markByIndex_success() {
        Person targetPerson = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        MarkCommand markCommand = new MarkCommand();

        GreyBookParser parser = new GreyBookParser();
        markCommand.addToParser(parser);

        String userInput = "mark " + INDEX_SECOND_PERSON.getOneBased() + " p/";
        ArgumentParseResult arg = assertDoesNotThrow(() -> parser.parse(userInput));

        Person markedPerson =
                new PersonBuilder(targetPerson).withAttendanceStatus(AttendanceStatus.Status.PRESENT).build();

        Model expectedModel = new ModelManager(new GreyBook(model.getGreyBook()), new UserPrefs(), new History());
        expectedModel.setPerson(targetPerson, markedPerson);

        String expectedMessage = String.format(MarkCommand.MESSAGE_MARK_PERSON_SUCCESS, markedPerson.getName(),
                AttendanceStatus.Status.PRESENT, Messages.format(markedPerson));

        assertCommandSuccess(markCommand, model, arg, expectedMessage, expectedModel);
    }

    @Test
    public void execute_markByIndexWithExtraSpaces_success() {
        Person targetPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        MarkCommand markCommand = new MarkCommand();
        GreyBookParser parser = new GreyBookParser();
        markCommand.addToParser(parser);

        String userInput = "   mark   " + INDEX_FIRST_PERSON.getOneBased() + "   e/   ";
        ArgumentParseResult arg = assertDoesNotThrow(() -> parser.parse(userInput));

        Person markedPerson =
                new PersonBuilder(targetPerson).withAttendanceStatus(AttendanceStatus.Status.EXCUSED).build();
        Model expectedModel = new ModelManager(new GreyBook(model.getGreyBook()), new UserPrefs(), new History());
        expectedModel.setPerson(targetPerson, markedPerson);

        String expectedMessage = String.format(MarkCommand.MESSAGE_MARK_PERSON_SUCCESS, markedPerson.getName(),
                AttendanceStatus.Status.EXCUSED, Messages.format(markedPerson));

        assertCommandSuccess(markCommand, model, arg, expectedMessage, expectedModel);
    }

    @Test
    public void execute_markByStudentId_success() {
        Person targetPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        StudentID sid = targetPerson.getStudentID();
        MarkCommand markCommand = new MarkCommand();

        GreyBookParser parser = new GreyBookParser();
        markCommand.addToParser(parser);

        String userInput = "mark " + sid.value + " l/";
        ArgumentParseResult arg = assertDoesNotThrow(() -> parser.parse(userInput));

        Person markedPerson =
                new PersonBuilder(targetPerson).withAttendanceStatus(AttendanceStatus.Status.LATE).build();

        Model expectedModel = new ModelManager(new GreyBook(model.getGreyBook()), new UserPrefs(), new History());
        expectedModel.setPerson(targetPerson, markedPerson);

        String expectedMessage = String.format(MarkCommand.MESSAGE_MARK_PERSON_SUCCESS, markedPerson.getName(),
                AttendanceStatus.Status.LATE, Messages.format(markedPerson));

        assertCommandSuccess(markCommand, model, arg, expectedMessage, expectedModel);
    }

    @Test
    public void execute_markAll_success() throws Exception {
        MarkCommand markCommand = new MarkCommand();
        GreyBookParser parser = new GreyBookParser();
        markCommand.addToParser(parser);

        String userInput = "mark all p/";
        ArgumentParseResult arg = parser.parse(userInput);

        Model expectedModel = new ModelManager(new GreyBook(model.getGreyBook()), new UserPrefs(), new History());
        for (Person person : expectedModel.getFilteredPersonList()) {
            Person markedPerson =
                    new PersonBuilder(person).withAttendanceStatus(AttendanceStatus.Status.PRESENT).build();
            expectedModel.setPerson(person, markedPerson);
        }

        assertCommandSuccess(markCommand, model, arg,
                String.format(MESSAGE_MARK_ALL_SUCCESS, AttendanceStatus.Status.PRESENT), expectedModel);

    }

    @Test
    public void execute_markAllEmptyList_success() throws Exception {
        Model emptyModel = new ModelManager(new GreyBook(), new UserPrefs(), new History());
        MarkCommand markCommand = new MarkCommand();
        GreyBookParser parser = new GreyBookParser();
        markCommand.addToParser(parser);

        String userInput = "mark all p/";
        ArgumentParseResult arg = parser.parse(userInput);

        assertCommandSuccess(markCommand, emptyModel, arg,
                String.format(MESSAGE_MARK_ALL_SUCCESS, AttendanceStatus.Status.PRESENT), emptyModel);
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        MarkCommand markCommand = new MarkCommand();
        GreyBookParser parser = new GreyBookParser();
        markCommand.addToParser(parser);

        String userInput = "mark 999 p/";
        ArgumentParseResult arg = assertDoesNotThrow(() -> parser.parse(userInput));

        assertCommandFailure(markCommand, model, arg, MESSAGE_PERSON_NOT_FOUND);
    }

    @Test
    public void execute_invalidStudentId_throwsCommandException() {
        MarkCommand markCommand = new MarkCommand();
        GreyBookParser parser = new GreyBookParser();
        markCommand.addToParser(parser);

        String userInput = "mark A0000000Y a/";
        ArgumentParseResult arg = assertDoesNotThrow(() -> parser.parse(userInput));

        assertCommandFailure(markCommand, model, arg, MESSAGE_PERSON_NOT_FOUND);
    }

    @Test
    public void execute_missingAttendanceFlag_throwsCommandException() {
        MarkCommand markCommand = new MarkCommand();
        GreyBookParser parser = new GreyBookParser();
        markCommand.addToParser(parser);

        String userInput = "mark " + INDEX_FIRST_PERSON.getOneBased();
        ArgumentParseResult arg = assertDoesNotThrow(() -> parser.parse(userInput));

        assertCommandFailure(markCommand, model, arg, MarkCommand.MESSAGE_MISSING_ATTENDANCE_FLAG);
    }

    @Test
    public void parse_invalidPrefix_throwsParseException() {
        MarkCommand markCommand = new MarkCommand();
        GreyBookParser parser = new GreyBookParser();
        markCommand.addToParser(parser);

        String userInput = "mark 1 x/";

        assertParseFailure(parser, userInput, String.format(MESSAGE_UNKNOWN_FOLLOWING_IDENTIFIER, "x/"));
    }

    @Test
    public void parse_noIdentifier_throwsParseException() {
        MarkCommand markCommand = new MarkCommand();
        GreyBookParser parser = new GreyBookParser();
        markCommand.addToParser(parser);

        String userInput = "mark p/";

        assertParseFailure(parser, userInput, String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE));
    }

    @Test
    public void parse_multipleIdentifiers_throwsParseException() {
        MarkCommand markCommand = new MarkCommand();
        GreyBookParser parser = new GreyBookParser();
        markCommand.addToParser(parser);

        Person targetPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        String userInput =
                String.format("mark %d %s p/", INDEX_FIRST_PERSON.getOneBased(), targetPerson.getStudentID().value);

        assertParseFailure(parser, userInput,
                String.format(MESSAGE_UNKNOWN_FOLLOWING_IDENTIFIER, targetPerson.getStudentID()));
    }

    @Test
    public void parse_multipleAttendanceFlags_throwsParseException() {
        MarkCommand markCommand = new MarkCommand();
        GreyBookParser parser = new GreyBookParser();
        markCommand.addToParser(parser);

        assertParseFailure(parser, "mark 1 p/ a/",
                Messages.getErrorMessageForMutuallyExclusivePrefixes(PREFIX_PRESENT, PREFIX_ABSENT));
    }
}
