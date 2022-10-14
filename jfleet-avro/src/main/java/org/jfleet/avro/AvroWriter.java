package org.jfleet.avro;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;
import org.jfleet.ColumnInfo;
import org.jfleet.EntityFieldType.FieldTypeEnum;
import org.jfleet.EntityInfo;
import org.jfleet.inspection.JpaEntityInspector;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;

public class AvroWriter<T> {
    private final AvroConfiguration<T> avroConfiguration;
    private final Schema schema;
    private final EntityInfo entityInfo;

    public AvroWriter(AvroConfiguration<T> avroConfiguration) {
        this.avroConfiguration = avroConfiguration;
        entityInfo = getEntityInfo(avroConfiguration);
        schema = new AvroSchemaBuilder(entityInfo).build();
    }

    public void writeAll(OutputStream output, Collection<T> entities) throws IOException {
        writeAll(output, entities.stream());
    }

    public void writeAll(OutputStream output, Stream<T> entities) throws IOException {

        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(schema);
        try (DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(datumWriter)) {
            dataFileWriter.create(schema, output);
            Iterator<T> iterator = entities.iterator();
            while (iterator.hasNext()) {
                dataFileWriter.append(buildAvroRecord(iterator.next()));
            }
        }
    }

    private static <T> EntityInfo getEntityInfo(AvroConfiguration<T> config) {
        EntityInfo configEntityInfo = config.getEntityInfo();
        if (configEntityInfo != null) {
            return configEntityInfo;
        }
        return new JpaEntityInspector(config.getClazz()).inspect();
    }

    private GenericRecord buildAvroRecord(T entity) {
        GenericRecord genericRecord = new GenericData.Record(schema);
        for (ColumnInfo columnInfo : entityInfo.getColumns()) {
            Object value = columnInfo.getAccessor().apply(entity);
            if (value != null) {
                Object extractedValue = extractValue(columnInfo, value);
                genericRecord.put(columnInfo.getColumnName(), extractedValue);
            }
        }
        return genericRecord;
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
