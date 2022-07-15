package org.jfleet.avro;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;
import org.jfleet.ColumnInfo;
import org.jfleet.EntityFieldType.FieldTypeEnum;
import org.jfleet.EntityInfo;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

public class AvroWriter<T> {
    private final AvroConfiguration avroConfiguration;

    public AvroWriter(AvroConfiguration avroConfiguration) {
        this.avroConfiguration = avroConfiguration;
    }

    public void writeAll(OutputStream output, Collection<T> collection) throws IOException {
        EntityInfo entityInfo = avroConfiguration.getEntityInfo();

        Schema schema = buildSchema(avroConfiguration);

        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(schema);
        try (DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(datumWriter)) {
            dataFileWriter.create(schema, output);
            for (T entity : collection) {
                GenericRecord genericRecord = new GenericData.Record(schema);
                for (ColumnInfo columnInfo : entityInfo.getColumns()) {
                    Object value = columnInfo.getAccessor().apply(entity);
                    if (value != null) {
                        value = extractValue(columnInfo, value);
                        genericRecord.put(columnInfo.getColumnName(), value);
                    }
                }
                dataFileWriter.append(genericRecord);
            }
        }
    }

    private Object extractValue(ColumnInfo columnInfo, Object value) {
        FieldTypeEnum fieldType = columnInfo.getFieldType().getFieldType();
        if (fieldType == FieldTypeEnum.BYTE) {
            value = ((Byte) value).intValue();
        } else if (fieldType == FieldTypeEnum.SHORT) {
            value = ((Short) value).intValue();
        } else if (fieldType == FieldTypeEnum.ENUMSTRING) {
            value = ((Enum<?>) value).name();
        } else if (fieldType == FieldTypeEnum.ENUMORDINAL) {
            value = ((Enum<?>) value).ordinal();
        }
        return value;
    }

    private Schema buildSchema(AvroConfiguration avroConfiguration) {
        EntityInfo entityInfo = avroConfiguration.getEntityInfo();
        SchemaBuilder.FieldAssembler<Schema> fields = SchemaBuilder.record(entityInfo.getEntityClass().getName())
                .namespace(entityInfo.getEntityClass().getPackage().getName()).fields();

        for (ColumnInfo columnInfo : entityInfo.getColumns()) {
            SchemaBuilder.FieldTypeBuilder<Schema> type = fields.name(columnInfo.getColumnName()).type();
            boolean isPrimitive = columnInfo.getFieldType().isPrimitive();

            switch (columnInfo.getFieldType().getFieldType()) {
                case STRING:
                case ENUMSTRING:
                    fields = buildAsString(type);
                    break;
                case ENUMORDINAL:
                    fields = buildAsInteger(type, false);
                    break;
                case INT:
                case SHORT:
                case BYTE:
                    fields = buildAsInteger(type, isPrimitive);
                    break;
                case DOUBLE:
                    fields = buildAsDouble(type, isPrimitive);
                    break;
                case LONG:
                    fields = buildAsLong(type, isPrimitive);
                    break;
                case FLOAT:
                    fields = buildAsFloat(type, isPrimitive);
                    break;
                case BOOLEAN:
                    fields = buildAsBoolean(type, isPrimitive);
                    break;
                default:
                    throw new UnsupportedTypeException(String.format("Unsupported type: %s", columnInfo.getFieldType().getFieldType()));
            }
        }

        return fields.endRecord();
    }

    private SchemaBuilder.FieldAssembler<Schema> buildAsString(SchemaBuilder.FieldTypeBuilder<Schema> type) {
        return type.unionOf().stringType().and().nullType().endUnion().noDefault();
    }

    private SchemaBuilder.FieldAssembler<Schema> buildAsBoolean(SchemaBuilder.FieldTypeBuilder<Schema> type, boolean isPrimitive) {
        if (isPrimitive) {
            return type.booleanType().noDefault();
        }
        return type.unionOf().booleanType().and().nullType().endUnion().noDefault();
    }

    private SchemaBuilder.FieldAssembler<Schema> buildAsFloat(SchemaBuilder.FieldTypeBuilder<Schema> type, boolean isPrimitive) {
        if (isPrimitive) {
            return type.floatType().noDefault();
        }
        return type.unionOf().floatType().and().nullType().endUnion().noDefault();
    }

    private SchemaBuilder.FieldAssembler<Schema> buildAsLong(SchemaBuilder.FieldTypeBuilder<Schema> type, boolean isPrimitive) {
        if (isPrimitive) {
            return type.longType().noDefault();
        }
        return type.unionOf().longType().and().nullType().endUnion().noDefault();
    }

    private SchemaBuilder.FieldAssembler<Schema> buildAsInteger(SchemaBuilder.FieldTypeBuilder<Schema> type, boolean isPrimitive) {
        if (isPrimitive) {
            return type.intType().noDefault();
        }
        return type.unionOf().intType().and().nullType().endUnion().noDefault();
    }

    private SchemaBuilder.FieldAssembler<Schema> buildAsDouble(SchemaBuilder.FieldTypeBuilder<Schema> type, boolean isPrimitive) {
        if (isPrimitive) {
            return type.doubleType().noDefault();
        }
        return type.unionOf().doubleType().and().nullType().endUnion().noDefault();
    }
}
