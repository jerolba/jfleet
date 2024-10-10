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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 *
 * Writes a JFleet entity to an OutputStream a CSV.
 *
 * Accepts a collection of objects and writes all into the OutputStream
 *
 * The class doesn't support to call writeAll multiple times on the given
 * OutputStream if the header is written.
 *
 * @deprecated {@link org.jfleet.csv.JFleetCsvWriter}
 * @param <T> Type of the object to write
 */
@Deprecated
public class CsvWriter<T> {

    private final CsvConfiguration<T> config;

    public CsvWriter(Class<T> clazz) {
        this(new CsvConfiguration<>(clazz));
    }

    public CsvWriter(CsvConfiguration<T> config) {
        this.config = config;
    }

    public void writeAll(OutputStream output, Collection<T> collection) throws IOException {
        writeAll(output, collection.stream());
    }

    public void writeAll(OutputStream output, Stream<T> stream) throws IOException {
        CsvSerializer<T> serializer = new CsvSerializer<>(output, config);
        if (config.isHeader()) {
            serializer.writeHeader();
        }
        Iterator<T> iterator = stream.iterator();
        while (iterator.hasNext()) {
            serializer.add(iterator.next());
        }
        serializer.flush();
    }

}
