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

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class JFleetCsvWriter<T> implements Closeable, Consumer<T> {

    private final CsvSerializer<T> serializer;

    public JFleetCsvWriter(OutputStream outputStream, Class<T> clazz) throws IOException {
        this(outputStream, new CsvConfiguration<>(clazz));
    }

    public JFleetCsvWriter(OutputStream outputStream, CsvConfiguration<T> config) throws IOException {
        this.serializer = new CsvSerializer<>(outputStream, config);
        if (config.isHeader()) {
            serializer.writeHeader();
        }
    }

    public void writeAll(Collection<T> collection) throws IOException {
        for (T entry : collection) {
            serializer.add(entry);
        }
    }

    public void writeAll(Stream<T> stream) throws IOException {
        Iterator<T> iterator = stream.iterator();
        while (iterator.hasNext()) {
            serializer.add(iterator.next());
        }
    }

    public void write(T entity) throws IOException {
        serializer.add(entity);
    }

    /**
     *
     * Writes the specified Java object to a CSV file implementing Consumer
     *
     * @param entity object to write
     * @throws UncheckedIOException if an error occurs while writing the records
     */
    @Override
    public void accept(T entity) {
        try {
            serializer.add(entity);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void close() throws IOException {
        serializer.close();
    }

}
