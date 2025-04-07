package seedu.address.logic.commands.read;

import static seedu.address.logic.Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX;
import static seedu.address.logic.parser.CliSyntax.EVENT_COMMAND_WORD;
import static seedu.address.logic.parser.event.EventCliSyntax.PREFIX_EVENT_END_LONG;
import static seedu.address.logic.parser.event.EventCliSyntax.PREFIX_EVENT_LINKED_CONTACT_LONG;
import static seedu.address.logic.parser.event.EventCliSyntax.PREFIX_EVENT_LOCATION_LONG;
import static seedu.address.logic.parser.event.EventCliSyntax.PREFIX_EVENT_NAME_LONG;
import static seedu.address.logic.parser.event.EventCliSyntax.PREFIX_EVENT_START_LONG;
import static seedu.address.logic.parser.event.EventCliSyntax.PREFIX_EVENT_TAG_LONG;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import seedu.address.commons.core.Operator;
import seedu.address.commons.core.Pair;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.contact.Contact;
import seedu.address.model.event.Event;
import seedu.address.model.event.EventManagerAndList;
import seedu.address.model.event.predicate.EventContactPredicate;
import seedu.address.model.event.predicate.EventPredicate;
import seedu.address.ui.ListPanelViewType;

/**
 * Filters and lists all events based on specified criteria. Filter criteria are formed with
 * columns, operators, and values.
 */
public class FilterEventCommand extends FilterCommand<EventManagerAndList, Event> {

    public static final String MESSAGE_USAGE = EVENT_COMMAND_WORD + " " + COMMAND_WORD
            + ": Filters events that fulfill all of the specified criteria. Specify at least one criterion.\n"
            + "Each criterion is formed using a column, operator, and values, in the form --<COL> [<OP>:] <VALUE(S)>\n"

            + "- --<COL> : Column to filter on ("
            + PREFIX_EVENT_NAME_LONG + ", "
            + PREFIX_EVENT_START_LONG + ", "
            + PREFIX_EVENT_END_LONG + ", "
            + PREFIX_EVENT_LOCATION_LONG + ", "
            + PREFIX_EVENT_TAG_LONG + ", "
            + PREFIX_EVENT_LINKED_CONTACT_LONG + ")\n"

            + "- <OP>: : Operator ("
            + Operator.AND.getName() + ", "
            + Operator.OR.getName() + ", "
            + Operator.NAND.getName() + ", "
            + Operator.NOR.getName() + "). If not specified, defaults to "
            + Operator.AND.getName() + "\n"

            + "- <VALUE(S)>: One or more values to filter by.\n"
            + "    + For name and location, use keywords separated by whitespaces. These keywords are not "
            + "case-sensitive, and partial matches are supported.\n"
            + "    + For start and end time, use closed intervals separated by whitespaces. "
            + "Each interval is in the format \"[<INTERVAL_START>/<INTERVAL_END>]\"."
            + " For both start and end time, the interval must either be in the format YY-MM-DD HH:MM"
            + ", where HH is in 24-hour format, or be '-', where it is unbounded."
            + " At least one of the two bounds must be specified.\n"
            + "    + For linked contacts, use the indices in the currently displayed contact list.\n"

            + "Examples:\n"
            + "1. " + EVENT_COMMAND_WORD + " " + COMMAND_WORD + " "
            + PREFIX_EVENT_NAME_LONG + Operator.OR.getName() + ": Exam PRESENTATION\n"
            + "   Find events whose name contains at least one of the keywords \"exam\" or \"presentation.\"\n"

            + "2. " + EVENT_COMMAND_WORD + " " + COMMAND_WORD + " "
            + PREFIX_EVENT_NAME_LONG + "CS1010S eXAM "
            + PREFIX_EVENT_START_LONG + Operator.OR.getName()
            + ": [25-03-13 23:59/25-03-20 23:59] [25-03-27 23:59/-]\n"
            + "   Find events whose name contains both the keywords \"CS1010S\" and \"exam\" and whose start time is "
            + "between 25-03-13 23:59 and 25-03-20 23:59 (inclusive) or after 25-03-27 23:59 (inclusive).\n"

            + "3. " + EVENT_COMMAND_WORD + " " + COMMAND_WORD + " "
            + PREFIX_EVENT_LOCATION_LONG + Operator.NAND.getName() + ": NUS Home "
            + PREFIX_EVENT_LINKED_CONTACT_LONG + "1 2 3\n"
            + "   Find events whose location does not contain keywords \"NUS\" and \"home\", and which are is linked"
            + " to the contacts currently at index 1, 2 and 3.\n";

    private final EventPredicate eventPredicate;
    private final Optional<Pair<Operator, List<Index>>> contactFilterOpt;

    /**
     * Creates a {@code FilterEventCommand} to filter items that match the given predicates.
     */
    public FilterEventCommand(EventPredicate eventPredicate,
                              Optional<Pair<Operator, List<Index>>> contactFilterOpt) {
        super(Model::getEventManagerAndList);
        this.eventPredicate = eventPredicate;
        this.contactFilterOpt = contactFilterOpt;
    }

    @Override
    public Predicate<Event> createPredicate(Model model) throws CommandException {
        if (contactFilterOpt.isPresent()) {
            List<Contact> filteredContacts = model.getContactManagerAndList().getFilteredItemsList();

            for (Index index : contactFilterOpt.get().second()) {
                if (index.getZeroBased() >= filteredContacts.size()) {
                    throw new CommandException(String.format(
                            MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX, index.getOneBased()));
                }
            }

            List<Contact> contacts = contactFilterOpt.get().second().stream()
                    .map(index -> filteredContacts.get(index.getZeroBased())).toList();

            eventPredicate.setContactPredicate(
                    new EventContactPredicate(contactFilterOpt.get().first(), contacts));
        }

        return eventPredicate;
    }

    @Override
    public ListPanelViewType getListPanelViewType() {
        return ListPanelViewType.EVENT;
    }
}
