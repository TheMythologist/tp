package greynekos.greybook.model;

import static greynekos.greybook.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static greynekos.greybook.testutil.Assert.assertThrows;
import static greynekos.greybook.testutil.TypicalPersons.ALICE;
import static greynekos.greybook.testutil.TypicalPersons.BENSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import greynekos.greybook.commons.core.GuiSettings;
import greynekos.greybook.commons.core.history.CommandHistory;
import greynekos.greybook.model.person.NameOrStudentIdPredicate;
import greynekos.greybook.testutil.GreyBookBuilder;

public class ModelManagerTest {

    private ModelManager modelManager = new ModelManager();

    @Test
    public void constructor() {
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new GreyBook(), new GreyBook(modelManager.getGreyBook()));
    }

    @Test
    public void setUserPrefs_nullUserPrefs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setUserPrefs(null));
    }

    @Test
    public void setUserPrefs_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setGreyBookFilePath(Paths.get("greybook/book/file/path"));
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4, false));
        modelManager.setUserPrefs(userPrefs);
        assertEquals(userPrefs, modelManager.getUserPrefs());

        // Modifying userPrefs should not modify modelManager's userPrefs
        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setGreyBookFilePath(Paths.get("new/greybook/book/file/path"));
        assertEquals(oldUserPrefs, modelManager.getUserPrefs());
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setGuiSettings(null));
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4, false);
        modelManager.setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    public void setHistory_nullHistory_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setHistory(null));
    }

    @Test
    public void setHistory_validHistory_copiesHistory() {
        History history = new History();
        history.setCommandHistory(new CommandHistory());
        modelManager.setHistory(history);

        // Modifying history should not modify modelManager's history
        History oldHistory = new History(history);
        history.setCommandHistory(new CommandHistory());
        assertEquals(oldHistory, modelManager.getHistory());
    }

    @Test
    public void setHistory_validHistory_setsHistory() {
        History history = new History();
        modelManager.setHistory(history);
        assertEquals(history, modelManager.getHistory());
    }

    @Test
    public void setGreyBookFilePath_nullPath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setGreyBookFilePath(null));
    }

    @Test
    public void setGreyBookFilePath_validPath_setsGreyBookFilePath() {
        Path path = Paths.get("greybook/book/file/path");
        modelManager.setGreyBookFilePath(path);
        assertEquals(path, modelManager.getGreyBookFilePath());
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInGreyBook_returnsFalse() {
        assertFalse(modelManager.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInGreyBook_returnsTrue() {
        modelManager.addPerson(ALICE);
        assertTrue(modelManager.hasPerson(ALICE));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredPersonList().remove(0));
    }

    @Test
    public void equals() {
        GreyBook greyBook = new GreyBookBuilder().withPerson(ALICE).withPerson(BENSON).build();
        GreyBook differentGreyBook = new GreyBook();
        UserPrefs userPrefs = new UserPrefs();
        History history = new History();

        // same values -> returns true
        modelManager = new ModelManager(greyBook, userPrefs, history);
        ModelManager modelManagerCopy = new ModelManager(greyBook, userPrefs, history);
        assertTrue(modelManager.equals(modelManagerCopy));

        // same object -> returns true
        assertTrue(modelManager.equals(modelManager));

        // null -> returns false
        assertFalse(modelManager.equals(null));

        // different types -> returns false
        assertFalse(modelManager.equals(5));

        // different greyBook -> returns false
        assertFalse(modelManager.equals(new ModelManager(differentGreyBook, userPrefs, history)));

        // different filteredList -> returns false
        String[] keywords = ALICE.getName().fullName.split("\\s+");
        modelManager.updateFilteredPersonList(new NameOrStudentIdPredicate(Arrays.asList(keywords), List.of()));
        assertFalse(modelManager.equals(new ModelManager(greyBook, userPrefs, history)));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setGreyBookFilePath(Paths.get("differentFilePath"));
        assertFalse(modelManager.equals(new ModelManager(greyBook, differentUserPrefs, history)));

        // different history -> returns false
        History differentHistory = new History();
        differentHistory.getCommandHistory().addCommand("test");
        assertFalse(modelManager.equals(new ModelManager(greyBook, userPrefs, differentHistory)));
    }
}
