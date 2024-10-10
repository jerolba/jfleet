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
package org.jfleet.parquet;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.apache.parquet.hadoop.ParquetWriter;

public class JFleetParquetWriter<T> implements Closeable, Consumer<T> {

    private final ParquetWriter<T> writer;

    public JFleetParquetWriter(ParquetConfiguration<T> config) throws IOException {
        this.writer = config.getWriterBuilder().build();
    }

    /**
     *
     * Writes the specified Collection of Java objects to a Parquet file.
     *
     * @param collection the collection of objects to write
     * @throws IOException if an error occurs while writing the records
     */
    public void writeAll(Collection<T> collection) throws IOException {
        for (T value : collection) {
            writer.write(value);
        }
    }

    /**
     *
     * Writes the specified stream of Java objects to a Parquet file.
     *
     * @param stream the stream of objects to write
     *
     * @throws IOException if an error occurs while writing the records
     */
    public void writeAll(Stream<T> stream) throws IOException {
        Iterator<T> it = stream.iterator();
        while (it.hasNext()) {
            writer.write(it.next());
        }
    }

    /**
     *
     * Writes the specified Java object to a Parquet file
     *
     * @param value object to write
     * @throws IOException if an error occurs while writing the records
     */
    public void write(T value) throws IOException {
        writer.write(value);
    }

    /**
     *
     * Writes the specified Java object to a Parquet file implementing Consumer of T
     *
     * @param value object to write
     * @throws UncheckedIOException if an error occurs while writing the records
     */
    @Override
    public void accept(T value) {
        try {
            writer.write(value);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}
