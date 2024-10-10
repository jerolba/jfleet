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

import static org.apache.parquet.schema.LogicalTypeAnnotation.enumType;
import static org.apache.parquet.schema.LogicalTypeAnnotation.intType;
import static org.apache.parquet.schema.LogicalTypeAnnotation.stringType;
import static org.apache.parquet.schema.PrimitiveType.PrimitiveTypeName.BINARY;

import java.util.ArrayList;
import java.util.List;

import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.PrimitiveType;
import org.apache.parquet.schema.PrimitiveType.PrimitiveTypeName;
import org.apache.parquet.schema.Type;
import org.apache.parquet.schema.Type.Repetition;
import org.apache.parquet.schema.Types;
import org.jfleet.ColumnInfo;
import org.jfleet.EntityInfo;

class ParquetSchemaBuilder {

    private final EntityInfo entityInfo;

    ParquetSchemaBuilder(EntityInfo entityInfo) {
        this.entityInfo = entityInfo;
    }

    public MessageType build() {
        Class<?> recordClass = entityInfo.getEntityClass();
        String record = recordClass.getSimpleName();

        List<Type> types = new ArrayList<>();
        for (ColumnInfo columnInfo : entityInfo.getColumns()) {
            types.add(getFieldSchema(columnInfo));
        }
        return new MessageType(record, types);
    }

    private Type getFieldSchema(ColumnInfo columnInfo) {
        boolean isPrimitive = columnInfo.getFieldType().isPrimitive();
        Repetition repetition = isPrimitive ? Repetition.REQUIRED : Repetition.OPTIONAL;
        String name = columnInfo.getColumnName();
        switch (columnInfo.getFieldType().getFieldType()) {
        case STRING:
            return Types.primitive(BINARY, repetition).as(stringType()).named(name);
        case ENUMSTRING:
            return Types.primitive(BINARY, repetition).as(enumType()).named(name);
        case ENUMORDINAL:
            return new PrimitiveType(repetition, PrimitiveTypeName.INT32, name);
        case INT:
            return new PrimitiveType(repetition, PrimitiveTypeName.INT32, name);
        case SHORT:
            return Types.primitive(PrimitiveTypeName.INT32, repetition).as(intType(16, true)).named(name);
        case BYTE:
            return Types.primitive(PrimitiveTypeName.INT32, repetition).as(intType(8, true)).named(name);
        case DOUBLE:
            return new PrimitiveType(repetition, PrimitiveTypeName.DOUBLE, name);
        case LONG:
            return new PrimitiveType(repetition, PrimitiveTypeName.INT64, name);
        case FLOAT:
            return new PrimitiveType(repetition, PrimitiveTypeName.FLOAT, name);
        case BOOLEAN:
            return new PrimitiveType(repetition, PrimitiveTypeName.BOOLEAN, name);
        default:
            throw new UnsupportedTypeException(
                    String.format("Unsupported type: %s", columnInfo.getFieldType().getFieldType()));
        }
    }

}
