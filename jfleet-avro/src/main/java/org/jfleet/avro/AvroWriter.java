package org.jfleet.avro;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;
import org.jfleet.ColumnInfo;
import org.jfleet.EntityFieldType.FieldTypeEnum;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

public class AvroWriter<T> {
    private final AvroConfiguration avroConfiguration;
    private final Schema schema;

    public AvroWriter(AvroConfiguration avroConfiguration) {
        this.avroConfiguration = avroConfiguration;
        schema = new AvroSchemaBuilder(avroConfiguration.getEntityInfo()).build();
    }

    public void writeAll(OutputStream output, Collection<T> collection) throws IOException {

        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(schema);
        try (DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(datumWriter)) {
            dataFileWriter.create(schema, output);
            for (T entity : collection) {
                GenericRecord genericRecord = new GenericData.Record(schema);
                for (ColumnInfo columnInfo : avroConfiguration.getEntityInfo().getColumns()) {
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

}
