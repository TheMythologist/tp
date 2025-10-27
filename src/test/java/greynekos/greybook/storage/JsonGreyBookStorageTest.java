package greynekos.greybook.storage;

import static greynekos.greybook.testutil.Assert.assertThrows;
import static greynekos.greybook.testutil.TypicalPersons.ALICE;
import static greynekos.greybook.testutil.TypicalPersons.HOON;
import static greynekos.greybook.testutil.TypicalPersons.IDA;
import static greynekos.greybook.testutil.TypicalPersons.getTypicalGreyBook;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import greynekos.greybook.commons.exceptions.DataLoadingException;
import greynekos.greybook.model.GreyBook;
import greynekos.greybook.model.ReadOnlyGreyBook;

public class JsonGreyBookStorageTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonGreyBookStorageTest");

    @TempDir
    public Path testFolder;

    @Test
    public void readGreyBook_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> readGreyBook(null));
    }

    private java.util.Optional<ReadOnlyGreyBook> readGreyBook(String filePath) throws DataLoadingException {
        return new JsonGreyBookStorage(Paths.get(filePath)).readGreyBook(addToTestDataPathIfNotNull(filePath));
    }

    private Path addToTestDataPathIfNotNull(String prefsFileInTestDataFolder) {
        return prefsFileInTestDataFolder != null ? TEST_DATA_FOLDER.resolve(prefsFileInTestDataFolder) : null;
    }

    @Test
    public void read_missingFile_emptyResult() {
        assertFalse(assertDoesNotThrow(() -> readGreyBook("NonExistentFile.json")).isPresent());
    }

    @Test
    public void read_notJsonFormat_exceptionThrown() {
        assertThrows(DataLoadingException.class, () -> readGreyBook("notJsonFormatGreyBook.json"));
    }

    @Test
    public void readGreyBook_invalidPersonGreyBook_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readGreyBook("invalidPersonGreyBook.json"));
    }

    @Test
    public void readGreyBook_invalidAndValidPersonGreyBook_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readGreyBook("invalidAndValidPersonGreyBook.json"));
    }

    @Test
    public void readAndSaveGreyBook_allInOrder_success() {
        Path filePath = testFolder.resolve("TempGreyBook.json");
        GreyBook original = getTypicalGreyBook();
        JsonGreyBookStorage jsonGreyBookStorage = new JsonGreyBookStorage(filePath);

        // Save in new file and read back
        assertDoesNotThrow(() -> jsonGreyBookStorage.saveGreyBook(original, filePath));
        ReadOnlyGreyBook readBack = assertDoesNotThrow(() -> jsonGreyBookStorage.readGreyBook(filePath)).get();
        assertEquals(original, new GreyBook(readBack));

        // Modify data, overwrite exiting file, and read back
        original.addPerson(HOON);
        original.removePerson(ALICE);
        assertDoesNotThrow(() -> jsonGreyBookStorage.saveGreyBook(original, filePath));
        readBack = assertDoesNotThrow(() -> jsonGreyBookStorage.readGreyBook(filePath)).get();
        assertEquals(original, new GreyBook(readBack));

        // Save and read without specifying file path
        original.addPerson(IDA);
        assertDoesNotThrow(() -> jsonGreyBookStorage.saveGreyBook(original)); // file path not specified
        readBack = assertDoesNotThrow(() -> jsonGreyBookStorage.readGreyBook()).get(); // file path not specified
        assertEquals(original, new GreyBook(readBack));

    }

    @Test
    public void saveGreyBook_nullGreyBook_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveGreyBook(null, "SomeFile.json"));
    }

    /**
     * Saves {@code greyBook} at the specified {@code filePath}.
     */
    private void saveGreyBook(ReadOnlyGreyBook greyBook, String filePath) {
        try {
            new JsonGreyBookStorage(Paths.get(filePath)).saveGreyBook(greyBook, addToTestDataPathIfNotNull(filePath));
        } catch (IOException ioe) {
            throw new AssertionError("There should not be an error writing to the file.", ioe);
        }
    }

    @Test
    public void saveGreyBook_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveGreyBook(new GreyBook(), null));
    }
}
