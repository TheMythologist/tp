package greynekos.greybook.logic.parser;

/**
 * Contains Command Line Interface (CLI) syntax definitions common to multiple
 * commands
 */
public class CliSyntax {

    /* Prefix definitions */
    public static final Prefix PREFIX_NAME = new Prefix("n/");
    public static final Prefix PREFIX_PHONE = new Prefix("p/");
    public static final Prefix PREFIX_EMAIL = new Prefix("e/");
    public static final Prefix PREFIX_TAG = new Prefix("t/");
    public static final Prefix PREFIX_STUDENTID = new Prefix("i/");
    public static final Prefix PREFIX_PRESENT = new Prefix("p/");
    public static final Prefix PREFIX_ABSENT = new Prefix("a/");
    public static final Prefix PREFIX_LATE = new Prefix("l/");
    public static final Prefix PREFIX_EXCUSED = new Prefix("e/");
    public static final Preamble PREAMBLE = new Preamble();
}
