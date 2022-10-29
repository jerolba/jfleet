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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.jfleet.EntityInfoBuilder;

public class CsvTestHelper {

    public static <T> String writeCsvToString(CsvConfiguration<T> config, List<T> collection) throws IOException {
        CsvWriter<T> writer = new CsvWriter<>(config);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writer.writeAll(baos, collection);
        return baos.toString(Charset.defaultCharset().name());
    }

    @SafeVarargs
    public static <T> String writeCsvToString(CsvConfiguration<T> config, T... collection) throws IOException {
        return writeCsvToString(config, Arrays.asList(collection));
    }

    public static EntityInfoBuilder<SomeEntity> createBuilderForSomeEntity() {
        EntityInfoBuilder<SomeEntity> entityBuilder = new EntityInfoBuilder<>(SomeEntity.class);
        entityBuilder.addFields("name", "age");
        return entityBuilder;
    }

}
