package seedu.address.model.item.commons;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents an Item's location in the location book.
 * Guarantees: immutable; is valid as declared in {@link #isValid(String)}
 */
public class Location {
    public static final String MESSAGE_CONSTRAINTS =
            "Invalid location supplied. Locations must consist of words not starting with a hyphen and separated by "
                    + "spaces, and it must not be blank";
    // Ensures string is not empty AND does not start with whitespace
    public static final String VALIDATION_REGEX = "^(?!\\s)(?!-)\\S+(?:\\s+(?!-)\\S+)*$";
    public final String value;

    /**
     * Constructs a {@code Location}.
     */
    public Location(String location) {
        requireNonNull(location);
        checkArgument(isValid(location), MESSAGE_CONSTRAINTS);
        value = location;
    }

    /**
     * Returns true if a given string is a valid event location.
     */
    public static boolean isValid(String location) {
        return location.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Location otherLocation)) {
            return false;
        }
        return this.value.toLowerCase().equals(otherLocation.value.toLowerCase());
    }

    @Override
    public int hashCode() {
        return value.toLowerCase().hashCode();
    }
}
