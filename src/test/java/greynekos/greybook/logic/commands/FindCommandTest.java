package greynekos.greybook.logic.commands;

import static greynekos.greybook.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static greynekos.greybook.logic.commands.CommandTestUtil.assertCommandFailure;
import static greynekos.greybook.logic.commands.CommandTestUtil.assertCommandSuccess;
import static greynekos.greybook.testutil.TypicalPersons.CARL;
import static greynekos.greybook.testutil.TypicalPersons.ELLE;
import static greynekos.greybook.testutil.TypicalPersons.FIONA;
import static greynekos.greybook.testutil.TypicalPersons.getTypicalGreyBook;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import greynekos.greybook.logic.parser.ArgumentParseResult;
import greynekos.greybook.logic.parser.GreyBookParser;
import greynekos.greybook.model.History;
import greynekos.greybook.model.Model;
import greynekos.greybook.model.ModelManager;
import greynekos.greybook.model.UserPrefs;
import greynekos.greybook.model.person.NameOrStudentIdPredicate;
import greynekos.greybook.model.person.Person;
import greynekos.greybook.model.person.StudentID;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * FindCommand.
 */
public class FindCommandTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalGreyBook(), new UserPrefs(), new History());
        expectedModel = new ModelManager(model.getGreyBook(), new UserPrefs(), new History());
    }

    @Test
    public void execute_emptyCriteria_throwsCommandException() throws Exception {
        FindCommand findCommand = new FindCommand();
        GreyBookParser parser = new GreyBookParser();
        findCommand.addToParser(parser);

        String userInput = "find";
        ArgumentParseResult arg = parser.parse(userInput);

        assertCommandFailure(findCommand, model, arg, FindCommand.MESSAGE_EMPTY_COMMAND);
    }

    @Test
    public void execute_multipleKeywords_multiplePersonsFound() throws Exception {
        FindCommand findCommand = new FindCommand();
        GreyBookParser parser = new GreyBookParser();
        findCommand.addToParser(parser);

        String userInput = "find Kurz Elle Kunz";
        ArgumentParseResult arg = parser.parse(userInput);

        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 3);

        NameOrStudentIdPredicate predicate =
                new NameOrStudentIdPredicate(Arrays.asList("Kurz", "Elle", "Kunz"), Collections.emptyList());
        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(findCommand, model, arg, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(CARL, ELLE, FIONA), model.getFilteredPersonList());
    }

    @Test
    public void execute_idFragment_singlePersonFound() throws Exception {
        FindCommand findCommand = new FindCommand();
        GreyBookParser parser = new GreyBookParser();
        findCommand.addToParser(parser);

        Person target = model.getFilteredPersonList().get(0);
        StudentID sid = target.getStudentID();

        String userInput = "find i/" + sid.value;
        ArgumentParseResult arg = parser.parse(userInput);

        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1);

        NameOrStudentIdPredicate predicate =
                new NameOrStudentIdPredicate(Collections.emptyList(), Arrays.asList(sid.value));
        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(findCommand, model, arg, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(target), model.getFilteredPersonList());
    }

    @Test
    public void execute_multipleIdFragments_multiplePersonsFound() throws Exception {
        FindCommand findCommand = new FindCommand();
        GreyBookParser parser = new GreyBookParser();
        findCommand.addToParser(parser);

        Person first = model.getFilteredPersonList().get(0);
        Person second = model.getFilteredPersonList().get(1);
        String id1 = first.getStudentID().value;
        String id2 = second.getStudentID().value;

        String userInput = "find i/" + id1 + " i/" + id2;
        ArgumentParseResult arg = parser.parse(userInput);

        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 2);

        NameOrStudentIdPredicate predicate =
                new NameOrStudentIdPredicate(Collections.emptyList(), Arrays.asList(id1, id2));
        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(findCommand, model, arg, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(first, second), model.getFilteredPersonList());
    }

    @Test
    public void execute_mixedIdFragmentAndKeyword_personsFound() throws Exception {
        FindCommand findCommand = new FindCommand();
        GreyBookParser parser = new GreyBookParser();
        findCommand.addToParser(parser);

        Person byId = model.getFilteredPersonList().get(0);
        String idFrag = byId.getStudentID().value; // full ID is safe and unique
        String userInput = "find i/" + idFrag + " Elle";
        ArgumentParseResult arg = parser.parse(userInput);

        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 2);

        NameOrStudentIdPredicate predicate = new NameOrStudentIdPredicate(Arrays.asList("Elle"), Arrays.asList(idFrag));
        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(findCommand, model, arg, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(byId, ELLE), model.getFilteredPersonList());
    }
}
