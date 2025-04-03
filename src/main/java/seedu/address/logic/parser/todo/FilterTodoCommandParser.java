package seedu.address.logic.parser.todo;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_NO_COLUMNS;
import static seedu.address.logic.Messages.MESSAGE_NO_VALUES;

import java.util.List;
import java.util.Optional;

import seedu.address.commons.core.Operator;
import seedu.address.commons.core.Pair;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.read.FilterTodoCommand;
import seedu.address.logic.parser.ArgumentMultimap;
import seedu.address.logic.parser.ArgumentTokenizer;
import seedu.address.logic.parser.Parser;
import seedu.address.logic.parser.ParserUtil;
import seedu.address.logic.parser.Prefix;
import seedu.address.logic.parser.PrefixAlias;
import seedu.address.logic.parser.PrefixAliasListBuilder;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.item.predicate.LocationPredicate;
import seedu.address.model.item.predicate.NamePredicate;
import seedu.address.model.item.predicate.TagPredicate;
import seedu.address.model.todo.predicate.TodoContactPredicate;
import seedu.address.model.todo.predicate.TodoDeadlinePredicate;
import seedu.address.model.todo.predicate.TodoPredicate;
import seedu.address.model.todo.predicate.TodoStatusPredicate;

/**
 * Parses input arguments and creates a new EditCommand object
 */
public class FilterTodoCommandParser implements Parser<FilterTodoCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the EditCommand and returns an
     * EditCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public FilterTodoCommand parse(String args) throws ParseException {
        requireNonNull(args);
        PrefixAlias namePrefix = TodoCliSyntax.PREFIX_ALIAS_TODO_NAME;
        PrefixAlias deadlinePrefix = TodoCliSyntax.PREFIX_ALIAS_TODO_DEADLINE;
        PrefixAlias locationPrefix = TodoCliSyntax.PREFIX_ALIAS_TODO_LOCATION;
        PrefixAlias statusPrefix = TodoCliSyntax.PREFIX_ALIAS_TODO_STATUS;
        PrefixAlias contactPrefix = TodoCliSyntax.PREFIX_ALIAS_TODO_LINKED_CONTACT;
        PrefixAlias tagPrefix = TodoCliSyntax.PREFIX_ALIAS_TODO_TAG;
        Prefix[] listOfPrefixes = new PrefixAliasListBuilder()
                .add(namePrefix, deadlinePrefix, locationPrefix, statusPrefix, contactPrefix)
                .toArray();
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, listOfPrefixes);

        argMultimap.verifyNoDuplicatePrefixesFor(listOfPrefixes);

        TodoPredicate predicate = new TodoPredicate();

        if (argMultimap.getValue(namePrefix).isPresent()) {
            Pair<Operator, String> operatorStringPair =
                    ParserUtil.parseOperatorAndString(argMultimap.getValue(namePrefix).get());
            if (operatorStringPair.second().trim().isEmpty()) {
                throw new ParseException(String.format(MESSAGE_NO_VALUES, namePrefix.toString()));
            }
            predicate.setNamePredicate(new NamePredicate(operatorStringPair.first(),
                    List.of(operatorStringPair.second().split("\\s+"))));
        }
        if (argMultimap.getValue(locationPrefix).isPresent()) {
            Pair<Operator, String> operatorStringPair =
                    ParserUtil.parseOperatorAndString(argMultimap.getValue(locationPrefix).get());
            if (operatorStringPair.second().trim().isEmpty()) {
                throw new ParseException(String.format(MESSAGE_NO_VALUES, locationPrefix.toString()));
            }
            predicate.setLocationPredicate(new LocationPredicate(operatorStringPair.first(),
                    List.of(operatorStringPair.second().split("\\s+"))));
        }
        if (argMultimap.getValue(statusPrefix).isPresent()) {
            if (argMultimap.getValue(statusPrefix).get().trim().isEmpty()) {
                throw new ParseException(String.format(MESSAGE_NO_VALUES, statusPrefix.toString()));
            }
            predicate.setStatusPredicate(new TodoStatusPredicate(ParserUtil.parseBoolean(
                    argMultimap.getValue(statusPrefix).get()
            )));
        }
        if (argMultimap.getValue(deadlinePrefix).isPresent()) {
            Pair<Operator, String> operatorStringPair =
                    ParserUtil.parseOperatorAndString(argMultimap.getValue(deadlinePrefix).get());
            if (operatorStringPair.second().trim().isEmpty()) {
                throw new ParseException(String.format(MESSAGE_NO_VALUES, deadlinePrefix));
            }
            predicate.setDeadlinePredicate(new TodoDeadlinePredicate(operatorStringPair.first(),
                    ParserUtil.parseDatetimePredicates(operatorStringPair.second())));
        }
        if (argMultimap.getValue(tagPrefix).isPresent()) {
            Pair<Operator, String> operatorStringPair =
                    ParserUtil.parseOperatorAndString(argMultimap.getValue(tagPrefix).get());
            if (operatorStringPair.second().trim().isEmpty()) {
                throw new ParseException(String.format(MESSAGE_NO_VALUES, tagPrefix.toString()));
            }
            predicate.setTagPredicate(new TagPredicate(operatorStringPair.first(),
                    ParserUtil.parseTags(operatorStringPair.second())));
        }
        Optional<Pair<Operator, List<Index>>> contactFilterOpt = Optional.empty();
        if (argMultimap.getValue(contactPrefix).isPresent()) {
            Pair<Operator, String> operatorStringPair = ParserUtil.parseOperatorAndString(
                    argMultimap.getValue(contactPrefix).get());
            String contactString = operatorStringPair.second().trim();
            if (contactString.isEmpty()) {
                throw new ParseException(String.format(MESSAGE_NO_VALUES, contactPrefix.toString()));
            }
            List<Index> contactIndices = ParserUtil.parseIndices(contactString);
            if (contactIndices.isEmpty()) {
                throw new ParseException(String.format(MESSAGE_NO_VALUES, contactPrefix.toString()));
            }
            contactFilterOpt = Optional.of(new Pair<>(operatorStringPair.first(), contactIndices));
            predicate.setContactPredicate(new TodoContactPredicate(Operator.OR, List.of()));
        }

        if (!predicate.isAnyFieldNonNull()) {
            throw new ParseException(MESSAGE_NO_COLUMNS);
        }

        return new FilterTodoCommand(predicate, contactFilterOpt);
    }
}
