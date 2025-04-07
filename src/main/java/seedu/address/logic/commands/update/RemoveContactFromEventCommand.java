package seedu.address.logic.commands.update;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.ContactMessages.MESSAGE_INDEX_OUT_OF_RANGE_CONTACT;
import static seedu.address.logic.parser.CliSyntax.EVENT_COMMAND_WORD;
import static seedu.address.logic.parser.event.EventCliSyntax.PREFIX_EVENT_LINKED_CONTACT_LONG;

import java.util.List;
import java.util.Objects;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.event.Event;

/**
 * Remove some associated contacts via index from an event.
 */
public class RemoveContactFromEventCommand extends EditEventCommand {

    public static final String COMMAND_WORD = "unlink";
    public static final String MESSAGE_USAGE = EVENT_COMMAND_WORD + " " + COMMAND_WORD
            + ": Remove the association between a event and some contacts.\n"
            + "Parameters: INDEX "
            + PREFIX_EVENT_LINKED_CONTACT_LONG + "[CONTACT_INDEX/INDICES]\n"
            + "INDEX must be a positive integer.\n"
            + "CONTACT_INDEX is based on the list of contacts associated with the event."
            + "Use `event info INDEX` to view the list of associated contacts.\n"
            + "CONTACT_INDEX is based on the list displayed by the event info command.\n"
            + "Example: " + EVENT_COMMAND_WORD + " " + COMMAND_WORD + " 1 "
            + PREFIX_EVENT_LINKED_CONTACT_LONG + "1 3 4";
    public static final String MESSAGE_REMOVE_CONTACT_SUCCESS = "Removed contacts from event: %1$s";

    private final List<Index> contactIndices;

    /**
     * Creates a RemoveContactFromEventCommand to remove the contacts at the specified {@code
     * contactIndices} from the list of contacts in the event at the specified {@code index}.
     */
    public RemoveContactFromEventCommand(Index index, List<Index> contactIndices) {
        super(index, new EditEventDescriptor());
        requireNonNull(contactIndices);
        this.contactIndices = contactIndices;
    }

    @Override
    public Event createEditedItem(Model model, Event eventToEdit) throws CommandException {
        // Check indices against current event contact list
        for (Index index : contactIndices) {
            if (index.getZeroBased() >= eventToEdit.getAttendance().size()) {
                throw new CommandException(String.format(MESSAGE_INDEX_OUT_OF_RANGE_CONTACT, index.getOneBased()));
            }
        }

        editEventDescriptor.setAttendance(eventToEdit.getAttendance().remove(contactIndices));
        return super.createEditedItem(model, eventToEdit);
    }

    @Override
    public String getSuccessMessage(Event editedItem) {
        return String.format(MESSAGE_REMOVE_CONTACT_SUCCESS, Messages.format(editedItem));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof RemoveContactFromEventCommand otherCommand)) {
            return false;
        }

        return targetIndex.equals(otherCommand.targetIndex)
                && contactIndices.equals(otherCommand.contactIndices);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetIndex, contactIndices);
    }
}
