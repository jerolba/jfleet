package org.jfleet.avro;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.jfleet.ColumnInfo;
import org.jfleet.EntityInfo;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

public class AvroWriter<T> {
    private AvroConfiguration avroConfiguration;

    public AvroWriter(AvroConfiguration avroConfiguration) {
        this.avroConfiguration = avroConfiguration;
    }

    public void writeAll(OutputStream output, Collection<T> collection) throws IOException {
        EntityInfo entityInfo = avroConfiguration.getEntityInfo();

        SchemaBuilder.FieldAssembler<Schema> fields = SchemaBuilder.record(entityInfo.getEntityClass().getName())
                .namespace(entityInfo.getEntityClass().getPackage().getName()).fields();

        for (ColumnInfo columnInfo : entityInfo.getColumns()) {
            switch (columnInfo.getFieldType().getFieldType()) {
                case STRING:
                    fields = fields.requiredString(columnInfo.getColumnName());
            }
        }

        Schema schema = fields.endRecord();
        System.out.print(schema.toString());
    }
}
