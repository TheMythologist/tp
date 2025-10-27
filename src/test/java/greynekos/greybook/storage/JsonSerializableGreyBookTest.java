package greynekos.greybook.storage;

import static greynekos.greybook.testutil.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import greynekos.greybook.commons.exceptions.IllegalValueException;
import greynekos.greybook.commons.util.JsonUtil;
import greynekos.greybook.model.GreyBook;
import greynekos.greybook.testutil.TypicalPersons;

public class JsonSerializableGreyBookTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableGreyBookTest");
    private static final Path TYPICAL_PERSONS_FILE = TEST_DATA_FOLDER.resolve("typicalPersonsGreyBook.json");
    private static final Path INVALID_PERSON_FILE = TEST_DATA_FOLDER.resolve("invalidPersonGreyBook.json");
    private static final Path DUPLICATE_PERSON_FILE = TEST_DATA_FOLDER.resolve("duplicatePersonGreyBook.json");

    @Test
    public void toModelType_typicalPersonsFile_success() {
        JsonSerializableGreyBook dataFromFile =
                assertDoesNotThrow(() -> JsonUtil.readJsonFile(TYPICAL_PERSONS_FILE, JsonSerializableGreyBook.class))
                        .get();
        GreyBook greyBookFromFile = assertDoesNotThrow(() -> dataFromFile.toModelType());
        GreyBook typicalPersonsGreyBook = TypicalPersons.getTypicalGreyBook();
        assertEquals(greyBookFromFile, typicalPersonsGreyBook);
    }

    @Test
    public void toModelType_invalidPersonFile_throwsIllegalValueException() {
        JsonSerializableGreyBook dataFromFile =
                assertDoesNotThrow(() -> JsonUtil.readJsonFile(INVALID_PERSON_FILE, JsonSerializableGreyBook.class))
                        .get();
        assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicatePersons_throwsIllegalValueException() {
        JsonSerializableGreyBook dataFromFile =
                assertDoesNotThrow(() -> JsonUtil.readJsonFile(DUPLICATE_PERSON_FILE, JsonSerializableGreyBook.class))
                        .get();
        assertThrows(IllegalValueException.class, JsonSerializableGreyBook.MESSAGE_DUPLICATE_PERSON,
                dataFromFile::toModelType);
    }

}
