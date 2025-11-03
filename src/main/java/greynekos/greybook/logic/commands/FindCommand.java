package greynekos.greybook.logic.commands;

import static greynekos.greybook.logic.parser.CliSyntax.PREFIX_STUDENTID;
import static java.util.Objects.requireNonNull;

import greynekos.greybook.logic.Messages;
import greynekos.greybook.logic.commands.exceptions.CommandException;
import greynekos.greybook.logic.parser.ArgumentParseResult;
import greynekos.greybook.logic.parser.GreyBookParser;
import greynekos.greybook.logic.parser.ParserUtil;
import greynekos.greybook.logic.parser.commandoption.OptionalSinglePreambleOption;
import greynekos.greybook.logic.parser.commandoption.ZeroOrMorePrefixOption;
import greynekos.greybook.model.Model;
import greynekos.greybook.model.person.NameOrStudentIdPredicate;

/**
 * Finds and lists all persons in GreyBook whose name contains any of the
 * argument keywords, or student ID contains any of the provided ID fragments.
 * Keyword matching is case-insensitive.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds persons by name keywords and/or student ID.\n"
            + "Parameters: [KEYWORD [MORE_KEYWORDS]...] [" + PREFIX_STUDENTID + "ID_FRAGMENT]...\n" + "Examples:\n"
            + "  " + COMMAND_WORD + " alice bob\n" + "  " + COMMAND_WORD + " i/12345 i/A0123456J\n" + "  "
            + COMMAND_WORD + " i/12345 alex\n" + "  " + COMMAND_WORD + " alex i/12345";

    public static final String MESSAGE_EMPTY_COMMAND = "Invalid command format!\n" + MESSAGE_USAGE;

    private final OptionalSinglePreambleOption<String> preambleOption = OptionalSinglePreambleOption.of("KEYWORDS");

    private final ZeroOrMorePrefixOption<String> studentIdFragmentsOption =
            ZeroOrMorePrefixOption.of(PREFIX_STUDENTID, "ID_FRAGMENT", s -> s == null ? "" : s.trim());

    @Override
    public void addToParser(GreyBookParser parser) {
        parser.newCommand(COMMAND_WORD, MESSAGE_USAGE, this).addOptions(studentIdFragmentsOption, preambleOption);
    }

    @Override
    public CommandResult execute(Model model, ArgumentParseResult arg) throws CommandException {
        requireNonNull(model);
        ParserUtil.KeywordsAndIdFrags parsed =
                ParserUtil.parseKeywordsAndIdFrags(arg, preambleOption, studentIdFragmentsOption);
        if (parsed.keywords().isEmpty() && parsed.idFrags().isEmpty()) {
            throw new CommandException(MESSAGE_EMPTY_COMMAND);
        }
        model.updateFilteredPersonList(new NameOrStudentIdPredicate(parsed.keywords(), parsed.idFrags()));
        return new CommandResult(
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, model.getFilteredPersonList().size()));
    }

}
