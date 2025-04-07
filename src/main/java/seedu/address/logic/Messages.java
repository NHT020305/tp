package seedu.address.logic;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.parser.Prefix;
import seedu.address.model.contact.Contact;
import seedu.address.model.event.Event;
import seedu.address.model.item.TaggedItem;
import seedu.address.model.item.commons.Tag;
import seedu.address.model.todo.Todo;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command. Type " + HelpCommand.COMMAND_WORD
            + " to see available commands.";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format! \n%1$s";
    public static final String MESSAGE_INVALID_ARGUMENTS = "Arguments missing or invalid! \n%1$s";
    public static final String MESSAGE_SEARCH_OVERVIEW = "Number of results: %1$d";
    public static final String MESSAGE_DUPLICATE_FIELDS = "Duplicate prefixes detected: ";

    public static final String MESSAGE_NO_COLUMNS = "Specify at least one column and value.";
    public static final String MESSAGE_NO_VALUES = "No values specified for column '%s'.";
    public static final String MESSAGE_UNRECOGNIZED_COLUMN = "Unrecognized column: '%s'.";
    public static final String MESSAGE_DUPLICATE_TAGS = "Duplicate tags found for: %s";


    public static final String MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX =
            "The contact index provided is out of range: %1$s";
    public static final String MESSAGE_MISSING_CONTACT_INDEX =
            "At least one contact must be provided.";
    public static final String MESSAGE_MISSING_TAG = "At least one tag must be provided.";

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields =
                Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Formats the {@code contact} for display to the user.
     */
    public static String format(Contact contact) {
        return contact.getName()
                + "; ID: " + contact.getId()
                + "; Email: " + contact.getEmail()
                + "; Course: " + contact.getCourse()
                + "; Group: " + contact.getGroup()
                + "; Tags: " + getTagListFormatted(contact);
    }

    /**
     * Formats the {@code todo} for display to the user.
     */
    public static String format(Todo todo) {
        String contactsFormatted = getTodoContactListFormatted(todo);
        String tagsFormatted = getTagListFormatted(todo);

        return todo.getName()
                + "; Deadline: " + todo.getDeadline()
                + "; Location: " + todo.getLocation()
                + "; Status: " + todo.getStatus()
                + "; Tags: " + tagsFormatted
                + "; Contacts:" + (todo.getContacts().isEmpty() ? " " : "\n") + contactsFormatted;
    }

    /**
     * Formats the {@code todo} for display to the user.
     */
    public static String format(Event event) {
        String contactListFormatted = getEventContactListFormatted(event);
        String eventListFormatted = getTagListFormatted(event);
        return event.getName()
                + "; Start Time: " + event.getStartTime()
                + "; End Time: " + event.getEndTime()
                + "; Location: " + event.getLocation()
                + "; Tag: " + eventListFormatted
                + "; Contacts: " + contactListFormatted;
    }

    /**
     * Returns a simplified format of a {@code contact} for display to the user.
     */
    public static String getSimplifiedFormat(Contact contact) {
        return contact.getName()
                + "; ID: " + contact.getId()
                + "; Course: " + contact.getCourse()
                + "; Group: " + contact.getGroup();
    }

    private static String getTagListFormatted(TaggedItem item) {
        if (item.getTags().isEmpty()) {
            return "None";
        } else {
            return item.getTags().stream()
                    .map(Tag::toString)
                    .collect(Collectors.joining(", "));
        }
    }

    private static String getTodoContactListFormatted(Todo todo) {
        if (todo.getContacts().isEmpty()) {
            return "None";
        } else {
            return IntStream.range(0, todo.getContacts().size())
                    .mapToObj(i -> (i + 1) + ". " + getSimplifiedFormat(todo.getContacts().get(i)))
                    .collect(Collectors.joining("\n"));
        }
    }

    private static String getEventContactListFormatted(Event event) {
        if (event.getAttendance().size() == 0) {
            return "None";
        }
        String string = "\n" + IntStream.range(0, event.getAttendance().size())
                .mapToObj(i -> String.format("%d.[%s] %s",
                        i + 1, event.getAttendance().get(i).second().toString(),
                        getSimplifiedFormat(event.getAttendance().get(i).first())))
                .collect(Collectors.joining("\n"));
        return string;
    }
}
