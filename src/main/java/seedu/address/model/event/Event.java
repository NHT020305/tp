package seedu.address.model.event;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;
import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.item.Item;
import seedu.address.model.person.Person;

/**
 * Represents an Event.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Event implements Item {
    private final EventName name;
    private final EventDateTime startTime;
    private final EventDateTime endTime;
    private final EventLocation location;
    private final List<Person> persons;

    /**
     * Every field must be present and not null, field values are validated, immutable.
     */
    public Event(EventName name, EventDateTime startTime,
            EventDateTime endTime, EventLocation location, List<Person> persons) {
        requireAllNonNull(name, startTime, endTime, location, persons);
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.persons = persons;
    }

    /**
     * Every field must be present and not null, field values are validated, immutable.
     */
    public Event(EventName name, EventDateTime startTime,
            EventDateTime endTime, EventLocation location) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.persons = List.of();
    }

    public EventName getName() {
        return this.name;
    }

    public EventDateTime getStartTime() {
        return this.startTime;
    }

    public EventDateTime getEndTime() {
        return this.endTime;
    }

    public EventLocation getLocation() {
        return this.location;
    }

    public List<Person> getPersons() {
        return this.persons;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("start_time", startTime)
                .add("end_time", endTime)
                .add("location", location)
                .add("persons", persons)
                .toString();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Event otherEvent)) {
            return false;
        }
        return this.name.equals(otherEvent.name)
                && this.startTime.equals(otherEvent.startTime)
                && this.endTime.equals(otherEvent.endTime)
                && this.location.equals(otherEvent.location)
                && this.persons.equals(otherEvent.persons);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, startTime, endTime, location, persons);
    }
}
