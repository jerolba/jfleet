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

import static org.jfleet.csv.CsvTestHelper.writeCsvToString;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.jfleet.EntityFieldType.FieldTypeEnum;
import org.jfleet.EntityInfoBuilder;
import org.jfleet.common.BaseTypeSerializer;
import org.jfleet.csv.CsvConfiguration.Builder;
import org.junit.jupiter.api.Test;

public class CsvSerializerTest {

    private static String ls = System.lineSeparator();

    @Test
    public void canCreateSyntheticColumns() throws IOException {
        EntityInfoBuilder<SomeEntity> entityBuilder = new EntityInfoBuilder<>(SomeEntity.class);
        entityBuilder.addFields("name", "age");
        entityBuilder.addColumn("adult", FieldTypeEnum.BOOLEAN, e -> e.getAge() >= 18);
        CsvConfiguration<SomeEntity> config = new CsvConfiguration<>(entityBuilder.build());
        String result = writeCsvToString(config, new SomeEntity("John", 10), new SomeEntity("Amanda", 34));
        assertEquals("name,age,adult" + ls + "John,10,false" + ls + "Amanda,34,true" + ls, result);
    }

    @Test
    public void canChangeDefaultSerializer() throws IOException {
        EntityInfoBuilder<SomeEntity> entityBuilder = new EntityInfoBuilder<>(SomeEntity.class);
        entityBuilder.addFields("name", "age");
        entityBuilder.addColumn("adult", FieldTypeEnum.BOOLEAN, e -> e.getAge() >= 18);

        BaseTypeSerializer serializer = new BaseTypeSerializer();
        serializer.add(FieldTypeEnum.BOOLEAN, (obj) -> ((Boolean) obj).booleanValue() ? "1" : "0");

        Builder<SomeEntity> config = new Builder<>(entityBuilder.build());
        config.typeSerializer(serializer);
        String result = writeCsvToString(config.build(), new SomeEntity("John", 10), new SomeEntity("Amanda", 34));
        assertEquals("name,age,adult" + ls + "John,10,0" + ls + "Amanda,34,1" + ls, result);
    }

    @Test
    public void accessorCanChangeType() throws IOException {
        EntityInfoBuilder<SomeEntity> entityBuilder = new EntityInfoBuilder<>(SomeEntity.class);
        entityBuilder.addField("name");
        entityBuilder.addColumn("age", FieldTypeEnum.STRING, e -> e.getAge() + " years");
        entityBuilder.addColumn("adult", FieldTypeEnum.STRING, e -> e.getAge() >= 18 ? "Si" : "No");

        Builder<SomeEntity> config = new Builder<>(entityBuilder.build());
        String result = writeCsvToString(config.build(), new SomeEntity("John", 10), new SomeEntity("Amanda", 34));
        assertEquals("name,age,adult" + ls + "John,10 years,No" + ls + "Amanda,34 years,Si" + ls, result);
    }

}
