package greynekos.greybook.model.person;

import static greynekos.greybook.testutil.TypicalPersons.ALICE;
import static greynekos.greybook.testutil.TypicalPersons.BENSON;
import static greynekos.greybook.testutil.TypicalPersons.getTypicalGreyBook;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import greynekos.greybook.model.GreyBook;

public class NameOrStudentIdPredicateTest {

    @BeforeAll
    public static void loadTypical() {
        new GreyBook(getTypicalGreyBook());
    }

    @Test
    public void test_matchesName_returnsTrue() {
        NameOrStudentIdPredicate predicate =
                new NameOrStudentIdPredicate(Arrays.asList("alice"), Collections.emptyList());
        assertTrue(predicate.test(ALICE));

        predicate = new NameOrStudentIdPredicate(Arrays.asList("ALICE"), Collections.emptyList());
        assertTrue(predicate.test(ALICE));

        predicate = new NameOrStudentIdPredicate(Arrays.asList("ALI"), Collections.emptyList());
        assertTrue(predicate.test(ALICE));

        predicate = new NameOrStudentIdPredicate(Arrays.asList("zzz", "alice"), Collections.emptyList());
        assertTrue(predicate.test(ALICE));
    }

    @Test
    public void test_matchesIdFragment_returnsTrue() {
        String fullId = ALICE.getStudentID().value;
        String frag = fullId.substring(1, Math.min(fullId.length(), 5));
        NameOrStudentIdPredicate predicate = new NameOrStudentIdPredicate(Collections.emptyList(), List.of(frag));
        assertTrue(predicate.test(ALICE));
    }

    @Test
    public void test_blankTokensIgnored_returnsTrueForActualTokens() {
        NameOrStudentIdPredicate predicate =
                new NameOrStudentIdPredicate(Arrays.asList(" ", "   ", "alice"), Arrays.asList("   "));
        assertTrue(predicate.test(ALICE));
    }

    @Test
    public void test_noMatch_returnsFalse() {
        NameOrStudentIdPredicate predicate =
                new NameOrStudentIdPredicate(Arrays.asList("charlie"), Collections.emptyList());
        assertFalse(predicate.test(ALICE));

        String idBen = BENSON.getStudentID().value;
        predicate = new NameOrStudentIdPredicate(Arrays.asList("charlie"), Arrays.asList(idBen));
        assertFalse(predicate.test(ALICE));
    }

    @Test
    public void test_emptyCriteria_returnsFalse() {
        NameOrStudentIdPredicate predicate =
                new NameOrStudentIdPredicate(Collections.emptyList(), Collections.emptyList());
        assertFalse(predicate.test(ALICE));
    }

    @Test
    public void equals() {
        NameOrStudentIdPredicate p1 = new NameOrStudentIdPredicate(Arrays.asList("alice"), Arrays.asList("123"));
        NameOrStudentIdPredicate p2 = new NameOrStudentIdPredicate(Arrays.asList("alice"), Arrays.asList("123"));
        NameOrStudentIdPredicate p3 = new NameOrStudentIdPredicate(Arrays.asList("benson"), Arrays.asList("123"));
        NameOrStudentIdPredicate p4 = new NameOrStudentIdPredicate(Arrays.asList("alice"), Arrays.asList("999"));

        assertTrue(p1.equals(p1));
        assertTrue(p1.equals(p2));
        assertFalse(p1.equals(p3));
        assertFalse(p1.equals(p4));
        assertFalse(p1.equals(null));
        assertFalse(p1.equals("not a predicate"));
    }
}
