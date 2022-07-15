package org.jfleet.avro;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.util.Utf8;
import org.jfleet.EntityFieldType.FieldTypeEnum;
import org.jfleet.EntityInfo;
import org.jfleet.EntityInfoBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class AvroWriterTest {
    @Test
    void shouldConvertEntityInfoWithStringTypesToAvro() throws IOException {
        EntityInfo entityInfo = new EntityInfoBuilder<>(TestEntity.class)
                .addColumn("foo", FieldTypeEnum.STRING, TestEntity::getFooString)
                .build();

        AvroConfiguration avroConfiguration = new AvroConfiguration(entityInfo);
        AvroWriter<TestEntity> avroWriter = new AvroWriter<>(avroConfiguration);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        TestEntity testEntity = new TestEntity();
        testEntity.setFooString("foo");
        avroWriter.writeAll(outputStream, Arrays.asList(testEntity));

        assertNotNull(avroWriter);
        assertTrue(outputStream.size() > 0);

        try (FileOutputStream fos = new FileOutputStream("/tmp/foo.avro")) {
            fos.write(outputStream.toByteArray());
        }

        DatumReader<GenericRecord> datumReader = new GenericDatumReader<>();

        try (DataFileReader<GenericRecord> dataFileReader = new DataFileReader<>(new File("/tmp/foo.avro"), datumReader)) {
            assertTrue(dataFileReader.hasNext());
            GenericRecord genericRecord = dataFileReader.next();
            assertEquals(new Utf8("foo"), genericRecord.get("foo"));
        }
    }

    @Test
    void shouldConvertEntityInfoWithNullStringType() throws IOException {
        EntityInfo entityInfo = new EntityInfoBuilder<>(TestEntity.class)
                .addColumn("foo", FieldTypeEnum.STRING, TestEntity::getFooString)
                .build();

        AvroConfiguration avroConfiguration = new AvroConfiguration(entityInfo);
        AvroWriter<TestEntity> avroWriter = new AvroWriter<>(avroConfiguration);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        TestEntity testEntity = new TestEntity();
        avroWriter.writeAll(outputStream, Arrays.asList(testEntity));

        assertNotNull(avroWriter);
        assertTrue(outputStream.size() > 0);

        try (FileOutputStream fos = new FileOutputStream("/tmp/foo.avro")) {
            fos.write(outputStream.toByteArray());
        }

        DatumReader<GenericRecord> datumReader = new GenericDatumReader<>();

        try (DataFileReader<GenericRecord> dataFileReader = new DataFileReader<>(new File("/tmp/foo.avro"), datumReader)) {
            assertTrue(dataFileReader.hasNext());
            GenericRecord genericRecord = dataFileReader.next();
            assertNull(genericRecord.get("foo"));
        }
    }

    @Test
    void shouldConvertEntityInfoWithNumericTypesToAvro() throws IOException {
        EntityInfo entityInfo = new EntityInfoBuilder<>(TestEntity.class)
                .addColumn("fooInt", FieldTypeEnum.INT, TestEntity::getFooInt)
                .addColumn("fooShort", FieldTypeEnum.SHORT, TestEntity::getFooShort)
                .addColumn("fooByte", FieldTypeEnum.BYTE, TestEntity::getFooByte)
                .addColumn("fooDouble", FieldTypeEnum.DOUBLE, TestEntity::getFooDouble)
                .addColumn("fooLong", FieldTypeEnum.LONG, TestEntity::getFooLong)
                .addColumn("fooFloat", FieldTypeEnum.FLOAT, TestEntity::getFooFloat)
                .build();

        AvroConfiguration avroConfiguration = new AvroConfiguration(entityInfo);
        AvroWriter<TestEntity> avroWriter = new AvroWriter<>(avroConfiguration);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        TestEntity testEntity = new TestEntity();
        testEntity.setFooInt(1);
        testEntity.setFooShort((short) 2);
        testEntity.setFooByte((byte) 3);
        testEntity.setFooDouble(10.2);
        testEntity.setFooLong(100L);
        testEntity.setFooFloat(50.1F);
        avroWriter.writeAll(outputStream, Arrays.asList(testEntity));

        assertNotNull(avroWriter);
        assertTrue(outputStream.size() > 0);

        try (FileOutputStream fos = new FileOutputStream("/tmp/foo.avro")) {
            fos.write(outputStream.toByteArray());
        }

        DatumReader<GenericRecord> datumReader = new GenericDatumReader<>();

        try (DataFileReader<GenericRecord> dataFileReader = new DataFileReader<>(new File("/tmp/foo.avro"), datumReader)) {
            assertTrue(dataFileReader.hasNext());
            GenericRecord genericRecord = dataFileReader.next();
            assertEquals(1, genericRecord.get("fooInt"));
            assertEquals(2, genericRecord.get("fooShort"));
            assertEquals(3, genericRecord.get("fooByte"));
            assertEquals(10.2, genericRecord.get("fooDouble"));
            assertEquals(100L, genericRecord.get("fooLong"));
            assertEquals(50.1F, genericRecord.get("fooFloat"));
        }
    }

    @Test
    void shouldConvertEntityInfoWithNullNumericTypesToAvro() throws IOException {
        EntityInfo entityInfo = new EntityInfoBuilder<>(TestEntity.class)
                .addColumn("fooInt", FieldTypeEnum.INT, TestEntity::getFooInt)
                .addColumn("fooShort", FieldTypeEnum.SHORT, TestEntity::getFooShort)
                .addColumn("fooByte", FieldTypeEnum.BYTE, TestEntity::getFooByte)
                .addColumn("fooDouble", FieldTypeEnum.DOUBLE, TestEntity::getFooDouble)
                .addColumn("fooLong", FieldTypeEnum.LONG, TestEntity::getFooLong)
                .addColumn("fooFloat", FieldTypeEnum.FLOAT, TestEntity::getFooFloat)
                .build();

        AvroConfiguration avroConfiguration = new AvroConfiguration(entityInfo);
        AvroWriter<TestEntity> avroWriter = new AvroWriter<>(avroConfiguration);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        TestEntity testEntity = new TestEntity();
        avroWriter.writeAll(outputStream, Arrays.asList(testEntity));

        assertNotNull(avroWriter);
        assertTrue(outputStream.size() > 0);

        try (FileOutputStream fos = new FileOutputStream("/tmp/foo.avro")) {
            fos.write(outputStream.toByteArray());
        }

        DatumReader<GenericRecord> datumReader = new GenericDatumReader<>();

        try (DataFileReader<GenericRecord> dataFileReader = new DataFileReader<>(new File("/tmp/foo.avro"), datumReader)) {
            assertTrue(dataFileReader.hasNext());
            GenericRecord genericRecord = dataFileReader.next();
            assertNull(genericRecord.get("fooInt"));
            assertNull(genericRecord.get("fooShort"));
            assertNull(genericRecord.get("fooByte"));
            assertNull(genericRecord.get("fooDouble"));
            assertNull(genericRecord.get("fooLong"));
            assertNull(genericRecord.get("fooFloat"));
        }
    }

    @Test
    void shouldThrowUnsupportedTypeException() {
        EntityInfo entityInfo = new EntityInfoBuilder<>(TestEntity.class)
                .addColumn("foo", FieldTypeEnum.BIGDECIMAL, a -> BigDecimal.ZERO)
                .build();

        AvroConfiguration avroConfiguration = new AvroConfiguration(entityInfo);
        AvroWriter<TestEntity> avroWriter = new AvroWriter<>(avroConfiguration);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        TestEntity testEntity = new TestEntity();
        assertThrows(UnsupportedTypeException.class, () -> avroWriter.writeAll(outputStream, Arrays.asList(testEntity)));
    }
}
