package greynekos.greybook.model.person;

import java.util.Objects;

/**
 * Uses the singleton pattern to define an "all" identifier
 */
public class All implements PersonIdentifierOrAll {
    public static final String ALL_KEYWORD = "all";

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof All)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash();
    }
}
