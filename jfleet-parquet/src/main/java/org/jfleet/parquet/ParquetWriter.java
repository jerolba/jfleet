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

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.jfleet.avro.AvroSchemaBuilder;
import org.jfleet.avro.EntityGenericRecordMapper;

public class ParquetWriter<T> {

    private final ParquetConfiguration<T> config;
    private final EntityGenericRecordMapper<T> mapper;
    private final Schema schema;

    public ParquetWriter(ParquetConfiguration<T> config) {
        this.config = config;
        this.schema = new AvroSchemaBuilder(this.config.getEntityInfo()).build();
        this.mapper = new EntityGenericRecordMapper<>(schema, this.config.getEntityInfo());
    }

    public void writeAll(Collection<T> collection) throws IOException {
        writeAll(collection.stream());
    }

    public void writeAll(Stream<T> stream) throws IOException {
        try (org.apache.parquet.hadoop.ParquetWriter<GenericRecord> writer = config
                .getWriterBuilder()
                .withSchema(schema)
                .build()) {

            Iterator<T> it = stream.iterator();
            while (it.hasNext()) {
                writer.write(mapper.buildAvroRecord(it.next()));
            }
        }
    }

}
