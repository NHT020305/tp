package seedu.address.model.contact;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Contact's course in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidCourse(String)}
 */
public class Course {

    public static final String MESSAGE_CONSTRAINTS =
            "Invalid course supplied. Courses must consist of words not starting with a hyphen and separated by "
                    + "spaces, and it must not be blank";
    // Ensures string is not empty AND does not start with whitespace
    public static final String VALIDATION_REGEX = "^(?!\\s)(?!-)\\S+(?:\\s+(?!-)\\S+)*$";

    public final String fullCourse;

    /**
     * Constructs a {@code course}.
     *
     * @param course A valid course.
     */
    public Course(String course) {
        requireNonNull(course);
        checkArgument(isValidCourse(course), MESSAGE_CONSTRAINTS);
        fullCourse = course;
    }

    /**
     * Returns true if a given string is a valid course.
     */
    public static boolean isValidCourse(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public int hashCode() {
        return fullCourse.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Course)) {
            return false;
        }

        Course otherCourse = (Course) other;
        return fullCourse.equals(otherCourse.fullCourse);
    }

    @Override
    public String toString() {
        return fullCourse;
    }

}
