package greynekos.greybook.commons.util;

import static greynekos.greybook.commons.util.AppUtil.checkArgument;
import static java.util.Objects.requireNonNull;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Helper functions for handling strings.
 */
public class StringUtil {

    /**
     * Returns true if {@code sentence} contains {@code term} as a substring,
     * ignoring case. This does NOT require a full word match.
     * <p>
     * Examples:
     *
     * <pre>
     *   containsSubstringIgnoreCase("ABc def", "abc") == true
     *   containsSubstringIgnoreCase("ABc def", "DEF") == true
     *   containsSubstringIgnoreCase("ABc def", "AB")  == true
     * </pre>
     *
     * @param sentence
     *            cannot be null
     * @param term
     *            cannot be null, cannot be empty, must be a single word
     */
    public static boolean containsSubstringIgnoreCase(String sentence, String term) {
        requireNonNull(sentence);
        requireNonNull(term);

        String preppedTerm = term.trim();
        checkArgument(!preppedTerm.isEmpty(), "Word parameter cannot be empty");
        checkArgument(preppedTerm.split("\\s+").length == 1, "Word parameter should be a single word");

        return sentence.toLowerCase().contains(preppedTerm.toLowerCase());
    }

    /**
     * Returns a detailed message of the t, including the stack trace.
     */
    public static String getDetails(Throwable t) {
        requireNonNull(t);
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return t.getMessage() + "\n" + sw.toString();
    }

    /**
     * Returns true if {@code s} represents a non-zero unsigned integer e.g. 1, 2,
     * 3, ..., {@code Integer.MAX_VALUE} <br>
     * Will return false for any other non-null string input e.g. empty string,
     * "-1", "0", "+1", and " 2 " (untrimmed), "3 0" (contains whitespace), "1 a"
     * (contains letters)
     *
     * @throws NullPointerException
     *             if {@code s} is null.
     */
    public static boolean isNonZeroUnsignedInteger(String s) {
        requireNonNull(s);

        try {
            int value = Integer.parseInt(s);
            return value > 0 && !s.startsWith("+"); // "+1" is successfully parsed by Integer#parseInt(String)
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}
