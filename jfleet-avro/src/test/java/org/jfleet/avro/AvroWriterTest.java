package org.jfleet.avro;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.util.Utf8;
import org.jfleet.EntityFieldType;
import org.jfleet.EntityInfo;
import org.jfleet.EntityInfoBuilder;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class AvroWriterTest {
    @Test
    void shouldConvertEntityInfoWithStringTypesToAvro() throws IOException {
        EntityInfo entityInfo = new EntityInfoBuilder<>(TestEntity.class)
                .addColumn("foo", EntityFieldType.FieldTypeEnum.STRING, TestEntity::getFoo)
                .build();

        AvroConfiguration avroConfiguration = new AvroConfiguration(entityInfo);
        AvroWriter<TestEntity> avroWriter = new AvroWriter<>(avroConfiguration);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        TestEntity testEntity = new TestEntity("foo", null, null);
        avroWriter.writeAll(outputStream, Arrays.asList(testEntity));

        assertNotNull(avroWriter);
        assertTrue(outputStream.size() > 0);

        try (FileOutputStream fos = new FileOutputStream("/tmp/foo.avro")) {
            fos.write(outputStream.toByteArray());
        }

        Schema schema = SchemaBuilder.record("TestEntity").namespace("org.jfleet.avro")
                .fields().requiredString("foo").endRecord();
        DatumReader<GenericRecord> datumReader = new GenericDatumReader<>(schema);

        try (DataFileReader<GenericRecord> dataFileReader = new DataFileReader<>(new File("/tmp/foo.avro"), datumReader)) {
            assertTrue(dataFileReader.hasNext());
            GenericRecord genericRecord = dataFileReader.next();
            assertEquals(new Utf8("foo"), genericRecord.get("foo"));
        }
    }
}
