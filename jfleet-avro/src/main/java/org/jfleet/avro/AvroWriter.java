package org.jfleet.avro;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;
import org.jfleet.ColumnInfo;
import org.jfleet.EntityFieldType;
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
                    genericRecord.put(columnInfo.getColumnName(), columnInfo.getAccessor().apply(entity));
                }
                dataFileWriter.append(genericRecord);
            }
        }
    }

    private Schema buildSchema(AvroConfiguration avroConfiguration) {
        EntityInfo entityInfo = avroConfiguration.getEntityInfo();
        SchemaBuilder.FieldAssembler<Schema> fields = SchemaBuilder.record(entityInfo.getEntityClass().getName())
                .namespace(entityInfo.getEntityClass().getPackage().getName()).fields();

        for (ColumnInfo columnInfo : entityInfo.getColumns()) {
            if (columnInfo.getFieldType().getFieldType() == EntityFieldType.FieldTypeEnum.STRING) {
                fields = fields.requiredString(columnInfo.getColumnName());
            }
        }

        return fields.endRecord();
    }
}
