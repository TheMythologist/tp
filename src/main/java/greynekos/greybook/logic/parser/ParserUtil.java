package greynekos.greybook.logic.parser;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import greynekos.greybook.commons.core.index.Index;
import greynekos.greybook.commons.util.StringUtil;
import greynekos.greybook.logic.parser.exceptions.ParseException;
import greynekos.greybook.model.person.Email;
import greynekos.greybook.model.person.Name;
import greynekos.greybook.model.person.PersonIdentifier;
import greynekos.greybook.model.person.Phone;
import greynekos.greybook.model.person.StudentID;
import greynekos.greybook.model.tag.Tag;

/**
 * Contains utility methods used for parsing strings in the various *Parser
 * classes.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX = "Index is not a non-zero unsigned integer.";
    public static final String MESSAGE_INDEX_TOO_LARGE = "Index is too large";

    public static final String MESSAGE_INVALID_PERSON_IDENTIFIER =
            "Person identifier is invalid. It should be either a positive integer index or a valid Student ID "
                    + "(format: A0000000Y).";

    private static final String VALID_NUMBER_REGEX = "0|[1-9]\\d*";

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading
     * and trailing whitespaces will be trimmed.
     *
     * @throws ParseException
     *             if the specified index is invalid (not non-zero unsigned
     *             integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * Parses {@code input} as a delete identifier - tries to parse as Index first,
     * then as StudentID. Returns a {@link PersonIdentifier} if it's valid as either
     * format. Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException
     *             if the input is neither a valid index nor a valid student ID.
     */
    public static PersonIdentifier parsePersonIdentifier(String input) throws ParseException {
        requireNonNull(input);
        String trimmed = input.trim();

        if (StringUtil.isNonZeroUnsignedInteger(trimmed)) {
            return parseIndex(trimmed);
        }

        if (trimmed.matches(VALID_NUMBER_REGEX)) {
            throw new ParseException(MESSAGE_INDEX_TOO_LARGE);
        }

        if (StudentID.isValidStudentID(trimmed.toUpperCase())) {
            return parseStudentID(trimmed);
        }

        throw new ParseException(MESSAGE_INVALID_PERSON_IDENTIFIER);
    }

    /**
     * Parses a {@code String name} into a {@code Name}. Leading and trailing
     * whitespaces will be trimmed.
     *
     * @throws ParseException
     *             if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!Name.isValidName(trimmedName)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(trimmedName);
    }

    /**
     * Parses a {@code String phone} into a {@code Phone}. Leading and trailing
     * whitespaces will be trimmed.
     *
     * @throws ParseException
     *             if the given {@code phone} is invalid.
     */
    public static Phone parsePhone(String phone) throws ParseException {
        requireNonNull(phone);
        String trimmedPhone = phone.trim();
        if (!Phone.isValidPhone(trimmedPhone)) {
            throw new ParseException(Phone.MESSAGE_CONSTRAINTS);
        }
        return new Phone(trimmedPhone);
    }

    /**
     * Parses a {@code String email} into an {@code Email}. Leading and trailing
     * whitespaces will be trimmed.
     *
     * @throws ParseException
     *             if the given {@code email} is invalid.
     */
    public static Email parseEmail(String email) throws ParseException {
        requireNonNull(email);
        String trimmedEmail = email.trim();
        if (!Email.isValidEmail(trimmedEmail)) {
            throw new ParseException(Email.MESSAGE_CONSTRAINTS);
        }
        return new Email(trimmedEmail);
    }

    /**
     * Parses a {@code String studentID} into a {@code StudentID}. Leading and
     * trailing whitespaces will be trimmed.
     *
     * @throws ParseException
     *             if the given {@code studentID} is invalid.
     */
    public static StudentID parseStudentID(String studentID) throws ParseException {
        requireNonNull(studentID);
        // Always convert studentID to upper case
        String trimmedStudentID = studentID.trim().toUpperCase();
        if (!StudentID.isValidStudentID(trimmedStudentID)) {
            throw new ParseException(StudentID.MESSAGE_CONSTRAINTS);
        }
        return new StudentID(trimmedStudentID);
    }

    /**
     * Parses a {@code String tag} into a {@code Tag}. Leading and trailing
     * whitespaces will be trimmed.
     *
     * @throws ParseException
     *             if the given {@code tag} is invalid.
     */
    public static Tag parseTag(String tag) throws ParseException {
        requireNonNull(tag);
        String trimmedTag = tag.trim();
        if (!Tag.isValidTagName(trimmedTag)) {
            throw new ParseException(Tag.MESSAGE_CONSTRAINTS);
        }
        return new Tag(trimmedTag);
    }

    /**
     * Parses a {@code String tag} into a {@code Tag}. Leading and trailing
     * whitespaces will be trimmed. Return an empty tag if the input string is
     * empty.
     *
     * @throws ParseException
     *             if the given {@code tag} is invalid.
     */
    public static Tag parseTagAllowEmpty(String tag) throws ParseException {
        requireNonNull(tag);
        String trimmedTag = tag.trim();
        if (trimmedTag.isEmpty()) {
            return new Tag();
        }
        if (!Tag.isValidTagName(trimmedTag)) {
            throw new ParseException(Tag.MESSAGE_CONSTRAINTS);
        }
        return new Tag(trimmedTag);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}.
     */
    public static Set<Tag> parseTags(Collection<String> tags) throws ParseException {
        requireNonNull(tags);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(parseTag(tagName));
        }
        return tagSet;
    }
}
