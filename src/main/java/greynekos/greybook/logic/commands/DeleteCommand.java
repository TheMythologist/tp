package greynekos.greybook.logic.commands;

import static java.util.Objects.requireNonNull;

import greynekos.greybook.logic.commands.exceptions.CommandException;
import greynekos.greybook.logic.commands.util.CommandUtil;
import greynekos.greybook.logic.parser.ArgumentParseResult;
import greynekos.greybook.logic.parser.GreyBookParser;
import greynekos.greybook.logic.parser.ParserUtil;
import greynekos.greybook.logic.parser.commandoption.SinglePreambleOption;
import greynekos.greybook.model.Model;
import greynekos.greybook.model.person.Person;
import greynekos.greybook.model.person.PersonIdentifier;

/**
 * Deletes a person identified using either the displayed index or student ID
 * from GreyBook.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE =
            COMMAND_WORD + ": Deletes the person identified by the index number or student ID.\n"
                    + "Parameters: INDEX (must be a positive integer) or STUDENTID (format: A0000000Y)\n"
                    + "Examples: \n" + "  " + COMMAND_WORD + " 1\n" + "  " + COMMAND_WORD + " A0123456J";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Member %1$s has been successfully deleted!";

    public static final String MESSAGE_PERSON_NOT_FOUND = "Error, user does not exist.";

    private final SinglePreambleOption<PersonIdentifier> identifierOption =
            SinglePreambleOption.of("INDEX or STUDENTID", ParserUtil::parsePersonIdentifier);

    @Override
    public void addToParser(GreyBookParser parser) {
        parser.newCommand(COMMAND_WORD, MESSAGE_USAGE, this).addOptions(identifierOption);
    }

    @Override
    public CommandResult execute(Model model, ArgumentParseResult arg) throws CommandException {
        requireNonNull(model);

        PersonIdentifier identifier = getParseResult(arg);
        Person personToDelete = CommandUtil.resolvePerson(model, identifier);

        model.deletePerson(personToDelete);
        return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, personToDelete.getName()));
    }

    @Override
    public PersonIdentifier getParseResult(ArgumentParseResult argResult) {
        return argResult.getValue(identifierOption);
    }
}
