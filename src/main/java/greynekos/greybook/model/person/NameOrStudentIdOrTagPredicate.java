package greynekos.greybook.model.person;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Set;

import greynekos.greybook.commons.util.StringUtil;
import greynekos.greybook.model.tag.Tag;

/**
 * Tests that a {@code Person}'s {@code Name} or {@code StudentID} matches any
 * of the keywords given.
 */
public class NameOrStudentIdOrTagPredicate extends NameOrStudentIdPredicate {

    private final List<String> tagFragments;

    /**
     * Constructs a NameOrStudentIdOrTagPredicate.
     */
    public NameOrStudentIdOrTagPredicate(List<String> keywords, List<String> idFragments, List<String> tagFragments) {
        super(keywords, idFragments);
        requireNonNull(tagFragments);
        this.tagFragments = tagFragments;
    }

    @Override
    public boolean test(Person person) {
        Set<Tag> tags = person.getTags();
        return tags.stream()
                .anyMatch(tag -> tagFragments.stream()
                        .anyMatch(tagFrag -> StringUtil.containsSubstringIgnoreCase(tag.tagName, tagFrag)))
                || super.test(person);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof NameOrStudentIdOrTagPredicate)) {
            return false;
        }
        NameOrStudentIdOrTagPredicate otherNameOrStudentIdOrTagPredicate = (NameOrStudentIdOrTagPredicate) other;
        return keywords.equals(otherNameOrStudentIdOrTagPredicate.keywords)
                && idFragmentsUp.equals(otherNameOrStudentIdOrTagPredicate.idFragmentsUp)
                && tagFragments.equals(otherNameOrStudentIdOrTagPredicate.tagFragments);
    }
}
