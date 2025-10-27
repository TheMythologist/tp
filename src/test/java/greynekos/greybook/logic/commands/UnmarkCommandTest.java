package greynekos.greybook.logic.commands;

import static greynekos.greybook.logic.commands.CommandTestUtil.assertCommandFailure;
import static greynekos.greybook.logic.commands.CommandTestUtil.assertCommandSuccess;
import static greynekos.greybook.logic.commands.util.CommandUtil.MESSAGE_PERSON_NOT_FOUND;
import static greynekos.greybook.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static greynekos.greybook.testutil.TypicalPersons.getTypicalGreyBook;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

import greynekos.greybook.logic.parser.ArgumentParseResult;
import greynekos.greybook.logic.parser.GreyBookParser;
import greynekos.greybook.model.GreyBook;
import greynekos.greybook.model.Model;
import greynekos.greybook.model.ModelManager;
import greynekos.greybook.model.UserPrefs;
import greynekos.greybook.model.person.AttendanceStatus;
import greynekos.greybook.model.person.Person;
import greynekos.greybook.model.person.StudentID;
import greynekos.greybook.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * UnmarkCommand.
 */
public class UnmarkCommandTest {

    private Model model = new ModelManager(getTypicalGreyBook(), new UserPrefs());

    @Test
    public void execute_unmarkByIndex_success() {
        Person targetPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        UnmarkCommand unmarkCommand = new UnmarkCommand();

        GreyBookParser parser = new GreyBookParser();
        unmarkCommand.addToParser(parser);

        String userInput = "unmark " + INDEX_FIRST_PERSON.getOneBased();
        ArgumentParseResult arg = assertDoesNotThrow(() -> parser.parse(userInput));

        Person unmarkedPerson =
                new PersonBuilder(targetPerson).withAttendanceStatus(AttendanceStatus.Status.NONE).build();

        Model expectedModel = new ModelManager(new GreyBook(model.getGreyBook()), new UserPrefs());
        expectedModel.setPerson(targetPerson, unmarkedPerson);

        String expectedMessage = String.format(UnmarkCommand.MESSAGE_UNMARK_PERSON_SUCCESS, unmarkedPerson.getName());

        assertCommandSuccess(unmarkCommand, model, arg, expectedMessage, expectedModel);
    }

    @Test
    public void execute_unmarkByStudentId_success() {
        Person targetPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        StudentID sid = targetPerson.getStudentID();
        UnmarkCommand unmarkCommand = new UnmarkCommand();

        GreyBookParser parser = new GreyBookParser();
        unmarkCommand.addToParser(parser);

        String userInput = "unmark " + sid.value;
        ArgumentParseResult arg = assertDoesNotThrow(() -> parser.parse(userInput));

        Person unmarkedPerson =
                new PersonBuilder(targetPerson).withAttendanceStatus(AttendanceStatus.Status.NONE).build();

        Model expectedModel = new ModelManager(new GreyBook(model.getGreyBook()), new UserPrefs());
        expectedModel.setPerson(targetPerson, unmarkedPerson);

        String expectedMessage = String.format(UnmarkCommand.MESSAGE_UNMARK_PERSON_SUCCESS, unmarkedPerson.getName());

        assertCommandSuccess(unmarkCommand, model, arg, expectedMessage, expectedModel);
    }

    @Test
    public void execute_unmarkAll_success() {
        UnmarkCommand unmarkCommand = new UnmarkCommand();
        GreyBookParser parser = new GreyBookParser();
        unmarkCommand.addToParser(parser);

        String userInput = "unmark all";
        ArgumentParseResult arg = assertDoesNotThrow(() -> parser.parse(userInput));

        Model expectedModel = new ModelManager(new GreyBook(model.getGreyBook()), new UserPrefs());
        for (Person person : expectedModel.getFilteredPersonList()) {
            Person resetPerson = new PersonBuilder(person).withAttendanceStatus(AttendanceStatus.Status.NONE).build();
            expectedModel.setPerson(person, resetPerson);
        }

        assertCommandSuccess(unmarkCommand, model, arg, UnmarkCommand.MESSAGE_UNMARK_ALL_SUCCESS, expectedModel);
    }

    @Test
    public void execute_unmarkAllEmptyList_success() {
        Model emptyModel = new ModelManager(new GreyBook(), new UserPrefs());
        UnmarkCommand unmarkCommand = new UnmarkCommand();
        GreyBookParser parser = new GreyBookParser();
        unmarkCommand.addToParser(parser);

        String userInput = "unmark all";
        ArgumentParseResult arg = assertDoesNotThrow(() -> parser.parse(userInput));

        assertCommandSuccess(unmarkCommand, emptyModel, arg, String.format(UnmarkCommand.MESSAGE_UNMARK_ALL_SUCCESS),
                emptyModel);
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        UnmarkCommand unmarkCommand = new UnmarkCommand();
        GreyBookParser parser = new GreyBookParser();
        unmarkCommand.addToParser(parser);

        String userInput = "unmark 999";
        ArgumentParseResult arg = assertDoesNotThrow(() -> parser.parse(userInput));

        assertCommandFailure(unmarkCommand, model, arg, MESSAGE_PERSON_NOT_FOUND);
    }

    @Test
    public void execute_invalidStudentId_throwsCommandException() {
        UnmarkCommand unmarkCommand = new UnmarkCommand();
        GreyBookParser parser = new GreyBookParser();
        unmarkCommand.addToParser(parser);

        String userInput = "unmark A0000000Y";
        ArgumentParseResult arg = assertDoesNotThrow(() -> parser.parse(userInput));

        assertCommandFailure(unmarkCommand, model, arg, MESSAGE_PERSON_NOT_FOUND);
    }

    // TODO: Currently no error message is thrown for these cases. Fix later.
    // @Test
    // public void parse_noIdentifier_throwsParseException() {
    // UnmarkCommand unmarkCommand = new UnmarkCommand();
    // GreyBookParser parser = new GreyBookParser();
    // unmarkCommand.addToParser(parser);

    // String userInput = "unmark";

    // assertParseFailure(parser, userInput, MESSAGE_INVALID_PERSON_IDENTIFIER);
    // }

    // @Test
    // public void parse_multipleIdentifiers_throwsParseException() {
    // UnmarkCommand unmarkCommand = new UnmarkCommand();
    // GreyBookParser parser = new GreyBookParser();
    // unmarkCommand.addToParser(parser);

    // Person targetPerson =
    // model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
    // String userInput =
    // String.format("unmark %d %s", INDEX_FIRST_PERSON.getOneBased(),
    // targetPerson.getStudentID().value);

    // assertParseFailure(parser, userInput, MESSAGE_INVALID_PERSON_IDENTIFIER);
    // }
}
