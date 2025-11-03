package greynekos.greybook.logic.parser;

import static greynekos.greybook.commons.util.CollectionUtil.requireAllNonNull;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import greynekos.greybook.commons.core.index.Index;
import greynekos.greybook.commons.util.StringUtil;
import greynekos.greybook.logic.parser.commandoption.OptionalSinglePreambleOption;
import greynekos.greybook.logic.parser.commandoption.ZeroOrMorePrefixOption;
import greynekos.greybook.logic.parser.exceptions.ParseException;
import greynekos.greybook.model.person.All;
import greynekos.greybook.model.person.Email;
import greynekos.greybook.model.person.Name;
import greynekos.greybook.model.person.PersonIdentifier;
import greynekos.greybook.model.person.PersonIdentifierOrAll;
import greynekos.greybook.model.person.Phone;
import greynekos.greybook.model.person.StudentID;
import greynekos.greybook.model.tag.Tag;

/**
 * Contains utility methods used for parsing strings in the various *Parser
 * classes.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX = "Index is not a non-zero unsigned integer.";

    public static final String MESSAGE_INVALID_PERSON_IDENTIFIER =
            "Person identifier is invalid. It should be either a positive integer index or a valid Student ID "
                    + "(format: A0000000Y).";

    public static final String VALID_NUMBER_REGEX = "\\d+";

    public static final String MESSAGE_INVALID_PERSON_IDENTIFIER_OR_ALL =
            "Person identifier or \"all\" keyword is invalid. It should be either a positive integer index,"
                    + " a valid Student ID (format: A0000000Y), or the keyword \"all\".";

    /**
     * Record class used by Find command.
     */
    public record KeywordsIdAndTagFrags(List<String> keywords, List<String> idFrags, List<String> tagFrags) {
    }

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
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }

        if (StudentID.isValidStudentID(trimmed.toUpperCase())) {
            return parseStudentID(trimmed);
        }

        throw new ParseException(MESSAGE_INVALID_PERSON_IDENTIFIER);
    }

    /**
     * Parses {@code input} as an unmark identifier - tries to parse as {@code All}
     * first, then {@code Index}, then as {@code StudentID}. Returns a
     * {@link PersonIdentifierOrAll} if it's valid as either format. Leading and
     * trailing whitespaces will be trimmed.
     *
     * @throws ParseException
     *             if the input is neither {@code All.ALL_KEYWORD}, a valid index
     *             nor a valid student ID.
     */
    public static PersonIdentifierOrAll parsePersonIdentifierOrAll(String input) throws ParseException {
        requireNonNull(input);
        String trimmed = input.trim();

        if (trimmed.equals(All.ALL_KEYWORD)) {
            return new All();
        }
        try {
            return parsePersonIdentifier(input);
        } catch (ParseException pe) {
            throw new ParseException(MESSAGE_INVALID_PERSON_IDENTIFIER_OR_ALL);
        }
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

    /**
     * Parses keywords (from preamble) and student ID fragments (from i/ values)
     * from {@code arg}. Returns an immutable container (record) holding both lists.
     */
    public static KeywordsIdAndTagFrags parseKeywordsAndIdFrags(ArgumentParseResult arg,
            OptionalSinglePreambleOption<String> preambleOption,
            ZeroOrMorePrefixOption<String> studentIdFragmentsOption,
            ZeroOrMorePrefixOption<String> tagsFragmentsOption) {

        requireAllNonNull(arg, preambleOption, studentIdFragmentsOption, tagsFragmentsOption);

        List<String> keywords = new ArrayList<>();
        List<String> idFrags = new ArrayList<>();
        List<String> tagFrags = new ArrayList<>();

        arg.getOptionalValue(preambleOption).map(
                s -> Arrays.stream(s.trim().split("\\s+")).filter(tok -> !tok.isBlank()).collect(Collectors.toList()))
                .ifPresent(keywords::addAll);

        for (String raw : arg.getAllValues(studentIdFragmentsOption)) {
            if (raw == null) {
                continue;
            }
            String trimmed = raw.trim();
            if (trimmed.isEmpty()) {
                continue;
            }

            String[] parts = trimmed.split("\\s+");
            idFrags.add(parts[0]);
            for (int i = 1; i < parts.length; i++) {
                String tok = parts[i];
                if (!tok.isBlank()) {
                    keywords.add(tok);
                }
            }
        }

        for (String raw : arg.getAllValues(tagsFragmentsOption)) {
            if (raw == null || raw.trim().isEmpty()) {
                continue;
            }
            String[] parts = raw.trim().split("\\s+");
            tagFrags.add(parts[0]);
            for (int i = 1; i < parts.length; i++) {
                String tok = parts[i];
                if (!tok.isBlank()) {
                    keywords.add(tok);
                }
            }
        }
        return new KeywordsIdAndTagFrags(List.copyOf(keywords), List.copyOf(idFrags), List.copyOf(tagFrags));
    }
}
