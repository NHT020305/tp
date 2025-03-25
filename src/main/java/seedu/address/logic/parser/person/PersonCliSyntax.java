package seedu.address.logic.parser.person;

import seedu.address.logic.parser.Prefix;

/**
 * Contains Command Line Interface (CLI) syntax definitions common to multiple todo commands.
 */
public class PersonCliSyntax {

    /* Prefix definitions */
    public static final Prefix PREFIX_PERSON_NAME = new Prefix("n/");
    public static final Prefix PREFIX_PERSON_PHONE = new Prefix("p/");
    public static final Prefix PREFIX_PERSON_EMAIL = new Prefix("e/");
    public static final Prefix PREFIX_PERSON_TAG = new Prefix("t/");
    public static final Prefix PREFIX_PERSON_ID = new Prefix("i/");
    public static final Prefix PREFIX_PERSON_COURSE = new Prefix("c/");
    public static final Prefix PREFIX_PERSON_GROUP = new Prefix("g/");
    public static final Prefix PREFIX_REMARK = new Prefix("r/");

}
