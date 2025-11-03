package greynekos.greybook.testutil;

import static greynekos.greybook.logic.commands.CommandTestUtil.VALID_EMAIL_AMY;
import static greynekos.greybook.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static greynekos.greybook.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static greynekos.greybook.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static greynekos.greybook.logic.commands.CommandTestUtil.VALID_PHONE_AMY;
import static greynekos.greybook.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static greynekos.greybook.logic.commands.CommandTestUtil.VALID_STUDENTID_AMY;
import static greynekos.greybook.logic.commands.CommandTestUtil.VALID_STUDENTID_BOB;
import static greynekos.greybook.logic.commands.CommandTestUtil.VALID_TAG_CONTRIBUTOR;
import static greynekos.greybook.logic.commands.CommandTestUtil.VALID_TAG_MEMBER;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import greynekos.greybook.model.GreyBook;
import greynekos.greybook.model.person.AttendanceStatus;
import greynekos.greybook.model.person.Person;

/**
 * A utility class containing a list of {@code Person} objects to be used in
 * tests.
 */
public class TypicalPersons {

    public static final Person ALICE = new PersonBuilder().withName("Alice Pauline").withEmail("alice@example.com")
            .withPhone("94351253").withStudentID("A1234567X").withTags("member")
            .withAttendanceStatus(AttendanceStatus.Status.PRESENT).build();
    public static final Person BENSON = new PersonBuilder().withName("Benson Meier").withEmail("johnd@example.com")
            .withPhone("98765432").withStudentID("A2345678L").withTags("owesMoney", "member")
            .withAttendanceStatus(AttendanceStatus.Status.NONE).build();
    public static final Person CARL =
            new PersonBuilder().withName("Carl Kurz").withPhone("95352563").withEmail("heinz@example.com")
                    .withStudentID("A3456789Y").withAttendanceStatus(AttendanceStatus.Status.NONE).build();
    public static final Person DANIEL = new PersonBuilder().withName("Daniel Meier").withPhone("87652533")
            .withEmail("cornelia@example.com").withStudentID("A4567890H").withTags("member")
            .withAttendanceStatus(AttendanceStatus.Status.ABSENT).build();
    public static final Person ELLE =
            new PersonBuilder().withName("Elle Meyer").withPhone("94822249").withEmail("werner@example.com")
                    .withStudentID("A5678901N").withAttendanceStatus(AttendanceStatus.Status.LATE).build();
    public static final Person FIONA =
            new PersonBuilder().withName("Fiona Kunz").withPhone("94824277").withEmail("lydia@example.com")
                    .withStudentID("A6789012X").withAttendanceStatus(AttendanceStatus.Status.NONE).build();
    public static final Person GEORGE =
            new PersonBuilder().withName("George Best").withPhone("94824428").withEmail("anna@example.com")
                    .withStudentID("A7890123E").withAttendanceStatus(AttendanceStatus.Status.EXCUSED).build();

    // Manually added
    public static final Person HOON =
            new PersonBuilder().withName("Hoon Meier").withPhone("84824248").withEmail("stefan@example.com")
                    .withStudentID("A8901234M").withAttendanceStatus(AttendanceStatus.Status.LATE).build();
    public static final Person IDA =
            new PersonBuilder().withName("Ida Mueller").withPhone("84821318").withEmail("hans@example.com")
                    .withStudentID("A9012345W").withAttendanceStatus(AttendanceStatus.Status.ABSENT).build();

    // Manually added - Person's details found in {@code CommandTestUtil}
    public static final Person AMY = new PersonBuilder().withName(VALID_NAME_AMY).withPhone(VALID_PHONE_AMY)
            .withEmail(VALID_EMAIL_AMY).withStudentID(VALID_STUDENTID_AMY).withTags(VALID_TAG_MEMBER)
            .withAttendanceStatus(AttendanceStatus.Status.NONE).build();
    public static final Person BOB =
            new PersonBuilder().withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_BOB)
                    .withStudentID(VALID_STUDENTID_BOB).withTags(VALID_TAG_CONTRIBUTOR, VALID_TAG_MEMBER)
                    .withAttendanceStatus(AttendanceStatus.Status.NONE).build();

    public static final String KEYWORD_MATCHING_MEIER = "Meier"; // A keyword that matches MEIER

    private TypicalPersons() {
    } // prevents instantiation

    /**
     * Returns an {@code GreyBook} with all the typical persons.
     */
    public static GreyBook getTypicalGreyBook() {
        GreyBook ab = new GreyBook();
        for (Person person : getTypicalPersons()) {
            ab.addPerson(person);
        }
        return ab;
    }

    public static List<Person> getTypicalPersons() {
        return new ArrayList<>(Arrays.asList(ALICE, BENSON, CARL, DANIEL, ELLE, FIONA, GEORGE));
    }
}
