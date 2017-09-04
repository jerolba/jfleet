package org.jfleet.postgres;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PgCopyEscaperTest {

    private static Logger logger = LoggerFactory.getLogger(PgCopyEscaperTest.class);

    private static final char ESCAPED_BY_CHAR = '\\';
    private static final char LINE_TERMINATED_CHAR = '\n';
    private static final char CARRIAGE_RETURN_CHAR = '\r';
    private static final char FIELD_TERMINATED_CHAR = '\t';

    private PgCopyEscaper escaper = new PgCopyEscaper();

    @Test
    public void testNoEscapeNeeded() {
        String text = "Some text with no escape neeed";
        String escaped = escaper.escapeForStdIn(text);
        assertEquals(text, escaped);
    }

    @Test
    public void testEscapeEscapedByChar() {
        String text = "Some text with some \\ escape char \\\\ to change";
        String escaped = escaper.escapeForStdIn(text);
        assertEquals("Some text with some " + ESCAPED_BY_CHAR + ESCAPED_BY_CHAR + " escape char " + ESCAPED_BY_CHAR
                + ESCAPED_BY_CHAR + ESCAPED_BY_CHAR + ESCAPED_BY_CHAR + " to change", escaped);
        logger.info("FROM:");
        logger.info(text);
        logger.info("TO:");
        logger.info(escaped);
    }

    @Test
    public void testEscapeFieldSeparator() {
        String text = "1234\tvalue\t5678\t";
        String escaped = escaper.escapeForStdIn(text);
        assertEquals("1234" + ESCAPED_BY_CHAR + FIELD_TERMINATED_CHAR + "value" + ESCAPED_BY_CHAR
                + FIELD_TERMINATED_CHAR + "5678" + ESCAPED_BY_CHAR + FIELD_TERMINATED_CHAR, escaped);
        logger.info("FROM:");
        logger.info(text);
        logger.info("TO:");
        logger.info(escaped);
    }

    @Test
    public void testEscapeLineTermination() {
        String text = "Some text with \nmore than one line\n";
        String escaped = escaper.escapeForStdIn(text);
        assertEquals("Some text with " + ESCAPED_BY_CHAR + LINE_TERMINATED_CHAR + "more than one line" + ESCAPED_BY_CHAR
                + LINE_TERMINATED_CHAR, escaped);
        logger.info("FROM:");
        logger.info(text);
        logger.info("TO:");
        logger.info(escaped);
    }

    @Test
    public void testEscapeLineTerminationWithCarriageReturn() {
        String text = "Some text with \r\nmore than one line\r\n";
        String escaped = escaper.escapeForStdIn(text);
        String expected = "Some text with " + ESCAPED_BY_CHAR + CARRIAGE_RETURN_CHAR + ESCAPED_BY_CHAR + LINE_TERMINATED_CHAR + "more than one line"
                + ESCAPED_BY_CHAR + CARRIAGE_RETURN_CHAR + ESCAPED_BY_CHAR + LINE_TERMINATED_CHAR;
        assertEquals(expected, escaped);
        logger.info("FROM:");
        logger.info(text);
        logger.info("TO:");
        logger.info(escaped);
    }

}
