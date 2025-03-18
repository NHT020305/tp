package seedu.address.logic.commands.person;

import static seedu.address.logic.parser.CliSyntax.PERSON_COMMAND_WORD;
import static seedu.address.logic.parser.CliSyntax.PREFIX_COURSE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.abstractcommand.AddCommand;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Adds a person to the address book.
 */
public class AddPersonCommand extends AddCommand<Person> {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = PERSON_COMMAND_WORD + " " + COMMAND_WORD + ": Adds a person to the "
            + "address book. "
            + "Parameters: "
            + PREFIX_ID + "ID "
            + PREFIX_NAME + "NAME "
            + PREFIX_PHONE + "PHONE "
            + PREFIX_EMAIL + "EMAIL "
            + PREFIX_COURSE + "COURSE "
            + PREFIX_GROUP + "GROUP "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + PERSON_COMMAND_WORD + " " + COMMAND_WORD + " "
            + PREFIX_ID + "A1234567A "
            + PREFIX_NAME + "John Doe "
            + PREFIX_PHONE + "98765432 "
            + PREFIX_EMAIL + "johnd@example.com "
            + PREFIX_COURSE + "CS50 "
            + PREFIX_GROUP + "T01 "
            + PREFIX_TAG + "friends "
            + PREFIX_TAG + "owesMoney";

    public static final String MESSAGE_SUCCESS = "New person added: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book";

    /**
     * Creates an AddCommand to add the specified {@code Person}
     */
    public AddPersonCommand(Person person) {
        super(person, Model::getPersonManagerAndList);
    }


    @Override
    public String getDuplicateItemMessage() {
        return MESSAGE_DUPLICATE_PERSON;
    }

    @Override
    public String getSuccessMessage(Person itemToAdd) {
        return String.format(MESSAGE_SUCCESS, Messages.format(itemToAdd));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddPersonCommand otherAddPersonCommand)) {
            return false;
        }

        return itemToAdd.equals(otherAddPersonCommand.itemToAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", itemToAdd)
                .toString();
    }
}
