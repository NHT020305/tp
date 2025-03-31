package seedu.address.logic.parser.event;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.event.EventCliSyntax.PREFIX_EVENT_LINKED_CONTACT_LONG;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.EventMessages;
import seedu.address.logic.commands.update.RemoveContactFromEventCommand;
import seedu.address.logic.parser.ArgumentMultimap;
import seedu.address.logic.parser.ArgumentTokenizer;
import seedu.address.logic.parser.Parser;
import seedu.address.logic.parser.ParserUtil;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new RemoveContactFromEventCommandParser object.
 */
public class RemoveContactFromEventCommandParser implements Parser<RemoveContactFromEventCommand> {

    @Override
    public RemoveContactFromEventCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_EVENT_LINKED_CONTACT_LONG);

        // Ensure only one prefix is present
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_EVENT_LINKED_CONTACT_LONG);
        if (!argMultimap.arePrefixesPresent(PREFIX_EVENT_LINKED_CONTACT_LONG) || argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemoveContactFromEventCommand.MESSAGE_USAGE));
        }

        // Parse index of event to edit
        Index index = ParserUtil.parseIndex(argMultimap.getPreamble());

        // Parse contact indices, duplicates are handled in parseIndices
        List<Index> contactIndices = ParserUtil.parseIndices(
                argMultimap.getValue(PREFIX_EVENT_LINKED_CONTACT_LONG).get());

        // Check against empty and duplicate contact indices
        if (contactIndices.isEmpty()) {
            throw new ParseException(EventMessages.MESSAGE_NOT_REMOVED);
        }

        return new RemoveContactFromEventCommand(index, contactIndices);
    }
}
