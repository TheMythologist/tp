package greynekos.greybook.logic.parser;

import static greynekos.greybook.logic.parser.ParserUtil.MESSAGE_INVALID_INDEX;
import static greynekos.greybook.testutil.Assert.assertThrows;
import static greynekos.greybook.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import greynekos.greybook.logic.parser.exceptions.ParseException;
import greynekos.greybook.model.person.Email;
import greynekos.greybook.model.person.Name;
import greynekos.greybook.model.person.Phone;
import greynekos.greybook.model.person.StudentID;
import greynekos.greybook.model.tag.Tag;

public class ParserUtilTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+651234";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_STUDENTID = "B1234567A";
    private static final String INVALID_TAG = "#friend";

    private static final String VALID_NAME = "Rachel Walker";
    private static final String VALID_PHONE = "123456";
    private static final String VALID_EMAIL = "rachel@example.com";
    private static final String VALID_STUDENTID = "A1234567X";
    private static final String VALID_TAG_1 = "friend";
    private static final String VALID_TAG_2 = "neighbour";

    private static final String WHITESPACE = " \t\r\n";

    @Test
    public void parseIndex_invalidInput_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseIndex("10 a"));
    }

    @Test
    public void parseIndex_outOfRangeInput_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_INDEX,
                () -> ParserUtil.parseIndex(Long.toString(Integer.MAX_VALUE + 1)));
    }

    @Test
    public void parseIndex_validInput_success() {
        // No whitespaces
        assertEquals(INDEX_FIRST_PERSON, assertDoesNotThrow(() -> ParserUtil.parseIndex("1")));

        // Leading and trailing whitespaces
        assertEquals(INDEX_FIRST_PERSON, assertDoesNotThrow(() -> ParserUtil.parseIndex("  1  ")));
    }

    @Test
    public void parseName_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseName((String) null));
    }

    @Test
    public void parseName_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseName(INVALID_NAME));
    }

    @Test
    public void parseName_validValueWithoutWhitespace_returnsName() {
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, assertDoesNotThrow(() -> ParserUtil.parseName(VALID_NAME)));
    }

    @Test
    public void parseName_validValueWithWhitespace_returnsTrimmedName() {
        String nameWithWhitespace = WHITESPACE + VALID_NAME + WHITESPACE;
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, assertDoesNotThrow(() -> ParserUtil.parseName(nameWithWhitespace)));
    }

    @Test
    public void parsePhone_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parsePhone((String) null));
    }

    @Test
    public void parsePhone_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parsePhone(INVALID_PHONE));
    }

    @Test
    public void parsePhone_validValueWithoutWhitespace_returnsPhone() {
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, assertDoesNotThrow(() -> ParserUtil.parsePhone(VALID_PHONE)));
    }

    @Test
    public void parsePhone_validValueWithWhitespace_returnsTrimmedPhone() {
        String phoneWithWhitespace = WHITESPACE + VALID_PHONE + WHITESPACE;
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, assertDoesNotThrow(() -> ParserUtil.parsePhone(phoneWithWhitespace)));
    }

    @Test
    public void parseEmail_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseEmail((String) null));
    }

    @Test
    public void parseEmail_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseEmail(INVALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithoutWhitespace_returnsEmail() {
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, assertDoesNotThrow(() -> ParserUtil.parseEmail(VALID_EMAIL)));
    }

    @Test
    public void parseEmail_validValueWithWhitespace_returnsTrimmedEmail() {
        String emailWithWhitespace = WHITESPACE + VALID_EMAIL + WHITESPACE;
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, assertDoesNotThrow(() -> ParserUtil.parseEmail(emailWithWhitespace)));
    }

    @Test
    public void parseStudentID_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseStudentID((String) null));
    }

    @Test
    public void parseStudentID_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseStudentID(INVALID_STUDENTID));
    }

    @Test
    public void parseStudentID_validValueWithoutWhitespace_returnsStudentID() {
        StudentID expectedStudentID = new StudentID(VALID_STUDENTID);
        assertEquals(expectedStudentID, assertDoesNotThrow(() -> ParserUtil.parseStudentID(VALID_STUDENTID)));
    }

    @Test
    public void parseStudentID_validValueWithWhitespace_returnsTrimmedStudentID() {
        String studentIdWithWhitespace = WHITESPACE + VALID_STUDENTID + WHITESPACE;
        StudentID expectedStudentID = new StudentID(VALID_STUDENTID);
        assertEquals(expectedStudentID, assertDoesNotThrow(() -> ParserUtil.parseStudentID(studentIdWithWhitespace)));
    }

    @Test
    public void parseTag_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTag(null));
    }

    @Test
    public void parseTag_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTag(INVALID_TAG));
    }

    @Test
    public void parseTag_validValueWithoutWhitespace_returnsTag() {
        Tag expectedTag = new Tag(VALID_TAG_1);
        assertEquals(expectedTag, assertDoesNotThrow(() -> ParserUtil.parseTag(VALID_TAG_1)));
    }

    @Test
    public void parseTag_validValueWithWhitespace_returnsTrimmedTag() {
        String tagWithWhitespace = WHITESPACE + VALID_TAG_1 + WHITESPACE;
        Tag expectedTag = new Tag(VALID_TAG_1);
        assertEquals(expectedTag, assertDoesNotThrow(() -> ParserUtil.parseTag(tagWithWhitespace)));
    }

    @Test
    public void parseTags_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTags(null));
    }

    @Test
    public void parseTags_collectionWithInvalidTags_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTags(Arrays.asList(VALID_TAG_1, INVALID_TAG)));
    }

    @Test
    public void parseTags_emptyCollection_returnsEmptySet() {
        assertTrue(assertDoesNotThrow(() -> ParserUtil.parseTags(Collections.emptyList())).isEmpty());
    }

    @Test
    public void parseTags_collectionWithValidTags_returnsTagSet() {
        Set<Tag> actualTagSet = assertDoesNotThrow(() -> ParserUtil.parseTags(Arrays.asList(VALID_TAG_1, VALID_TAG_2)));
        Set<Tag> expectedTagSet = new HashSet<Tag>(Arrays.asList(new Tag(VALID_TAG_1), new Tag(VALID_TAG_2)));

        assertEquals(expectedTagSet, actualTagSet);
    }
}
