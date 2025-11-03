package greynekos.greybook.model;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Predicate;

import greynekos.greybook.commons.core.GuiSettings;
import greynekos.greybook.model.person.AttendanceStatus;
import greynekos.greybook.model.person.Person;
import greynekos.greybook.model.person.StudentID;
import javafx.collections.ObservableList;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluate to true */
    Predicate<Person> PREDICATE_SHOW_ALL_PERSONS = unused -> true;

    /**
     * Replaces history data with the data in {@code history}.
     */
    void setHistory(ReadOnlyHistory history);

    /**
     * Returns the history.
     */
    ReadOnlyHistory getHistory();

    /**
     * Replaces user prefs data with the data in {@code userPrefs}.
     */
    void setUserPrefs(ReadOnlyUserPrefs userPrefs);

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Returns the user prefs' GreyBook file path.
     */
    Path getGreyBookFilePath();

    /**
     * Sets the user prefs' GreyBook file path.
     */
    void setGreyBookFilePath(Path greyBookFilePath);

    /**
     * Replaces GreyBook data with the data in {@code greyBook}.
     */
    void setGreyBook(ReadOnlyGreyBook greyBook);

    /** Returns the GreyBook */
    ReadOnlyGreyBook getGreyBook();

    /**
     * Returns true if a person with the same identity as {@code person} exists in
     * the GreyBook.
     */
    boolean hasPerson(Person person);

    /**
     * Deletes the given person. The person must exist in the GreyBook.
     */
    void deletePerson(Person target);

    /**
     * Adds the given person. {@code person} must not already exist in the greybook
     * book.
     */
    void addPerson(Person person);

    /**
     * Replaces the given person {@code target} with {@code editedPerson}.
     * {@code target} must exist in the GreyBook. The person identity of
     * {@code editedPerson} must not be the same as another existing person in the
     * GreyBook.
     */
    void setPerson(Person target, Person editedPerson);

    /**
     * Adds an {@code attendanceStatus} for the given person {@code target}.
     * {@code target} must exist in the GreyBook. The person identity of GreyBook.
     */
    void markPerson(Person target, AttendanceStatus.Status attendanceStatus);

    /**
     * Removes the attendance status of the given person {@code target}.
     * {@code target} must exist in the GreyBook. The person identity of GreyBook.
     */
    void unmarkPerson(Person target);

    /**
     * Returns the person with the given student ID, if they exist in the greybook
     * book.
     */
    Optional<Person> getPersonByStudentId(StudentID studentId);

    /** Returns an unmodifiable view of the filtered person list */
    ObservableList<Person> getFilteredPersonList();

    /**
     * Updates the filter of the filtered person list to filter by the given
     * {@code predicate}.
     *
     * @throws NullPointerException
     *             if {@code predicate} is null.
     */
    void updateFilteredPersonList(Predicate<Person> predicate);
}
