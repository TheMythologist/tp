package greynekos.greybook.logic.parser.commandoption;

import greynekos.greybook.logic.parser.Prefix;
import greynekos.greybook.logic.parser.exceptions.ParseException;

/**
 * The API of the Option component.
 */
public interface Option<T> {
    Prefix getPrefix();

    String getName();

    T parseOptionArgument(String argument) throws ParseException;
}
