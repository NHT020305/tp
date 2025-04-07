package seedu.address.logic.commands.update;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.ContactMessages.MESSAGE_INDEX_OUT_OF_RANGE_CONTACT;
import static seedu.address.logic.parser.CliSyntax.TODO_COMMAND_WORD;
import static seedu.address.logic.parser.todo.TodoCliSyntax.PREFIX_TODO_LINKED_CONTACT_LONG;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.contact.Contact;
import seedu.address.model.todo.Todo;

/**
 * Remove some associated contacts via index from a todo.
 */
public class RemoveContactFromTodoCommand extends EditTodoCommand {

    public static final String COMMAND_WORD = "unlink";
    public static final String MESSAGE_USAGE = TODO_COMMAND_WORD + " " + COMMAND_WORD
            + ": Remove the association between a todo and some contacts.\n"
            + "Parameters: INDEX "
            + PREFIX_TODO_LINKED_CONTACT_LONG + "[CONTACT_INDEX/INDICES]\n"
            + "INDEX must be a positive integer.\n"
            + "CONTACT_INDEX is based on the list of contacts associated with the todo."
            + "Use `todo info INDEX` to view the list of associated contacts.\n"
            + "Example: " + TODO_COMMAND_WORD + " " + COMMAND_WORD + " 1 "
            + PREFIX_TODO_LINKED_CONTACT_LONG + "1 3 4";
    public static final String MESSAGE_REMOVE_CONTACT_SUCCESS = "Removed contacts from todo: %1$s";

    private final List<Index> contactIndices;

    /**
     * Creates an RemoveContactFromTodoCommand to remove the contacts at the specified {@code
     * contactIndices} from the list of contacts in the todo at the specified {@code index}.
     */
    public RemoveContactFromTodoCommand(Index index, List<Index> contactIndices) {
        super(index, new EditTodoDescriptor());
        requireNonNull(contactIndices);
        this.contactIndices = contactIndices;
    }

    @Override
    public Todo createEditedItem(Model model, Todo todoToEdit) throws CommandException {
        // Check indices against current todo contact list
        for (Index index : contactIndices) {
            if (index.getZeroBased() >= todoToEdit.getContacts().size()) {
                throw new CommandException(String.format(MESSAGE_INDEX_OUT_OF_RANGE_CONTACT, index.getOneBased()));
            }
        }

        // Map to contacts and remove
        List<Contact> newContacts = new ArrayList<>(todoToEdit.getContacts());
        contactIndices.stream()
                .map(Index::getZeroBased)
                .sorted(Comparator.reverseOrder())
                .forEach(index -> newContacts.remove((int) index));

        // Update the descriptor and call super
        editTodoDescriptor.setContacts(newContacts);
        return super.createEditedItem(model, todoToEdit);
    }

    @Override
    public String getSuccessMessage(Todo editedItem) {
        return String.format(MESSAGE_REMOVE_CONTACT_SUCCESS, Messages.format(editedItem));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof RemoveContactFromTodoCommand otherCommand)) {
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
