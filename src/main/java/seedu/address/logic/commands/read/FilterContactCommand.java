package seedu.address.logic.commands.read;

import static seedu.address.logic.parser.CliSyntax.CONTACT_COMMAND_WORD;
import static seedu.address.logic.parser.contact.ContactCliSyntax.PREFIX_CONTACT_COURSE_LONG;
import static seedu.address.logic.parser.contact.ContactCliSyntax.PREFIX_CONTACT_EMAIL_LONG;
import static seedu.address.logic.parser.contact.ContactCliSyntax.PREFIX_CONTACT_GROUP_LONG;
import static seedu.address.logic.parser.contact.ContactCliSyntax.PREFIX_CONTACT_ID_LONG;
import static seedu.address.logic.parser.contact.ContactCliSyntax.PREFIX_CONTACT_NAME_LONG;
import static seedu.address.logic.parser.contact.ContactCliSyntax.PREFIX_CONTACT_TAG_LONG;

import java.util.function.Predicate;

import seedu.address.commons.core.Operator;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.Model;
import seedu.address.model.contact.Contact;
import seedu.address.model.contact.ContactManagerAndList;
import seedu.address.model.contact.ContactPredicate;
import seedu.address.ui.ListPanelViewType;

/**
 * Filters and lists all contacts in address book based on specified criteria.
 * Filter criteria are formed with columns, operators, and values.
 */
public class FilterContactCommand extends FilterCommand<ContactManagerAndList, Contact> {

    public static final String MESSAGE_USAGE = CONTACT_COMMAND_WORD + " " + COMMAND_WORD
            + ": Filters contacts that fulfill all of the specified criteria. Specify at least one criterion.\n"
            + "Each criterion is formed using a column, operator, and values, in the form --<COL> [<OP>:] <VALUE(S)>\n"

            + "- --<COL> : Column to filter on ("
            + PREFIX_CONTACT_NAME_LONG + ", "
            + PREFIX_CONTACT_EMAIL_LONG + ", "
            + PREFIX_CONTACT_ID_LONG + ", "
            + PREFIX_CONTACT_COURSE_LONG + ", "
            + PREFIX_CONTACT_GROUP_LONG + ", "
            + PREFIX_CONTACT_TAG_LONG + ")\n"

            + "- <OP>: : Operator ("
            + Operator.AND.getName() + ", "
            + Operator.OR.getName() + ", "
            + Operator.NAND.getName() + ", "
            + Operator.NOR.getName() + "). If not specified, defaults to "
            + Operator.AND.getName() + "\n"

            + "- <VALUE(S)>: One or more values to filter by.\n"
            + "    + Enter keywords separated by spaces. These keywords are not case-sensitive, and partial matches "
            + "are supported.\n"

            + "Examples:\n"
            + "1. " + CONTACT_COMMAND_WORD + " " + COMMAND_WORD + " "
            + PREFIX_CONTACT_ID_LONG + Operator.OR.getName() + ": 12 13\n"
            + "   Find contacts with ID 12 or 13.\n"

            + "2. " + CONTACT_COMMAND_WORD + " " + COMMAND_WORD + " "
            + PREFIX_CONTACT_NAME_LONG + "Darren Tan "
            + PREFIX_CONTACT_COURSE_LONG + "CS1010S "
            + PREFIX_CONTACT_GROUP_LONG + Operator.OR.getName() + ": T01 T02 T03\n"
            + "   Find contacts with both \"Darren\" and \"Tan\" in their name who enroll in course CS1010S and class "
            + "T01, T02, or T03.\n"

            + "3. " + CONTACT_COMMAND_WORD + " " + COMMAND_WORD + " "
            + PREFIX_CONTACT_NAME_LONG + Operator.NAND.getName() + ": enemy Hater "
            + PREFIX_CONTACT_TAG_LONG + Operator.AND.getName() + ": handsome smart\n"
            + "   Find contacts whose names do not contain \"enemy\" and \"Hater\" and are tagged with both "
            + "\"handsome\" and \"smart\".";

    private final Predicate<Contact> predicate;

    /**
     * Constructs a FilterContactCommand with the given predicate.
     *
     * @param predicate the predicate used to filter contacts
     */
    public FilterContactCommand(ContactPredicate predicate) {
        super(Model::getContactManagerAndList);
        this.predicate = predicate;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FilterContactCommand otherFilterCommand)) {
            return false;
        }

        return predicate.equals(otherFilterCommand.predicate);
    }

    @Override
    public Predicate<Contact> createPredicate(Model model) {
        return predicate;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }

    @Override
    public ListPanelViewType getListPanelViewType() {
        return ListPanelViewType.CONTACT;
    }
}
