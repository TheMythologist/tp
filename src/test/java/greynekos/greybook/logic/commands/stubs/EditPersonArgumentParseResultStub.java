package greynekos.greybook.logic.commands.stubs;

import static greynekos.greybook.logic.parser.CliSyntax.PREAMBLE;
import static greynekos.greybook.logic.parser.CliSyntax.PREFIX_EMAIL;
import static greynekos.greybook.logic.parser.CliSyntax.PREFIX_NAME;
import static greynekos.greybook.logic.parser.CliSyntax.PREFIX_PHONE;
import static greynekos.greybook.logic.parser.CliSyntax.PREFIX_STUDENTID;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import greynekos.greybook.commons.core.index.Index;
import greynekos.greybook.logic.commands.EditCommand.EditPersonDescriptor;
import greynekos.greybook.logic.parser.commandoption.Option;
import greynekos.greybook.logic.parser.commandoption.OptionalOption;
import greynekos.greybook.logic.parser.commandoption.RequiredOption;
import greynekos.greybook.model.tag.Tag;

/**
 * Mock stub for ArgumentParseResult to test EditCommand. Stores the index of
 * the person to be editted and the EditPersonDescriptor for the person.
 */
public class EditPersonArgumentParseResultStub extends ArgumentParseResultStub {
    private Index index;
    private EditPersonDescriptor descriptor;

    /**
     * @param index
     *            the Index of the person to be editted
     * @param descriptor
     *            the EditorPersonDescriptor to edit the person
     */
    public EditPersonArgumentParseResultStub(Index index, EditPersonDescriptor descriptor) {
        super();
        this.index = index;
        this.descriptor = descriptor;
    }

    // Can only be called for index flag
    @Override
    public <T> T getValue(RequiredOption<T> flag) {
        @SuppressWarnings("unchecked")
        T ret = (T) index;

        return ret;
    }

    @Override
    public <T> Optional<T> getOptionalValue(OptionalOption<T> flag) {
        Object o = null;
        if (flag.getPrefix().equals(PREFIX_NAME)) {
            o = descriptor.getName();
        } else if (flag.getPrefix().equals(PREFIX_EMAIL)) {
            o = descriptor.getEmail();
        } else if (flag.getPrefix().equals(PREFIX_PHONE)) {
            o = descriptor.getPhone();
        } else if (flag.getPrefix().equals(PREFIX_STUDENTID)) {
            o = descriptor.getStudentID();
        } else if (flag.getPrefix().equals(PREAMBLE)) {
            o = index;
        }

        @SuppressWarnings("unchecked")
        Optional<T> ret = (Optional<T>) o;
        return ret;
    }

    // Can only be called for tag flag
    @Override
    public <T> List<T> getAllValues(Option<T> flag) {
        @SuppressWarnings("unchecked")
        List<T> ret = (List<T>) new ArrayList<>(descriptor.getTags().isPresent()
                ? (descriptor.getTags().get().isEmpty() ? List.of(new Tag()) : descriptor.getTags().get())
                : Set.of());

        return ret;
    }
}
