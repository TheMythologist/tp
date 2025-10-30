package greynekos.greybook.logic.commands;

import static greynekos.greybook.logic.parser.CliSyntax.PREFIX_EMAIL;
import static greynekos.greybook.logic.parser.CliSyntax.PREFIX_NAME;
import static greynekos.greybook.logic.parser.CliSyntax.PREFIX_PHONE;
import static greynekos.greybook.logic.parser.CliSyntax.PREFIX_STUDENTID;
import static greynekos.greybook.logic.parser.CliSyntax.PREFIX_TAG;
import static greynekos.greybook.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import greynekos.greybook.commons.core.index.Index;
import greynekos.greybook.commons.util.CollectionUtil;
import greynekos.greybook.commons.util.ToStringBuilder;
import greynekos.greybook.logic.Messages;
import greynekos.greybook.logic.commands.exceptions.CommandException;
import greynekos.greybook.logic.parser.ArgumentParseResult;
import greynekos.greybook.logic.parser.GreyBookParser;
import greynekos.greybook.logic.parser.ParserUtil;
import greynekos.greybook.logic.parser.commandoption.OptionalPrefixOption;
import greynekos.greybook.logic.parser.commandoption.SinglePreambleOption;
import greynekos.greybook.logic.parser.commandoption.ZeroOrMorePrefixOption;
import greynekos.greybook.model.Model;
import greynekos.greybook.model.person.AttendanceStatus;
import greynekos.greybook.model.person.Email;
import greynekos.greybook.model.person.Name;
import greynekos.greybook.model.person.Person;
import greynekos.greybook.model.person.Phone;
import greynekos.greybook.model.person.StudentID;
import greynekos.greybook.model.tag.Tag;

/**
 * Edits the details of an existing person in GreyBook.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the person identified "
            + "by the index number used in the displayed person list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) " + "[" + PREFIX_NAME + "NAME] " + "[" + PREFIX_PHONE
            + "PHONE] " + "[" + PREFIX_EMAIL + "EMAIL] " + "[" + PREFIX_STUDENTID + "STUDENTID] " + "[" + PREFIX_TAG
            + "TAG]...\n" + "Example:\n" + "  " + COMMAND_WORD + " 1 " + PREFIX_PHONE + "91234567 " + PREFIX_EMAIL
            + "johndoe@example.com";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited Person: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in GreyBook.";

    private final SinglePreambleOption<Index> indexOption = SinglePreambleOption.of("INDEX", ParserUtil::parseIndex);
    private final OptionalPrefixOption<Name> nameOption =
            OptionalPrefixOption.of(PREFIX_NAME, "NAME", ParserUtil::parseName);
    private final OptionalPrefixOption<Phone> phoneOption =
            OptionalPrefixOption.of(PREFIX_PHONE, "PHONE", ParserUtil::parsePhone);
    private final OptionalPrefixOption<Email> emailOption =
            OptionalPrefixOption.of(PREFIX_EMAIL, "EMAIL", ParserUtil::parseEmail);
    private final OptionalPrefixOption<StudentID> studentIdOption =
            OptionalPrefixOption.of(PREFIX_STUDENTID, "STUDENTID", ParserUtil::parseStudentID);
    private final ZeroOrMorePrefixOption<Tag> tagOption =
            ZeroOrMorePrefixOption.of(PREFIX_TAG, "TAG", ParserUtil::parseTagAllowEmpty);

    @Override
    public void addToParser(GreyBookParser parser) {
        parser.newCommand(COMMAND_WORD, MESSAGE_USAGE, this).addOptions(indexOption, nameOption, phoneOption,
                emailOption, studentIdOption, tagOption);
    }

    private Optional<Set<Tag>> parseTagsForEdit(Collection<Tag> tags) {
        assert tags != null;

        if (tags.isEmpty()) {
            return Optional.empty();
        }

        Collection<Tag> tagSet = tags.size() == 1 && tags.stream().anyMatch(tag -> tag.tagName.equals(""))
                ? Collections.emptySet()
                : tags;
        return Optional.of(Set.copyOf(tagSet));
    }

    @Override
    public CommandResult execute(Model model, ArgumentParseResult arg) throws CommandException {
        requireNonNull(model);

        Index index = arg.getValue(indexOption);

        EditPersonDescriptor editPersonDescriptor = getParseResult(arg);

        if (!editPersonDescriptor.isAnyFieldEdited()) {
            throw new CommandException(EditCommand.MESSAGE_NOT_EDITED);
        }

        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());
        Person editedPerson = createEditedPerson(personToEdit, editPersonDescriptor);

        if (!personToEdit.isSamePerson(editedPerson) && model.hasPerson(editedPerson)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson)));
    }

    @Override
    public EditPersonDescriptor getParseResult(ArgumentParseResult argResult) {
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();

        if (argResult.getOptionalValue(nameOption).isPresent()) {
            editPersonDescriptor.setName(argResult.getOptionalValue(nameOption).get());
        }
        if (argResult.getOptionalValue(phoneOption).isPresent()) {
            editPersonDescriptor.setPhone(argResult.getOptionalValue(phoneOption).get());
        }
        if (argResult.getOptionalValue(emailOption).isPresent()) {
            editPersonDescriptor.setEmail(argResult.getOptionalValue(emailOption).get());
        }
        if (argResult.getOptionalValue(studentIdOption).isPresent()) {
            editPersonDescriptor.setStudentID(argResult.getOptionalValue(studentIdOption).get());
        }
        parseTagsForEdit(argResult.getAllValues(tagOption)).ifPresent(editPersonDescriptor::setTags);

        return editPersonDescriptor;
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    private static Person createEditedPerson(Person personToEdit, EditPersonDescriptor editPersonDescriptor) {
        assert personToEdit != null;

        Name updatedName = editPersonDescriptor.getName().orElse(personToEdit.getName());
        Phone updatedPhone = editPersonDescriptor.getPhone().orElse(personToEdit.getPhone());
        Email updatedEmail = editPersonDescriptor.getEmail().orElse(personToEdit.getEmail());
        StudentID updatedStudentId = editPersonDescriptor.getStudentID().orElse(personToEdit.getStudentID());
        Set<Tag> updatedTags = editPersonDescriptor.getTags().orElse(personToEdit.getTags());
        AttendanceStatus updatedAttendance = personToEdit.getAttendance();

        return new Person(updatedName, updatedPhone, updatedEmail, updatedStudentId, updatedTags, updatedAttendance);
    }

    /**
     * Stores the details to edit the person with. Each non-empty field value will
     * replace the corresponding field value of the person.
     */
    public static class EditPersonDescriptor {
        private Name name;
        private Phone phone;
        private Email email;
        private StudentID studentId;
        private Set<Tag> tags;

        public EditPersonDescriptor() {
        }

        /**
         * Copy constructor. A defensive copy of {@code tags} is used internally.
         */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setStudentID(toCopy.studentId);
            setTags(toCopy.tags);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, email, studentId, tags);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        public Optional<StudentID> getStudentID() {
            return Optional.ofNullable(studentId);
        }

        public void setStudentID(StudentID studentID) {
            this.studentId = studentID;
        }

        /**
         * Sets {@code tags} to this object's {@code tags}. A defensive copy of
         * {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws
         * {@code UnsupportedOperationException} if modification is attempted. Returns
         * {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditPersonDescriptor)) {
                return false;
            }

            EditPersonDescriptor otherEditPersonDescriptor = (EditPersonDescriptor) other;
            return Objects.equals(name, otherEditPersonDescriptor.name)
                    && Objects.equals(phone, otherEditPersonDescriptor.phone)
                    && Objects.equals(email, otherEditPersonDescriptor.email)
                    && Objects.equals(studentId, otherEditPersonDescriptor.studentId)
                    && Objects.equals(tags, otherEditPersonDescriptor.tags);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this).add("name", name).add("phone", phone).add("email", email)
                    .add("studentID", studentId).add("tags", tags).toString();
        }
    }
}
