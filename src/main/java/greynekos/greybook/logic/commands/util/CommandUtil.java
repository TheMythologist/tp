package greynekos.greybook.logic.commands.util;

import java.util.List;
import java.util.Optional;

import greynekos.greybook.commons.core.index.Index;
import greynekos.greybook.commons.util.StringUtil;
import greynekos.greybook.logic.commands.exceptions.CommandException;
import greynekos.greybook.logic.parser.ParserUtil;
import greynekos.greybook.model.Model;
import greynekos.greybook.model.person.Person;
import greynekos.greybook.model.person.PersonIdentifier;
import greynekos.greybook.model.person.StudentID;

/**
 * Common utility methods for commands
 */
public class CommandUtil {

    public static final String MESSAGE_PERSON_NOT_FOUND = "Error, user does not exist.";

    /**
     * Finds the person to delete based on the identifier. The identifier can be
     * either an index or a student ID.
     *
     * @param model
     *            The model containing the person list
     * @param identifier
     *            The identifier as a {@link PersonIdentifier} (either Index or
     *            StudentID)
     * @return The person to delete
     * @throws CommandException
     *             if the person cannot be found
     */
    public static Person resolvePerson(Model model, PersonIdentifier identifier) throws CommandException {
        if (identifier instanceof Index) {
            return findPersonByIndex(model, (Index) identifier);
        } else if (identifier instanceof StudentID) {
            model.updateFilteredPersonList(Model.PREDICATE_SHOW_ALL_PERSONS);
            return findPersonByStudentId(model, (StudentID) identifier);
        }
        // Should not reach here
        throw new CommandException(ParserUtil.MESSAGE_INVALID_PERSON_IDENTIFIER);
    }

    /**
     * Checks if the identifier is an index.
     *
     * @param identifier
     *            The identifier string
     * @return true if the identifier is a valid index
     */
    public static boolean isIndex(String identifier) {
        return StringUtil.isNonZeroUnsignedInteger(identifier);
    }

    /**
     * Finds a person by their index in the displayed list.
     *
     * @param model
     *            The model containing the person list
     * @param index
     *            The index as an {@link Index}
     * @return The person at the specified index
     * @throws CommandException
     *             if the index is out of bounds
     */
    private static Person findPersonByIndex(Model model, Index index) throws CommandException {
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(MESSAGE_PERSON_NOT_FOUND);
        }

        return lastShownList.get(index.getZeroBased());
    }

    /**
     * Finds a person by their student ID.
     *
     * @param model
     *            The model containing the person list
     * @param studentId
     *            The student ID as a {@link StudentID}
     * @return The person with the specified student ID
     * @throws CommandException
     *             if no person with the student ID is found
     */
    private static Person findPersonByStudentId(Model model, StudentID studentId) throws CommandException {
        List<Person> lastShownList = model.getFilteredPersonList();

        Optional<Person> person = lastShownList.stream().filter(p -> p.getStudentID().equals(studentId)).findFirst();

        if (!person.isPresent()) {
            throw new CommandException(MESSAGE_PERSON_NOT_FOUND);
        }

        return person.get();
    }

}
