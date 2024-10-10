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

import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.hadoop.api.WriteSupport;
import org.apache.parquet.io.api.Binary;
import org.apache.parquet.io.api.RecordConsumer;
import org.apache.parquet.schema.MessageType;
import org.jfleet.ColumnInfo;
import org.jfleet.EntityInfo;

class JFleetWriteSupport<T> extends WriteSupport<T> {

    private final EntityInfo entityInfo;
    private final MessageType schema;
    private final Map<String, String> extraMetaData;
    private RecordWriter<T> writer;

    JFleetWriteSupport(EntityInfo entityInfo, MessageType schema, Map<String, String> extraMetaData) {
        this.entityInfo = entityInfo;
        this.schema = schema;
        this.extraMetaData = extraMetaData;
    }

    @Override
    public WriteContext init(Configuration configuration) {
        return new WriteContext(schema, this.extraMetaData);
    }

    @Override
    public void prepareForWrite(RecordConsumer recordConsumer) {
        writer = new RecordWriter<>(entityInfo, recordConsumer);
    }

    @Override
    public void write(T record) {
        writer.write(record);
    }

    private static class RecordWriter<T> {

        private final EntityInfo entityInfo;
        private final RecordConsumer recordConsumer;

        private RecordWriter(EntityInfo entityInfo, RecordConsumer recordConsumer) {
            this.recordConsumer = recordConsumer;
            this.entityInfo = entityInfo;
        }

        private void write(T record) {
            int idx = 0;
            recordConsumer.startMessage();
            for (ColumnInfo col : entityInfo.getColumns()) {
                Object value = col.getAccessor().apply(record);
                if (value != null) {
                    recordConsumer.startField(col.getColumnName(), idx);
                    addField(value, col);
                    recordConsumer.endField(col.getColumnName(), idx);
                }
                idx++;
            }
            recordConsumer.endMessage();
        }

        private void addField(Object value, ColumnInfo col) {
            switch (col.getFieldType().getFieldType()) {
            case STRING:
                recordConsumer.addBinary(Binary.fromString((String) value));
                break;
            case ENUMSTRING:
                recordConsumer.addBinary(Binary.fromString(((Enum<?>) value).name()));
                break;
            case ENUMORDINAL:
                recordConsumer.addInteger(((Enum<?>) value).ordinal());
                break;
            case INT:
                recordConsumer.addInteger((int) value);
                break;
            case SHORT:
                recordConsumer.addInteger(((Short) value).intValue());
                break;
            case BYTE:
                recordConsumer.addInteger(((Byte) value).intValue());
                break;
            case LONG:
                recordConsumer.addLong((long) value);
                break;
            case DOUBLE:
                recordConsumer.addDouble((double) value);
                break;
            case FLOAT:
                recordConsumer.addFloat((float) value);
                break;
            case BOOLEAN:
                recordConsumer.addBoolean((boolean) value);
                break;
            default:
                throw new UnsupportedTypeException(
                        String.format("Unsupported type: %s", col.getFieldType().getFieldType()));
            }
        }

    }
}
