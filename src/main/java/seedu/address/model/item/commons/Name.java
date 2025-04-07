package seedu.address.model.item.commons;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents an Item's name in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValid(String)}
 */
public class Name implements Comparable<Name> {

    public static final String MESSAGE_CONSTRAINTS =
            "Invalid name supplied. Names must consist of words not starting with a hyphen and separated by spaces, "
                    + "and it must not be blank";
    // Ensures string is not empty AND does not start with whitespace
    public static final String VALIDATION_REGEX = "^(?!\\s)(?!-)\\S+(?:\\s+(?!-)\\S+)*$";

    public final String value;

    /**
     * Constructs a {@code Name}.
     *
     * @param name A valid name.
     */
    public Name(String name) {
        requireNonNull(name);
        checkArgument(isValid(name), MESSAGE_CONSTRAINTS);
        value = name;
    }

    /**
     * Returns true if a given string is a valid name.
     */
    public static boolean isValid(String test) {
        return test.matches(VALIDATION_REGEX);
    }


    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Name otherName)) {
            return false;
        }

        return value.toLowerCase().equals(otherName.value.toLowerCase());
    }

    @Override
    public int hashCode() {
        return value.toLowerCase().hashCode();
    }

    @Override
    public int compareTo(Name other) {
        return this.value.toLowerCase().compareTo(other.value.toLowerCase());
    }
}
