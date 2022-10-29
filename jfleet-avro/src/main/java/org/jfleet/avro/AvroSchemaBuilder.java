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

import java.util.function.Function;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.SchemaBuilder.BaseFieldTypeBuilder;
import org.apache.avro.SchemaBuilder.FieldAssembler;
import org.apache.avro.SchemaBuilder.FieldDefault;
import org.apache.avro.SchemaBuilder.FieldTypeBuilder;
import org.jfleet.ColumnInfo;
import org.jfleet.EntityInfo;

public class AvroSchemaBuilder {

    private final EntityInfo entityInfo;

    public AvroSchemaBuilder(EntityInfo entityInfo) {
        this.entityInfo = entityInfo;
    }

    public Schema build() {
        FieldAssembler<Schema> fields = SchemaBuilder.record(entityInfo.getEntityClass().getName())
                .namespace(entityInfo.getEntityClass().getPackage().getName())
                .fields();
        for (ColumnInfo columnInfo : entityInfo.getColumns()) {
            fields = getFieldSchema(columnInfo, fields);
        }
        return fields.endRecord();
    }

    private FieldAssembler<Schema> getFieldSchema(ColumnInfo columnInfo, FieldAssembler<Schema> fields) {
        FieldTypeBuilder<Schema> beginType = fields.name(columnInfo.getColumnName()).type();
        boolean isPrimitive = columnInfo.getFieldType().isPrimitive();

        switch (columnInfo.getFieldType().getFieldType()) {
        case STRING:
        case ENUMSTRING:
            return beginType.nullable().stringType().noDefault();
        case ENUMORDINAL:
            return ofType(beginType, false, BaseFieldTypeBuilder::intType);
        case INT:
        case SHORT:
        case BYTE:
            return ofType(beginType, isPrimitive, BaseFieldTypeBuilder::intType);
        case DOUBLE:
            return ofType(beginType, isPrimitive, BaseFieldTypeBuilder::doubleType);
        case LONG:
            return ofType(beginType, isPrimitive, BaseFieldTypeBuilder::longType);
        case FLOAT:
            return ofType(beginType, isPrimitive, BaseFieldTypeBuilder::floatType);
        case BOOLEAN:
            return ofType(beginType, isPrimitive, BaseFieldTypeBuilder::booleanType);
        default:
            throw new UnsupportedTypeException(
                    String.format("Unsupported type: %s", columnInfo.getFieldType().getFieldType()));
        }
    }

    private FieldAssembler<Schema> ofType(FieldTypeBuilder<Schema> type, boolean isPrimitive,
            Function<BaseFieldTypeBuilder<Schema>, FieldDefault<Schema, ?>> typeMapper) {
        BaseFieldTypeBuilder<Schema> forType = isPrimitive ? type : type.nullable();
        return typeMapper.apply(forType).noDefault();
    }

}
