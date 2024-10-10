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
package org.jfleet.avro;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;
import org.jfleet.EntityInfo;
import org.jfleet.inspection.JpaEntityInspector;

public class JFleetAvroWriter<T> implements Closeable, Consumer<T> {

    private final EntityGenericRecordMapper<T> mapper;
    private final DataFileWriter<GenericRecord> dataFileWriter;

    public JFleetAvroWriter(OutputStream outputStream, EntityInfo entityInfo) throws IOException {
        Schema schema = new AvroSchemaBuilder(entityInfo).build();
        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(schema);
        this.dataFileWriter = new DataFileWriter<>(datumWriter);
        this.dataFileWriter.create(schema, outputStream);
        this.mapper = new EntityGenericRecordMapper<>(schema, entityInfo);
    }

    public JFleetAvroWriter(OutputStream outputStream, Class<T> clazz) throws IOException {
        this(outputStream, buildEntityInfo(clazz));
    }

    public JFleetAvroWriter(OutputStream outputStream, AvroConfiguration<T> avroConfiguration) throws IOException {
        this(outputStream, getEntityInfo(avroConfiguration));
    }

    private static <T> EntityInfo getEntityInfo(AvroConfiguration<T> config) {
        EntityInfo configEntityInfo = config.getEntityInfo();
        if (configEntityInfo != null) {
            return configEntityInfo;
        }
        return new JpaEntityInspector(config.getClazz()).inspect();
    }

    private static <T> EntityInfo buildEntityInfo(Class<T> clazz) {
        return new JpaEntityInspector(clazz).inspect();
    }

    /**
     *
     * Writes the specified Collection of Java objects to an Avro file.
     *
     * @param entities the collection of objects to write
     * @throws IOException if an error occurs while writing the records
     */
    public void writeAll(Collection<T> entities) throws IOException {
        for (T entity : entities) {
            write(entity);
        }
    }

    /**
     *
     * Writes the specified stream of Java objects to an Avro file.
     *
     * @param entities the stream of objects to write
     *
     * @throws IOException if an error occurs while writing the records
     */
    public void writeAll(Stream<T> entities) throws IOException {
        Iterator<T> iterator = entities.iterator();
        while (iterator.hasNext()) {
            write(iterator.next());
        }
    }

    /**
     *
     * Writes the specified Java object to an Avro file.
     *
     * @param entity object to write
     * @throws IOException if an error occurs while writing the records
     */
    public void write(T entity) throws IOException {
        dataFileWriter.append(mapper.buildAvroRecord(entity));
    }

    /**
     *
     * Writes the specified Java object to an Avro file implementing Consumer of T
     *
     * @param entity object to write
     * @throws UncheckedIOException if an error occurs while writing the records
     */
    @Override
    public void accept(T entity) {
        try {
            write(entity);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void close() throws IOException {
        dataFileWriter.close();
    }

}
