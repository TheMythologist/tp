package greynekos.greybook.logic.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import greynekos.greybook.logic.parser.exceptions.ParseException;

/**
 * Tokenizes arguments string of the form:
 * {@code preamble <prefix>value <prefix>value ...}<br>
 * e.g. {@code some preamble text t/ 11.00 t/12.00 k/ m/ July} where prefixes
 * are {@code t/ k/ m/}.<br>
 * 1. An argument's value can be an empty string e.g. the value of {@code k/} in
 * the above example.<br>
 * 2. Leading and trailing whitespaces of an argument value will be
 * discarded.<br>
 * 3. An argument may be repeated and all its values will be accumulated e.g.
 * the value of {@code t/} in the above example.<br>
 */
public class ArgumentTokenizer {

    /**
     * Tokenizes an arguments string and returns an {@code ArgumentMultimap} object
     * that maps prefixes to their respective argument values. Only the given
     * prefixes will be recognized in the arguments string.
     *
     * @param argsString
     *            Arguments string of the form:
     *            {@code preamble <prefix>value <prefix>value ...}
     * @param prefixes
     *            Prefixes to tokenize the arguments string with
     * @return ArgumentMultimap object that maps prefixes to their arguments
     *
     * @throws ParseException
     *             if there is an invalid escape sequence or a quote (") is not
     *             closed.
     */
    public static ArgumentMultimap tokenize(String argsString, Prefix... prefixes) throws ParseException {
        List<PrefixPosition> positions = findAllPrefixPositions(argsString, prefixes);
        return extractArguments(argsString, positions);
    }

    /**
     * Finds all zero-based prefix positions in the given arguments string.
     *
     * @param argsString
     *            Arguments string of the form:
     *            {@code preamble <prefix>value <prefix>value ...}
     * @param prefixes
     *            Prefixes to find in the arguments string
     * @return List of zero-based prefix positions in the given arguments string
     */
    private static List<PrefixPosition> findAllPrefixPositions(String argsString, Prefix... prefixes) {
        return Arrays.stream(prefixes).flatMap(prefix -> findPrefixPositions(argsString, prefix).stream())
                .collect(Collectors.toList());
    }

    /**
     * {@see findAllPrefixPositions}
     */
    private static List<PrefixPosition> findPrefixPositions(String argsString, Prefix prefix) {
        List<PrefixPosition> positions = new ArrayList<>();

        int prefixPosition = findPrefixPosition(argsString, prefix.getPrefix(), 0);
        while (prefixPosition != -1) {
            PrefixPosition extendedPrefix = new PrefixPosition(prefix, prefixPosition);
            positions.add(extendedPrefix);
            prefixPosition = findPrefixPosition(argsString, prefix.getPrefix(), prefixPosition);
        }

        return positions;
    }

    /**
     * Returns the index of the first occurrence of {@code prefix} in
     * {@code argsString} starting from index {@code fromIndex}. An occurrence is
     * valid if there is a whitespace before {@code prefix}, and the prefix is not
     * escaped by having surrounding quotes. Returns -1 if no such occurrence can be
     * found.
     *
     * E.g if {@code argsString} = "e/hip/900", {@code prefix} = "p/" and
     * {@code fromIndex} = 0, this method returns -1 as there are no valid
     * occurrences of "p/" with whitespace before it. However, if {@code argsString}
     * = "e/hi p/900", {@code prefix} = "p/" and {@code fromIndex} = 0, this method
     * returns 5.
     */
    private static int findPrefixPosition(String argsString, String prefix, int fromIndex) {
        boolean isInQuotes = false;
        for (int i = fromIndex; i < argsString.length(); i++) {
            char c = argsString.charAt(i);

            if (c == '"') {
                int backslashes = 0;
                int j = i - 1;
                while (j >= 0 && argsString.charAt(j) == '\\') {
                    backslashes += 1;
                    j--;
                }

                if (backslashes % 2 == 0) {
                    isInQuotes = !isInQuotes;
                }
            }

            if (!isInQuotes && c == ' ' && argsString.startsWith(prefix, i + 1)) {
                return i + 1;
            }
        }

        return -1;
    }

    /**
     * Extracts prefixes and their argument values, and returns an
     * {@code ArgumentMultimap} object that maps the extracted prefixes to their
     * respective arguments. Prefixes are extracted based on their zero-based
     * positions in {@code argsString}.
     *
     * @param argsString
     *            Arguments string of the form:
     *            {@code preamble <prefix>value <prefix>value ...}
     * @param prefixPositions
     *            Zero-based positions of all prefixes in {@code argsString}
     * @return ArgumentMultimap object that maps prefixes to their arguments
     */
    private static ArgumentMultimap extractArguments(String argsString, List<PrefixPosition> prefixPositions)
            throws ParseException {

        // Sort by start position
        prefixPositions.sort((prefix1, prefix2) -> prefix1.getStartPosition() - prefix2.getStartPosition());

        // Insert a PrefixPosition to represent the preamble
        PrefixPosition preambleMarker = new PrefixPosition(new Preamble(), 0);
        prefixPositions.add(0, preambleMarker);

        // Add a dummy PrefixPosition to represent the end of the string
        PrefixPosition endPositionMarker = new PrefixPosition(new Preamble(), argsString.length());
        prefixPositions.add(endPositionMarker);

        // Map prefixes to their argument values (if any)
        ArgumentMultimap argMultimap = new ArgumentMultimap();
        for (int i = 0; i < prefixPositions.size() - 1; i++) {
            // Extract and store prefixes and their arguments
            Prefix argPrefix = prefixPositions.get(i).getPrefix();
            String argValue = extractArgumentValue(argsString, prefixPositions.get(i), prefixPositions.get(i + 1));
            argMultimap.put(argPrefix, argValue);
        }

        return argMultimap;
    }

    /**
     * Returns the trimmed value of the argument in the arguments string specified
     * by {@code currentPrefixPosition}. The end position of the value is determined
     * by {@code nextPrefixPosition}. Also transforms escape characters into the
     * character they are representing
     *
     * @throws ParseException
     *             if there is an invalid escape sequence or a " is not closed.
     */
    private static String extractArgumentValue(String argsString, PrefixPosition currentPrefixPosition,
            PrefixPosition nextPrefixPosition) throws ParseException {
        Prefix prefix = currentPrefixPosition.getPrefix();

        int valueStartPos = currentPrefixPosition.getStartPosition() + prefix.getPrefix().length();
        String value = argsString.substring(valueStartPos, nextPrefixPosition.getStartPosition()).trim();

        StringBuilder escapedValue = new StringBuilder();
        boolean isBackslash = false;
        int startOfQuotes = -1;

        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);

            if (c != '"' && c != '\\') {
                if (isBackslash) {
                    throw new ParseException("Invalid escape sequence");
                }
                escapedValue.append(c);
            }

            if (c == '\\') {
                if (isBackslash) {
                    escapedValue.append(c);
                }

                isBackslash = !isBackslash;
            }

            if (c == '"') {
                if (isBackslash) {
                    escapedValue.append(c);
                    isBackslash = false;
                } else if (startOfQuotes == -1) {
                    startOfQuotes = i;
                } else {
                    startOfQuotes = -1;
                }
            }
        }

        if (startOfQuotes != -1) {
            throw new ParseException("Expected \" at end of argument");
        }

        return escapedValue.toString();
    }

    /**
     * Represents a prefix's position in an arguments string.
     */
    private static class PrefixPosition {
        private int startPosition;
        private final Prefix prefix;

        PrefixPosition(Prefix prefix, int startPosition) {
            this.prefix = prefix;
            this.startPosition = startPosition;
        }

        int getStartPosition() {
            return startPosition;
        }

        Prefix getPrefix() {
            return prefix;
        }
    }

}
