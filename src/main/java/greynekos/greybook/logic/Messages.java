package greynekos.greybook.logic;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import greynekos.greybook.logic.parser.Prefix;
import greynekos.greybook.model.person.Person;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND =
            "Unknown command. Please use the help command to view all " + "available commands.";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format! \n%1$s";
    public static final String MESSAGE_MISSING_STUDENTID = "Indicated Student ID not found in the list: %1$s";
    public static final String MESSAGE_INVALID_PERSON_DISPLAYED_INDEX = "The person index provided is invalid";
    public static final String MESSAGE_PERSONS_LISTED_OVERVIEW = "%1$d persons listed!";
    public static final String MESSAGE_DUPLICATE_FIELDS =
            "Multiple values specified for the following single-valued field(s): ";
    public static final String MESSAGE_MUTUALLY_EXCLUSIVE_FIELDS =
            "Multiple values specified for the following mutually exclusive field(s): ";

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields = Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    public static String getErrorMessageForMutuallyExclusivePrefixes(Prefix... mutuallyExclusivePrefixes) {
        assert mutuallyExclusivePrefixes.length > 0;

        Set<String> mutuallyExclusiveFields =
                Stream.of(mutuallyExclusivePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return MESSAGE_MUTUALLY_EXCLUSIVE_FIELDS + String.join(" ", mutuallyExclusiveFields);
    }

    /**
     * Formats the {@code person} for display to the user.
     */
    public static String format(Person person) {
        final StringBuilder builder = new StringBuilder();
        builder.append(person.getName()).append("; Phone: ").append(person.getPhone()).append("; Email: ")
                .append(person.getEmail()).append("; StudentID: ").append(person.getStudentID()).append("; Tags: ");
        person.getTags().forEach(builder::append);
        return builder.toString();
    }

}
