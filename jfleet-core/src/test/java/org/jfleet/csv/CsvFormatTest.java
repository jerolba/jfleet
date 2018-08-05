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
package org.jfleet.csv;

import static org.jfleet.csv.CsvTestHelper.createBuilderForSomeEntity;
import static org.jfleet.csv.CsvTestHelper.writeCsvToString;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.jfleet.csv.CsvConfiguration.Builder;
import org.junit.Test;

public class CsvFormatTest {

    private Builder<SomeEntity> config = new Builder<>(createBuilderForSomeEntity().build());

    @Test
    public void fieldSeparatorTest() throws IOException {
        config.fieldSeparator(';');
        String result = writeCsvToString(config.build(), new SomeEntity("John", 10));
        assertEquals("name;age\nJohn;10\n", result);
    }

    @Test
    public void escapeFieldSeparatorTest() throws IOException {
        config.fieldSeparator(';').textDelimiter('\'');
        String result = writeCsvToString(config.build(), new SomeEntity("John;Smith", 10));
        assertEquals("name;age\n'John;Smith';10\n", result);
    }

    @Test
    public void optionalTextDelimiterNotUsedTest() throws IOException {
        config.textDelimiter('$');
        String result = writeCsvToString(config.build(), new SomeEntity("John", 10));
        assertEquals("name,age\nJohn,10\n", result);
    }

    @Test
    public void optionalTextDelimiterUsedTest() throws IOException {
        config.textDelimiter('"');
        String result = writeCsvToString(config.build(), new SomeEntity("John \"Smith\"", 10));
        assertEquals("name,age\n\"John \"\"Smith\"\"\",10\n", result);
    }

    @Test
    public void mandatoryTextDelimiterUsedTest() throws IOException {
        config.textDelimiter('\'').alwaysDelimitText(true);
        String result = writeCsvToString(config.build(), 
                new SomeEntity("John 'Smith'", 10),
                new SomeEntity("Amanda", 20));
        assertEquals("'name','age'\n'John ''Smith''','10'\n'Amanda','20'\n", result);
    }

    @Test
    public void escapeLineDelimiterTest() throws IOException {
        config.textDelimiter('\'');
        String result = writeCsvToString(config.build(), new SomeEntity("John \nSmith", 10));
        assertEquals("name,age\n'John \nSmith',10\n", result);
    }

}
