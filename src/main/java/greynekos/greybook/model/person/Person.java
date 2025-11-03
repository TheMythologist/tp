package greynekos.greybook.model.person;

import static greynekos.greybook.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import greynekos.greybook.commons.util.ToStringBuilder;
import greynekos.greybook.model.tag.Tag;

/**
 * Represents a Person in the GreyBook. Guarantees: details are present and not
 * null, field values are validated, immutable.
 */
public class Person {

    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Email email;
    private final StudentID studentID;

    // Data fields
    private final Set<Tag> tags = new HashSet<>();
    private final AttendanceStatus attendanceStatus;

    /**
     * Every field must be present and not null. Constructs with a person with an
     * unmarked attendance.
     */
    public Person(Name name, Phone phone, Email email, StudentID studentID, Set<Tag> tags) {
        requireAllNonNull(name, phone, email, tags, studentID);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.tags.addAll(tags);
        this.studentID = studentID;
        this.attendanceStatus = new AttendanceStatus();
    }

    /**
     * Every field must be present and not null.
     */
    public Person(Name name, Phone phone, Email email, StudentID studentID, Set<Tag> tags,
            AttendanceStatus attendanceStatus) {
        requireAllNonNull(name, phone, email, tags, studentID, attendanceStatus);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.tags.addAll(tags);
        this.studentID = studentID;
        this.attendanceStatus = attendanceStatus;
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    public StudentID getStudentID() {
        return studentID;
    }

    /**
     * Returns an immutable tag set, which throws
     * {@code UnsupportedOperationException} if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    public AttendanceStatus getAttendance() {
        return this.attendanceStatus;
    }

    /**
     * Returns true if both persons have the same name. This defines a weaker notion
     * of equality between two persons.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null && otherPerson.getStudentID().equals(getStudentID());
    }

    /**
     * Returns true if both persons have the same identity and data fields. This
     * defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Person)) {
            return false;
        }

        Person otherPerson = (Person) other;
        return name.equals(otherPerson.name) && phone.equals(otherPerson.phone) && email.equals(otherPerson.email)
                && tags.equals(otherPerson.tags) && studentID.equals(otherPerson.studentID)
                && attendanceStatus.equals(otherPerson.attendanceStatus);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, studentID, tags, attendanceStatus);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("name", name).add("phone", phone).add("email", email)
                .add("studentId", studentID).add("tags", tags).add("attendanceStatus", attendanceStatus).toString();
    }

}
