package greynekos.greybook.model.util;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import greynekos.greybook.model.GreyBook;
import greynekos.greybook.model.ReadOnlyGreyBook;
import greynekos.greybook.model.person.Email;
import greynekos.greybook.model.person.Name;
import greynekos.greybook.model.person.Person;
import greynekos.greybook.model.person.Phone;
import greynekos.greybook.model.person.StudentID;
import greynekos.greybook.model.tag.Tag;

/**
 * Contains utility methods for populating {@code GreyBook} with sample data.
 */
public class SampleDataUtil {
    public static Person[] getSamplePersons() {
        return new Person[]{
            new Person(new Name("Alex Yeoh"), new Phone("87438807"), new Email("alexyeoh@example.com"),
                    new StudentID("A1234567X"), getTagSet("member")),
            new Person(new Name("Bernice Yu"), new Phone("99272758"), new Email("berniceyu@example.com"),
                    new StudentID("A2345678L"), getTagSet("contributor", "member")),
            new Person(new Name("Charlotte Oliveiro"), new Phone("93210283"), new Email("charlotte@example.com"),
                    new StudentID("A3456789Y"), getTagSet("president")),
            new Person(new Name("David Li"), new Phone("91031282"), new Email("lidavid@example.com"),
                    new StudentID("A4567890H"), getTagSet("exco")),
            new Person(new Name("Irfan Ibrahim"), new Phone("92492021"), new Email("irfan@example.com"),
                    new StudentID("A5678901N"), getTagSet()),
            new Person(new Name("Roy Balakrishnan"), new Phone("92624417"), new Email("royb@example.com"),
                    new StudentID("A6789012X"), getTagSet("contributor")),
        };
    }

    public static ReadOnlyGreyBook getSampleGreyBook() {
        GreyBook sampleAb = new GreyBook();
        for (Person samplePerson : getSamplePersons()) {
            sampleAb.addPerson(samplePerson);
        }
        return sampleAb;
    }

    /**
     * Returns a tag set containing the list of strings given.
     */
    public static Set<Tag> getTagSet(String... strings) {
        return Arrays.stream(strings).map(Tag::new).collect(Collectors.toSet());
    }

}
