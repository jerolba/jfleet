/**
 * Copyright 2017 Jerónimo López Bezanilla
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jfleet.mysql;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadDataEscaperTest {

    private static Logger logger = LoggerFactory.getLogger(LoadDataEscaperTest.class);

    private static final char ESCAPED_BY_CHAR = '\\';
    private static final char LINE_TERMINATED_CHAR = '\n';
    private static final char FIELD_TERMINATED_CHAR = '\t';

    private LoadDataEscaper escaper = new LoadDataEscaper();

    @Test
    public void testNoEscapeNeeded() {
        String text = "Some text with no escape neeed";
        String escaped = escaper.escapeForLoadFile(text);
        assertEquals(text, escaped);
    }

    @Test
    public void testEscapeEscapedByChar() {
        String text = "Some text with some \\ escape char \\\\ to change";
        String escaped = escaper.escapeForLoadFile(text);
        assertEquals("Some text with some " + ESCAPED_BY_CHAR + ESCAPED_BY_CHAR + " escape char " + ESCAPED_BY_CHAR
                + ESCAPED_BY_CHAR + ESCAPED_BY_CHAR + ESCAPED_BY_CHAR + " to change", escaped);
        logger.info("FROM:");
        logger.info(text);
        logger.info("TO:");
        logger.info(escaped);
    }

    @Test
    public void testEscapeLineTermination() {
        String text = "Some text with \nmore than one line\n";
        String escaped = escaper.escapeForLoadFile(text);
        assertEquals("Some text with " + ESCAPED_BY_CHAR + LINE_TERMINATED_CHAR + "more than one line" + ESCAPED_BY_CHAR
                + LINE_TERMINATED_CHAR, escaped);
        logger.info("FROM:");
        logger.info(text);
        logger.info("TO:");
        logger.info(escaped);
    }

    @Test
    public void testEscapeFieldTermination() {
        String text = "1234\tvalue\t5678\t";
        String escaped = escaper.escapeForLoadFile(text);
        assertEquals("1234" + ESCAPED_BY_CHAR + FIELD_TERMINATED_CHAR + "value" + ESCAPED_BY_CHAR
                + FIELD_TERMINATED_CHAR + "5678" + ESCAPED_BY_CHAR + FIELD_TERMINATED_CHAR, escaped);
        logger.info("FROM:");
        logger.info(text);
        logger.info("TO:");
        logger.info(escaped);
    }
}
