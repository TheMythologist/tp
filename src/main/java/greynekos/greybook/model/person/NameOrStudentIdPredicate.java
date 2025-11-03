package greynekos.greybook.model.person;

import static greynekos.greybook.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;
import java.util.function.Predicate;

import greynekos.greybook.commons.util.StringUtil;

/**
 * Tests that a {@code Person}'s {@code Name} or {@code StudentID} matches any
 * of the keywords given.
 */
public class NameOrStudentIdPredicate implements Predicate<Person> {

    private final List<String> keywords;
    private final List<String> idFragmentsUp;

    /**
     * Constructs a NameOrStudentIdPredicate.
     */
    public NameOrStudentIdPredicate(List<String> keywords, List<String> idFragments) {
        requireAllNonNull(keywords, idFragments);
        this.keywords = keywords;
        this.idFragmentsUp = idFragments.stream().map(String::toUpperCase).toList();
    }

    @Override
    public boolean test(Person person) {
        String fullName = person.getName().fullName;

        boolean matchesName = !keywords.isEmpty() && keywords.stream().filter(kw -> !kw.isBlank())
                .anyMatch(kw -> StringUtil.containsSubstringIgnoreCase(fullName, kw));

        String idUp = person.getStudentID().toString().toUpperCase();

        boolean matchesAnyIdFrag =
                !idFragmentsUp.isEmpty() && idFragmentsUp.stream().filter(f -> !f.isBlank()).anyMatch(idUp::contains);

        return matchesName || matchesAnyIdFrag;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof NameOrStudentIdPredicate)) {
            return false;
        }
        NameOrStudentIdPredicate otherNameOrStudentIdPredicate = (NameOrStudentIdPredicate) other;
        return keywords.equals(otherNameOrStudentIdPredicate.keywords)
                && idFragmentsUp.equals(otherNameOrStudentIdPredicate.idFragmentsUp);
    }
}
