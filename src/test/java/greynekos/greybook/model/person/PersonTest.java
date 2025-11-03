package greynekos.greybook.model.person;

import static greynekos.greybook.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static greynekos.greybook.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static greynekos.greybook.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static greynekos.greybook.logic.commands.CommandTestUtil.VALID_STUDENTID_BOB;
import static greynekos.greybook.logic.commands.CommandTestUtil.VALID_TAG_CONTRIBUTOR;
import static greynekos.greybook.testutil.Assert.assertThrows;
import static greynekos.greybook.testutil.TypicalPersons.ALICE;
import static greynekos.greybook.testutil.TypicalPersons.BOB;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import greynekos.greybook.model.person.AttendanceStatus.Status;
import greynekos.greybook.testutil.PersonBuilder;

public class PersonTest {

    @Test
    public void asObservableList_modifyList_throwsUnsupportedOperationException() {
        Person person = new PersonBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> person.getTags().remove(0));
    }

    @Test
    public void isSamePerson() {
        // same object -> returns true
        assertTrue(ALICE.isSamePerson(ALICE));

        // null -> returns false
        assertFalse(ALICE.isSamePerson(null));

        // same name and studentId, all other attributes different -> returns true
        Person editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_BOB)
                .withTags(VALID_TAG_CONTRIBUTOR).withAttendanceStatus(Status.LATE).build();
        assertTrue(ALICE.isSamePerson(editedAlice));

        // different studentId, all other attributes same -> returns false
        editedAlice = new PersonBuilder(ALICE).withStudentID(VALID_STUDENTID_BOB).build();
        assertFalse(ALICE.isSamePerson(editedAlice));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Person aliceCopy = new PersonBuilder(ALICE).build();
        assertTrue(ALICE.equals(aliceCopy));

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different person -> returns false
        assertFalse(ALICE.equals(BOB));

        // different name -> returns false
        Person editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different phone -> returns false
        editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different email -> returns false
        editedAlice = new PersonBuilder(ALICE).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different studentID -> returns false
        editedAlice = new PersonBuilder(ALICE).withStudentID(VALID_STUDENTID_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different tags -> returns false
        editedAlice = new PersonBuilder(ALICE).withTags(VALID_TAG_CONTRIBUTOR).build();
        assertFalse(ALICE.equals(editedAlice));

        // different attendanceStatus -> returns false
        editedAlice = new PersonBuilder(ALICE).withAttendanceStatus(Status.LATE).build();
        assertFalse(ALICE.equals(editedAlice));
    }

    @Test
    public void toStringMethod() {
        String expected = Person.class.getCanonicalName() + "{name=" + ALICE.getName() + ", phone=" + ALICE.getPhone()
                + ", email=" + ALICE.getEmail() + ", studentId=" + ALICE.getStudentID() + ", tags=" + ALICE.getTags()
                + ", attendanceStatus=" + ALICE.getAttendance() + "}";
        assertEquals(expected, ALICE.toString());
    }
}
