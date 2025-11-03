package greynekos.greybook.model.person;

import static greynekos.greybook.testutil.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class PhoneTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Phone(null));
    }

    @Test
    public void constructor_invalidPhone_throwsIllegalArgumentException() {
        String invalidPhone = "";
        assertThrows(IllegalArgumentException.class, () -> new Phone(invalidPhone));
    }

    @Test
    public void isValidPhone() {
        // null phone number
        assertThrows(NullPointerException.class, () -> Phone.isValidPhone(null));

        // invalid phone numbers
        assertFalse(Phone.isValidPhone("")); // empty string
        assertFalse(Phone.isValidPhone(" ")); // spaces only
        assertFalse(Phone.isValidPhone("91")); // less than 3 numbers
        assertFalse(Phone.isValidPhone("phone")); // non-numeric
        assertFalse(Phone.isValidPhone("9011p041")); // alphabets within digits
        assertFalse(Phone.isValidPhone("9312 1534")); // spaces within digits
        assertFalse(Phone.isValidPhone("1234567")); // 7 digits (not 8)
        assertFalse(Phone.isValidPhone("123456789")); // 9 digits (not 8)
        assertFalse(Phone.isValidPhone("12345678901")); // 11 digits without +
        assertFalse(Phone.isValidPhone("+1")); // international format but only 1 digits
        assertFalse(Phone.isValidPhone("+1234567890123456")); // international format but more than 15 digits
        assertFalse(Phone.isValidPhone("+")); // just plus sign
        assertFalse(Phone.isValidPhone("+abc123")); // plus sign with non-numeric

        // valid phone numbers - Singapore format (8 digits)
        assertTrue(Phone.isValidPhone("91234567")); // exactly 8 digits
        assertTrue(Phone.isValidPhone("93121534")); // exactly 8 digits
        assertTrue(Phone.isValidPhone("87654321")); // exactly 8 digits

        // valid phone numbers - International format (+ followed by 3-15 digits)
        assertTrue(Phone.isValidPhone("+123")); // minimum 3 digits
        assertTrue(Phone.isValidPhone("+6591234567")); // Singapore with country code
        assertTrue(Phone.isValidPhone("+12025551234")); // US number
        assertTrue(Phone.isValidPhone("+441234567890")); // UK number
        assertTrue(Phone.isValidPhone("+123456789012345")); // maximum 15 digits
    }

    @Test
    public void equals() {
        Phone phone = new Phone("91234567");

        // same values -> returns true
        assertTrue(phone.equals(new Phone("91234567")));

        // same object -> returns true
        assertTrue(phone.equals(phone));

        // null -> returns false
        assertFalse(phone.equals(null));

        // different types -> returns false
        assertFalse(phone.equals(5.0f));

        // different values -> returns false
        assertFalse(phone.equals(new Phone("87654321")));
    }
}
