package greynekos.greybook.storage;

import static greynekos.greybook.testutil.TypicalPersons.getTypicalGreyBook;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.file.Path;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import greynekos.greybook.commons.core.GuiSettings;
import greynekos.greybook.commons.core.history.CommandHistory;
import greynekos.greybook.model.GreyBook;
import greynekos.greybook.model.History;
import greynekos.greybook.model.ReadOnlyGreyBook;
import greynekos.greybook.model.UserPrefs;

public class StorageManagerTest {

    @TempDir
    public Path testFolder;

    private StorageManager storageManager;

    @BeforeEach
    public void setUp() {
        JsonGreyBookStorage greyBookStorage = new JsonGreyBookStorage(getTempFilePath("ab"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(getTempFilePath("prefs"));
        JsonHistoryStorage historyStorage = new JsonHistoryStorage(getTempFilePath("history"));
        storageManager = new StorageManager(greyBookStorage, userPrefsStorage, historyStorage);
    }

    private Path getTempFilePath(String fileName) {
        return testFolder.resolve(fileName);
    }

    @Test
    public void prefsReadSave() throws Exception {
        /*
         * Note: This is an integration test that verifies the StorageManager is
         * properly wired to the {@link JsonUserPrefsStorage} class. More extensive
         * testing of UserPref saving/reading is done in {@link
         * JsonUserPrefsStorageTest} class.
         */
        UserPrefs original = new UserPrefs();
        original.setGuiSettings(new GuiSettings(300, 600, 4, 6, false));
        storageManager.saveUserPrefs(original);
        UserPrefs retrieved = storageManager.readUserPrefs().get();
        assertEquals(original, retrieved);
    }

    @Test
    public void historyReadSave() throws Exception {
        /*
         * Note: This is an integration test that verifies the StorageManager is
         * properly wired to the {@link JsonHistoryStorage} class. More extensive
         * testing of History saving/reading is done in {@link JsonHistoryStorageTest}
         * class.
         */
        History original = new History();
        original.setCommandHistory(new CommandHistory(Arrays.asList("mark 1 p/", "find test")));
        storageManager.saveHistory(original);
        History retrieved = storageManager.readHistory().get();
        assertEquals(original, retrieved);
    }

    @Test
    public void greyBookReadSave() throws Exception {
        /*
         * Note: This is an integration test that verifies the StorageManager is
         * properly wired to the {@link JsonGreyBookStorage} class. More extensive
         * testing of UserPref saving/reading is done in {@link JsonGreyBookStorageTest}
         * class.
         */
        GreyBook original = getTypicalGreyBook();
        storageManager.saveGreyBook(original);
        ReadOnlyGreyBook retrieved = storageManager.readGreyBook().get();
        assertEquals(original, new GreyBook(retrieved));
    }

    @Test
    public void getGreyBookFilePath() {
        assertNotNull(storageManager.getGreyBookFilePath());
    }

}
