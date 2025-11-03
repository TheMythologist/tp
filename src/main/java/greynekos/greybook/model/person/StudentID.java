package greynekos.greybook.model.person;

import static greynekos.greybook.commons.util.AppUtil.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * Represents a Person's student ID in the GreyBook. Guarantees: immutable; is
 * valid as declared in {@link #isValidStudentID(String)}
 */
public class StudentID implements PersonIdentifier {

    public static final String MESSAGE_CONSTRAINTS =
            "Student IDs must be a valid NUS Student ID with a matching checksum";
    public static final String VALIDATION_REGEX = "^(?:A\\d{7}|U\\d{6,7})[YXWURNMLJHEAB]$";
    public static final String VALID_CHECKSUMS = "YXWURNMLJHEAB";
    public final String value;

    /**
     * Constructs a {@code StudentID}.
     *
     * @param studentID
     *            A valid student ID.
     */
    public StudentID(String studentID) {
        requireNonNull(studentID);
        checkArgument(isValidStudentID(studentID), MESSAGE_CONSTRAINTS);
        value = studentID;
    }

    /**
     * Returns true if a given string is a valid student ID.
     */
    public static boolean isValidStudentID(String test) {
        if (!test.matches(VALIDATION_REGEX)) {
            return false;
        }
        return isValidStudentIdChecksum(test);
    }

    /**
     * Calculates the checksum character, given the student ID digits.
     *
     * @param test
     *            the student ID string (without checksum character)
     * @return the calculated checksum character
     * @throws IllegalArgumentException
     *             if the input is invalid
     */
    public static char calculateStudentIdChecksum(String test) {
        requireNonNull(test);
        if (test.length() != 7 && test.length() != 8) {
            throw new IllegalArgumentException("Student ID must be exactly 8 or 9 characters long");
        }

        int[] weights;
        if (test.charAt(0) == 'U') {
            weights = new int[]{
                0, 1, 3, 1, 2, 7
            };
        } else { // 'A'
            weights = new int[]{
                1, 1, 1, 1, 1, 1
            };
        }

        String digits = test.substring(test.length() - 6);
        int sum = 0;

        for (int i = 0; i < 6; i++) {
            int digit = Character.getNumericValue(digits.charAt(i));
            sum += weights[i] * digit;
        }

        return VALID_CHECKSUMS.charAt(sum % 13);
    }

    /**
     * Returns true if a given student ID checksum is valid.
     */
    private static boolean isValidStudentIdChecksum(String test) {
        int checksumIndex = test.length() - 1;
        String values = test.substring(0, checksumIndex);
        char checksum = test.charAt(checksumIndex);

        // Discard 3rd digit from U-prefixed NUSNET ID (e.g., U1x45678 → U145678)
        if (values.charAt(0) == 'U' && values.length() == 8) {
            values = values.substring(0, 3) + values.substring(4);
        }

        return checksum == calculateStudentIdChecksum(values);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof StudentID)) {
            return false;
        }

        StudentID otherStudentID = (StudentID) other;
        return value.equals(otherStudentID.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
