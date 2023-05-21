/**
 * Copyright 2022 Jerónimo López Bezanilla
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

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import org.jfleet.EntityFieldType.FieldTypeEnum;
import org.jfleet.EntityInfoBuilder;
import org.junit.jupiter.api.Test;

public class JFleetCsvWriterTest {

    private static String ls = System.lineSeparator();

    @Test
    public void writeMultipleTimesInJfleetWriter() throws IOException {
        EntityInfoBuilder<SomeEntity> entityBuilder = new EntityInfoBuilder<>(SomeEntity.class);
        entityBuilder.addFields("name", "age");
        entityBuilder.addColumn("adult", FieldTypeEnum.BOOLEAN, e -> e.getAge() >= 18);
        CsvConfiguration<SomeEntity> config = new CsvConfiguration<>(entityBuilder.build());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (JFleetCsvWriter<SomeEntity> writer = new JFleetCsvWriter<>(baos, config)) {
            writer.writeAll(asList(new SomeEntity("John", 10), new SomeEntity("Amanda", 34)));
            writer.write(new SomeEntity("Joe", 98));
            writer.writeAll(asList(new SomeEntity("Peter", 20), new SomeEntity("Rose", 16)));
        }
        String result = baos.toString(Charset.defaultCharset().name());
        assertEquals("name,age,adult" + ls + "John,10,false" + ls + "Amanda,34,true" + ls
                + "Joe,98,true" + ls + "Peter,20,true" + ls + "Rose,16,false" + ls, result);
    }

}
