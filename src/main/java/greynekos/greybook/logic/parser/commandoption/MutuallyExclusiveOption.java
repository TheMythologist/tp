package greynekos.greybook.logic.parser.commandoption;

/**
 * An interface representing an option that cannot appear more than once in the
 * command
 */
public interface MutuallyExclusiveOption<T> extends NoDuplicateOption<T> {
    String getPrefixGroup();
}
