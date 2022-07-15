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
            switch (columnInfo.getFieldType().getFieldType()) {
                case STRING:
                case ENUMSTRING:
                    fields = fields.name(columnInfo.getColumnName()).type().unionOf().stringType().and().nullType().endUnion().noDefault();
                    break;
                case ENUMORDINAL:
                    fields = fields.name(columnInfo.getColumnName()).type().unionOf().intType().and().nullType().endUnion().noDefault();
                    break;

                case INT:
                case SHORT:
                case BYTE:
                    if (columnInfo.getFieldType().isPrimitive()) {
                        fields = fields.name(columnInfo.getColumnName()).type().intType().noDefault();
                    } else {
                        fields = fields.name(columnInfo.getColumnName()).type().unionOf().intType().and().nullType().endUnion().noDefault();
                    }
                    break;

                case DOUBLE:
                    if (columnInfo.getFieldType().isPrimitive()) {
                        fields = fields.name(columnInfo.getColumnName()).type().doubleType().noDefault();
                    } else {
                        fields = fields.name(columnInfo.getColumnName()).type().unionOf().doubleType().and().nullType().endUnion().noDefault();
                    }
                    break;

                case LONG:
                    if (columnInfo.getFieldType().isPrimitive()) {
                        fields = fields.name(columnInfo.getColumnName()).type().longType().noDefault();
                    } else {
                        fields = fields.name(columnInfo.getColumnName()).type().unionOf().longType().and().nullType().endUnion().noDefault();
                    }
                    break;
                case FLOAT:
                    if (columnInfo.getFieldType().isPrimitive()) {
                        fields = fields.name(columnInfo.getColumnName()).type().floatType().noDefault();
                    } else {
                        fields = fields.name(columnInfo.getColumnName()).type().unionOf().floatType().and().nullType().endUnion().noDefault();
                    }
                    break;

                case BOOLEAN:
                    if (columnInfo.getFieldType().isPrimitive()) {
                        fields = fields.name(columnInfo.getColumnName()).type().booleanType().noDefault();
                    } else {
                        fields = fields.name(columnInfo.getColumnName()).type().unionOf().booleanType().and().nullType().endUnion().noDefault();
                    }
                    break;

                default:
                    throw new UnsupportedTypeException(String.format("Unsupported type: %s", columnInfo.getFieldType().getFieldType()));
            }
        }

        return fields.endRecord();
    }
}
