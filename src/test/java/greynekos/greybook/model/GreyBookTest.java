package greynekos.greybook.model;

import static greynekos.greybook.logic.commands.CommandTestUtil.VALID_TAG_CONTRIBUTOR;
import static greynekos.greybook.testutil.Assert.assertThrows;
import static greynekos.greybook.testutil.TypicalPersons.ALICE;
import static greynekos.greybook.testutil.TypicalPersons.getTypicalGreyBook;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import greynekos.greybook.model.person.Person;
import greynekos.greybook.model.person.exceptions.DuplicatePersonException;
import greynekos.greybook.testutil.PersonBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class GreyBookTest {

    private final GreyBook greyBook = new GreyBook();

    @Test
    public void constructor() {
        assertEquals(Collections.emptyList(), greyBook.getPersonList());
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> greyBook.resetData(null));
    }

    @Test
    public void resetData_withValidReadOnlyGreyBook_replacesData() {
        GreyBook newData = getTypicalGreyBook();
        greyBook.resetData(newData);
        assertEquals(newData, greyBook);
    }

    @Test
    public void resetData_withDuplicatePersons_throwsDuplicatePersonException() {
        // Two persons with the same identity fields
        Person editedAlice = new PersonBuilder(ALICE).withTags(VALID_TAG_CONTRIBUTOR).build();
        List<Person> newPersons = Arrays.asList(ALICE, editedAlice);
        GreyBookStub newData = new GreyBookStub(newPersons);

        assertThrows(DuplicatePersonException.class, () -> greyBook.resetData(newData));
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> greyBook.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInGreyBook_returnsFalse() {
        assertFalse(greyBook.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInGreyBook_returnsTrue() {
        greyBook.addPerson(ALICE);
        assertTrue(greyBook.hasPerson(ALICE));
    }

    @Test
    public void getPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> greyBook.getPersonList().remove(0));
    }

    @Test
    public void toStringMethod() {
        String expected = GreyBook.class.getCanonicalName() + "{persons=" + greyBook.getPersonList() + "}";
        assertEquals(expected, greyBook.toString());
    }

    /**
     * A stub ReadOnlyGreyBook whose persons list can violate interface constraints.
     */
    private static class GreyBookStub implements ReadOnlyGreyBook {
        private final ObservableList<Person> persons = FXCollections.observableArrayList();

        GreyBookStub(Collection<Person> persons) {
            this.persons.setAll(persons);
        }

        @Override
        public ObservableList<Person> getPersonList() {
            return persons;
        }
    }

}
