package seedu.address.logic.commands.update;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.EVENT_COMMAND_WORD;
import static seedu.address.logic.parser.event.EventCliSyntax.PREFIX_EVENT_LINKED_CONTACT_LONG;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.event.AttendanceStatus;
import seedu.address.model.event.Event;

/**
 * Adds the log of given contacts via index.
 */
public class AddContactToLogEventCommand extends EditEventCommand {
    public static final String COMMAND_WORD = "log";

    public static final String MESSAGE_USAGE = EVENT_COMMAND_WORD + " " + COMMAND_WORD
            + ": Logs some contacts with an event.\n"
            + "Parameters: INDEX "
            + PREFIX_EVENT_LINKED_CONTACT_LONG + "[CONTACT_INDEX/INDICES]\n"
            + "INDEX must be a positive integer.\n"
            + "CONTACT_INDEX is based on the list of contacts associated with the event."
            + "Use `event info INDEX` to view the list of associated contacts.\n"
            + "Example: " + EVENT_COMMAND_WORD + " " + COMMAND_WORD + " 1 "
            + PREFIX_EVENT_LINKED_CONTACT_LONG + "1 2 3";
    public static final String MESSAGE_ADD_LOG_SUCCESS = "Added attendance from contacts to event: %1$s";
    public static final String MESSAGE_CONTACT_ALREADY_LOGGED =
            "The contact at the following index(es) are already logged: %s";

    private final List<Index> contactIndices;

    /**
     * Creates a RemoveContactFromEventCommand to remove the contacts at the specific {@code
     * contactIndices}.
     */
    public AddContactToLogEventCommand(Index index, List<Index> contactIndices) {
        super(index, new EditEventDescriptor());
        requireNonNull(contactIndices);
        this.contactIndices = contactIndices;
    }

    @Override
    public Event createEditedItem(Model model, Event eventToEdit) throws CommandException {
        // Check indices against current filtered list
        for (Index index : contactIndices) {
            if (index.getZeroBased() >= eventToEdit.getAttendance().size()) {
                throw new CommandException(String.format(Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX,
                        index.getOneBased()));
            }
        }

        // Map to contacts and check for already logged
        List<Index> checkContactMarked = eventToEdit.getAttendance().match(contactIndices,
                new AttendanceStatus(true));
        if (!checkContactMarked.isEmpty()) {
            throw new CommandException(String.format(MESSAGE_CONTACT_ALREADY_LOGGED,
                    checkContactMarked.stream().map(x -> String.valueOf(x.getOneBased()))
                            .collect(Collectors.joining(", ")))
            );
        }

        // Update the descriptor and call super
        editEventDescriptor.setAttendance(eventToEdit.getAttendance().log(contactIndices,
                new AttendanceStatus(true)));
        return super.createEditedItem(model, eventToEdit);
    }

    @Override
    public String getSuccessMessage(Event editedItem) {
        return String.format(MESSAGE_ADD_LOG_SUCCESS, Messages.format(editedItem));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddContactToLogEventCommand otherCommand)) {
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
