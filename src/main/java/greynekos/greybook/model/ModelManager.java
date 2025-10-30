package greynekos.greybook.model;

import static greynekos.greybook.commons.util.CollectionUtil.requireAllNonNull;
import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;

import greynekos.greybook.commons.core.GuiSettings;
import greynekos.greybook.commons.core.LogsCenter;
import greynekos.greybook.model.person.AttendanceStatus;
import greynekos.greybook.model.person.Person;
import greynekos.greybook.model.person.StudentID;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

/**
 * Represents the in-memory model of the GreyBook data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final GreyBook greyBook;
    private final UserPrefs userPrefs;
    private final History history;
    private final FilteredList<Person> filteredPersons;

    /**
     * Initializes a ModelManager with the given greyBook and userPrefs.
     */
    public ModelManager(ReadOnlyGreyBook greyBook, ReadOnlyUserPrefs userPrefs, ReadOnlyHistory history) {
        requireAllNonNull(greyBook, userPrefs);

        logger.fine("Initializing with GreyBook: " + greyBook + " and user prefs " + userPrefs);

        this.greyBook = new GreyBook(greyBook);
        this.userPrefs = new UserPrefs(userPrefs);
        this.history = new History(history);
        filteredPersons = new FilteredList<>(this.greyBook.getPersonList());
    }

    public ModelManager() {
        this(new GreyBook(), new UserPrefs(), new History());
    }

    // =========== History
    // ==================================================================================

    @Override
    public void setHistory(ReadOnlyHistory history) {
        requireNonNull(history);
        this.history.resetData(history);
    }

    @Override
    public ReadOnlyHistory getHistory() {
        return history;
    }

    // =========== UserPrefs
    // ==================================================================================

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    @Override
    public Path getGreyBookFilePath() {
        return userPrefs.getGreyBookFilePath();
    }

    @Override
    public void setGreyBookFilePath(Path greyBookFilePath) {
        requireNonNull(greyBookFilePath);
        userPrefs.setGreyBookFilePath(greyBookFilePath);
    }

    // =========== GreyBook
    // ================================================================================

    @Override
    public void setGreyBook(ReadOnlyGreyBook greyBook) {
        this.greyBook.resetData(greyBook);
    }

    @Override
    public ReadOnlyGreyBook getGreyBook() {
        return greyBook;
    }

    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return greyBook.hasPerson(person);
    }

    @Override
    public void deletePerson(Person target) {
        greyBook.removePerson(target);
    }

    @Override
    public void addPerson(Person person) {
        greyBook.addPerson(person);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        greyBook.setPerson(target, editedPerson);
    }

    @Override
    public void markPerson(Person target, AttendanceStatus.Status attendanceStatus) {
        requireAllNonNull(target, attendanceStatus);

        AttendanceStatus newAttendanceStatus = new AttendanceStatus(attendanceStatus);
        Person markedPerson = new Person(target.getName(), target.getPhone(), target.getEmail(), target.getStudentID(),
                target.getTags(), newAttendanceStatus);
        setPerson(target, markedPerson);
    }

    @Override
    public void unmarkPerson(Person target) {
        requireAllNonNull(target);

        Person unmarkedPerson = new Person(target.getName(), target.getPhone(), target.getEmail(),
                target.getStudentID(), target.getTags(), new AttendanceStatus());
        setPerson(target, unmarkedPerson);
    }

    @Override
    public Optional<Person> getPersonByStudentId(StudentID studentId) {
        requireNonNull(studentId);
        return filteredPersons.stream().filter(person -> person.getStudentID().equals(studentId)).findFirst();
    }

    // =========== Filtered Person List Accessors
    // =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the
     * internal list of {@code versionedGreyBook}
     */
    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return filteredPersons;
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager)) {
            return false;
        }

        ModelManager otherModelManager = (ModelManager) other;
        return greyBook.equals(otherModelManager.greyBook) && userPrefs.equals(otherModelManager.userPrefs)
                && history.equals(otherModelManager.history)
                && filteredPersons.equals(otherModelManager.filteredPersons);
    }
}
