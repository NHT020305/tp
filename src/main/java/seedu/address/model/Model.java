package seedu.address.model;

import java.nio.file.Path;
import java.util.function.Predicate;

import seedu.address.commons.core.GuiSettings;
import seedu.address.model.contact.Contact;
import seedu.address.model.contact.ContactManagerAndList;
import seedu.address.model.event.Event;
import seedu.address.model.event.EventManagerAndList;
import seedu.address.model.todo.Todo;
import seedu.address.model.todo.TodoManagerAndList;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluate to true */
    Predicate<Contact> PREDICATE_SHOW_ALL_CONTACTS = unused -> true;
    Predicate<Event> PREDICATE_SHOW_ALL_EVENTS = unused -> true;
    Predicate<Todo> PREDICATE_SHOW_ALL_TODOS = unused -> true;

    /**
     * Replaces user prefs data with the data in {@code userPrefs}.
     */
    void setUserPrefs(ReadOnlyUserPrefs userPrefs);

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Returns the user prefs' address book file path.
     */
    Path getContactListFilePath();

    /**
     * Sets the user prefs' address book file path.
     */
    void setContactListFilePath(Path contactListFilePath);

    /**
     * Returns the user prefs' todo list file path.
     */
    Path getTodoListFilePath();

    /**
     * Sets the user prefs' address book file path.
     */
    void setTodoListFilePath(Path addressBookFilePath);

    /**
     * Returns the user prefs' event list file path.
     */
    Path getEventListFilePath();

    /**
     * Sets the user prefs' address book file path.
     */
    void setEventListFilePath(Path addressBookFilePath);

    /**
     * Returns the contact manager and display list.
     */
    ContactManagerAndList getContactManagerAndList();

    /**
     * Returns the todo manager and display list.
     */
    TodoManagerAndList getTodoManagerAndList();

    /**
     * Returns the event manager and display list
     */
    EventManagerAndList getEventManagerAndList();
}
