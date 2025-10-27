package greynekos.greybook.logic.commands.util;

import static greynekos.greybook.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import greynekos.greybook.commons.core.index.Index;
import greynekos.greybook.logic.commands.exceptions.CommandException;
import greynekos.greybook.model.Model;
import greynekos.greybook.model.ModelManager;
import greynekos.greybook.model.UserPrefs;
import greynekos.greybook.model.person.Person;
import greynekos.greybook.model.person.StudentID;
import greynekos.greybook.testutil.TypicalPersons;

public class CommandUtilTest {

    private static final String NOT_FOUND_MSG = "Error, user does not exist.";

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(TypicalPersons.getTypicalGreyBook(), new UserPrefs());
    }

    @Test
    public void resolvePersonByIndex_success() {
        // one-based index "1" should resolve to the first person in the currently
        // filtered list
        Person expected = model.getFilteredPersonList().get(0);
        Person resolved = assertDoesNotThrow(() -> CommandUtil.resolvePerson(model, INDEX_FIRST_PERSON));
        assertEquals(expected, resolved);
    }

    @Test
    public void resolvePersonByIndex_outOfBoundsThrowsCommandException() {
        int outOfBoundsOneBased = model.getFilteredPersonList().size() + 1;
        CommandException ex = assertThrows(CommandException.class,
                () -> CommandUtil.resolvePerson(model, Index.fromOneBased(outOfBoundsOneBased)));
        assertEquals(NOT_FOUND_MSG, ex.getMessage());
    }

    @Test
    public void resolvePersonByStudentId_success() {
        Person expected = model.getFilteredPersonList().get(0);

        Person resolved = assertDoesNotThrow(() -> CommandUtil.resolvePerson(model, expected.getStudentID()));
        assertEquals(expected, resolved);
    }

    @Test
    public void resolvePersonByStudentId_notFoundThrowsCommandException() {
        String missingId = generateMissingValidStudentId();
        CommandException ex =
                assertThrows(CommandException.class, () -> CommandUtil.resolvePerson(model, new StudentID(missingId)));
        assertEquals(NOT_FOUND_MSG, ex.getMessage());
    }

    @Test
    public void isIndex_basicCases() {
        assertTrue(CommandUtil.isIndex("1"));
        assertTrue(CommandUtil.isIndex("2"));

        assertFalse(CommandUtil.isIndex("0"));
        assertFalse(CommandUtil.isIndex("-1"));
        assertFalse(CommandUtil.isIndex("abc"));
    }

    /**
     * Produces a valid-looking student ID that is guaranteed not to appear in the
     * current model data. Assumes standard AB3-style pattern
     * (?:A\\d{7}|U\\d{6,7})[YXWURNMLJHEAB].
     */
    private String generateMissingValidStudentId() {
        Set<String> existing =
                model.getFilteredPersonList().stream().map(p -> p.getStudentID().value).collect(Collectors.toSet());

        // Try a sequence of valid IDs until we find one not present.
        // Start from A0000000.
        int startDigits = 0;
        for (int i = 0; i < 10000000; i++) {
            String baseDigits = "A" + String.format("%07d", startDigits + i);
            String validStudentId = baseDigits + StudentID.calculateStudentIdChecksum(baseDigits);
            assertTrue(StudentID.isValidStudentID(validStudentId), validStudentId);
            if (!existing.contains(validStudentId)) {
                return validStudentId;
            }
        }
        // Extremely unlikely to happen with typical test data.
        throw new IllegalStateException("Could not generate a non-colliding StudentID for tests.");
    }
}
