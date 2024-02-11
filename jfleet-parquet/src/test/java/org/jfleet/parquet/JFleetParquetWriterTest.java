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

import static org.jfleet.EntityFieldType.FieldTypeEnum.BIGDECIMAL;
import static org.jfleet.EntityFieldType.FieldTypeEnum.BOOLEAN;
import static org.jfleet.EntityFieldType.FieldTypeEnum.BYTE;
import static org.jfleet.EntityFieldType.FieldTypeEnum.DOUBLE;
import static org.jfleet.EntityFieldType.FieldTypeEnum.ENUMORDINAL;
import static org.jfleet.EntityFieldType.FieldTypeEnum.ENUMSTRING;
import static org.jfleet.EntityFieldType.FieldTypeEnum.FLOAT;
import static org.jfleet.EntityFieldType.FieldTypeEnum.INT;
import static org.jfleet.EntityFieldType.FieldTypeEnum.LONG;
import static org.jfleet.EntityFieldType.FieldTypeEnum.SHORT;
import static org.jfleet.EntityFieldType.FieldTypeEnum.STRING;
import static org.jfleet.parquet.TestEntityWithEnum.WeekDays.FRIDAY;
import static org.jfleet.parquet.TestEntityWithEnum.WeekDays.SATURDAY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.util.Utf8;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.ParquetReadOptions;
import org.apache.parquet.avro.AvroParquetReader;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.util.HadoopInputFile;
import org.apache.parquet.io.InputFile;
import org.apache.parquet.io.InvalidRecordException;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.Type.Repetition;
import org.jfleet.EntityInfo;
import org.jfleet.EntityInfoBuilder;
import org.junit.jupiter.api.Test;

class JFleetParquetWriterTest {

    @Test
    public void shouldWrite() throws IOException {
        EntityInfo entityInfo = new EntityInfoBuilder<>(TestEntity.class)
                .addColumn("fooInt", INT, TestEntity::getFooInt)
                .addColumn("fooShort", SHORT, TestEntity::getFooShort)
                .addColumn("fooByte", BYTE, TestEntity::getFooByte)
                .addColumn("fooDouble", DOUBLE, TestEntity::getFooDouble)
                .addColumn("fooLong", LONG, TestEntity::getFooLong)
                .addColumn("fooFloat", FLOAT, TestEntity::getFooFloat)
                .build();

        TestEntity testEntity = new TestEntity();
        testEntity.setFooInt(1);
        testEntity.setFooShort((short) 2);
        testEntity.setFooByte((byte) 3);
        testEntity.setFooDouble(10.2);
        testEntity.setFooLong(100L);
        testEntity.setFooFloat(50.1F);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ParquetConfiguration<TestEntity> parquetConfiguration = new ParquetConfiguration.Builder<TestEntity>(
                outputStream, entityInfo).build();
        try (JFleetParquetWriter<TestEntity> parquetWriter = new JFleetParquetWriter<>(parquetConfiguration)) {
            parquetWriter.write(testEntity);
        }

    }

    @Test
    void shouldFillOutputStream() throws IOException {
        EntityInfo entityInfo = new EntityInfoBuilder<>(TestEntity.class)
                .addColumn("foo", STRING, TestEntity::getFooString)
                .build();
        TestEntity testEntity = new TestEntity();
        testEntity.setFooString("foo");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ParquetConfiguration<TestEntity> parquetConfiguration = new ParquetConfiguration.Builder<TestEntity>(
                outputStream, entityInfo).build();
        try (JFleetParquetWriter<TestEntity> parquetWriter = new JFleetParquetWriter<>(parquetConfiguration)) {
            parquetWriter.write(testEntity);
        }
        assertTrue(outputStream.size() > 0);
    }

    @Test
    void shouldConvertEntityInfoWithStringTypesToParquet() throws IOException {
        EntityInfo entityInfo = new EntityInfoBuilder<>(TestEntity.class)
                .addColumn("foo", STRING, TestEntity::getFooString)
                .build();
        TestEntity testEntity = new TestEntity();
        testEntity.setFooString("foo");

        try (ParquetReader<GenericRecord> parquetReader = serializeAndRead(entityInfo, testEntity)) {
            GenericRecord genericRecord = parquetReader.read();
            assertNotNull(genericRecord);
            assertEquals(new Utf8("foo"), genericRecord.get("foo"));
        }
    }

    @Test
    void shouldConvertEntityInfoWithNullStringType() throws IOException {
        EntityInfo entityInfo = new EntityInfoBuilder<>(TestEntity.class)
                .addColumn("foo", STRING, TestEntity::getFooString)
                .build();

        TestEntity testEntity = new TestEntity();

        try (ParquetReader<GenericRecord> parquetReader = serializeAndRead(entityInfo, testEntity)) {
            GenericRecord genericRecord = parquetReader.read();
            assertNotNull(genericRecord);
            assertNull(genericRecord.get("foo"));
        }
    }

    @Test
    void shouldConvertEntityInfoWithBooleanType() throws IOException {
        EntityInfo entityInfo = new EntityInfoBuilder<>(TestEntity.class)
                .addColumn("fooBoolean", BOOLEAN, TestEntity::getFooBoolean)
                .build();

        TestEntity testEntity = new TestEntity();
        testEntity.setFooBoolean(true);

        try (ParquetReader<GenericRecord> parquetReader = serializeAndRead(entityInfo, testEntity)) {
            GenericRecord genericRecord = parquetReader.read();
            assertNotNull(genericRecord);
            assertTrue((Boolean) genericRecord.get("fooBoolean"));
        }
    }

    @Test
    void shouldConvertEntityWithPrimitives() throws IOException {
        EntityInfo entityInfo = new EntityInfoBuilder<>(TestEntityWithPrimitives.class)
                .addColumn("fooBoolean", BOOLEAN, true, TestEntityWithPrimitives::isFooBoolean)
                .addColumn("fooInt", INT, true, TestEntityWithPrimitives::getFooInt)
                .addColumn("fooShort", SHORT, true, TestEntityWithPrimitives::getFooShort)
                .addColumn("fooByte", BYTE, true, TestEntityWithPrimitives::getFooByte)
                .addColumn("fooDouble", DOUBLE, true, TestEntityWithPrimitives::getFooDouble)
                .addColumn("fooLong", LONG, true, TestEntityWithPrimitives::getFooLong)
                .addColumn("fooFloat", FLOAT, true, TestEntityWithPrimitives::getFooFloat)
                .build();

        TestEntityWithPrimitives testEntity = new TestEntityWithPrimitives();
        testEntity.setFooBoolean(true);
        testEntity.setFooInt(1);
        testEntity.setFooShort((short) 2);
        testEntity.setFooByte((byte) 3);
        testEntity.setFooDouble(10.2);
        testEntity.setFooLong(100L);
        testEntity.setFooFloat(50.1F);

        try (ParquetReader<GenericRecord> parquetReader = serializeAndRead(entityInfo, testEntity)) {
            GenericRecord genericRecord = parquetReader.read();
            assertNotNull(genericRecord);
            assertTrue((Boolean) genericRecord.get("fooBoolean"));
            assertEquals(1, genericRecord.get("fooInt"));
            assertEquals(2, genericRecord.get("fooShort"));
            assertEquals(3, genericRecord.get("fooByte"));
            assertEquals(10.2, genericRecord.get("fooDouble"));
            assertEquals(100L, genericRecord.get("fooLong"));
            assertEquals(50.1F, genericRecord.get("fooFloat"));
        }
    }

    @Test
    void shouldCreateSchemaWithoutNullTypesForPrimitives() throws IOException {
        EntityInfo entityInfo = new EntityInfoBuilder<>(TestEntityWithPrimitives.class)
                .addColumn("fooBoolean", BOOLEAN, true, TestEntityWithPrimitives::isFooBoolean)
                .addColumn("fooInt", INT, true, TestEntityWithPrimitives::getFooInt)
                .addColumn("fooShort", SHORT, true, TestEntityWithPrimitives::getFooShort)
                .addColumn("fooByte", BYTE, true, TestEntityWithPrimitives::getFooByte)
                .addColumn("fooDouble", DOUBLE, true, TestEntityWithPrimitives::getFooDouble)
                .addColumn("fooLong", LONG, true, TestEntityWithPrimitives::getFooLong)
                .addColumn("fooFloat", FLOAT, true, TestEntityWithPrimitives::getFooFloat)
                .build();

        TestEntityWithPrimitives testEntity = new TestEntityWithPrimitives();
        testEntity.setFooBoolean(true);
        testEntity.setFooInt(1);
        testEntity.setFooShort((short) 2);
        testEntity.setFooByte((byte) 3);
        testEntity.setFooDouble(10.2);
        testEntity.setFooLong(100L);
        testEntity.setFooFloat(50.1F);

        try (ParquetReader<GenericRecord> parquetReader = serializeAndRead(entityInfo, testEntity)) {
            Schema schema = parquetReader.read().getSchema();
            assertFieldIsNotUnionType(schema, "fooBoolean");
            assertFieldIsNotUnionType(schema, "fooInt");
            assertFieldIsNotUnionType(schema, "fooShort");
            assertFieldIsNotUnionType(schema, "fooByte");
            assertFieldIsNotUnionType(schema, "fooDouble");
            assertFieldIsNotUnionType(schema, "fooLong");
            assertFieldIsNotUnionType(schema, "fooFloat");
        }
    }

    private void assertFieldIsNotUnionType(Schema schema, String fieldName) {
        assertNotEquals(Schema.Type.UNION, schema.getField(fieldName).schema().getType());
    }

    @Test
    void shouldCreateSchemaWithNullTypesForObjects() throws IOException {
        EntityInfo entityInfo = new EntityInfoBuilder<>(TestEntity.class)
                .addColumn("fooBoolean", BOOLEAN, TestEntity::getFooBoolean)
                .addColumn("fooInt", INT, TestEntity::getFooInt)
                .addColumn("fooShort", SHORT, TestEntity::getFooShort)
                .addColumn("fooByte", BYTE, TestEntity::getFooByte)
                .addColumn("fooDouble", DOUBLE, TestEntity::getFooDouble)
                .addColumn("fooLong", LONG, TestEntity::getFooLong)
                .addColumn("fooFloat", FLOAT, TestEntity::getFooFloat)
                .build();

        TestEntity testEntity = new TestEntity();
        testEntity.setFooBoolean(true);
        testEntity.setFooInt(1);
        testEntity.setFooShort((short) 2);
        testEntity.setFooByte((byte) 3);
        testEntity.setFooDouble(10.2);
        testEntity.setFooLong(100L);
        testEntity.setFooFloat(50.1F);

        try (ParquetReader<GenericRecord> parquetReader = serializeAndRead(entityInfo, testEntity)) {
            Schema schema = parquetReader.read().getSchema();
            assertUnionFieldContainsNullType(schema, "fooBoolean");
            assertUnionFieldContainsNullType(schema, "fooInt");
            assertUnionFieldContainsNullType(schema, "fooShort");
            assertUnionFieldContainsNullType(schema, "fooByte");
            assertUnionFieldContainsNullType(schema, "fooDouble");
            assertUnionFieldContainsNullType(schema, "fooLong");
            assertUnionFieldContainsNullType(schema, "fooFloat");
        }
    }

    private void assertUnionFieldContainsNullType(Schema schema, String fieldName) {
        Schema fieldSchema = schema.getField(fieldName).schema();
        assertEquals(Schema.Type.UNION, fieldSchema.getType());
        assertTrue(fieldSchema.getTypes().contains(Schema.create(Schema.Type.NULL)));
    }

    @Test
    void shouldConvertEntityInfoWithNullBoolean() throws IOException {
        EntityInfo entityInfo = new EntityInfoBuilder<>(TestEntity.class)
                .addColumn("fooBoolean", BOOLEAN, TestEntity::getFooBoolean)
                .build();

        TestEntity testEntity = new TestEntity();

        try (ParquetReader<GenericRecord> parquetReader = serializeAndRead(entityInfo, testEntity)) {
            GenericRecord genericRecord = parquetReader.read();
            assertNotNull(genericRecord);
            assertNull(genericRecord.get("fooBoolean"));
        }
    }

    @Test
    void shouldConvertEntityInfoWithNonNullString() throws IOException {
        EntityInfo entityInfo = new EntityInfoBuilder<>(TestEntity.class)
                .addColumn("fooString", STRING, true, TestEntity::getFooString)
                .build();

        TestEntity testEntity = new TestEntity();
        testEntity.setFooString("some value 1");

        try (FileOutputStream outputStream = new FileOutputStream("/tmp/foo.parquet")) {
            ParquetConfiguration<TestEntity> parquetConfiguration = new ParquetConfiguration.Builder<TestEntity>(
                    outputStream, entityInfo).build();
            try (JFleetParquetWriter<TestEntity> parquetWriter = new JFleetParquetWriter<>(parquetConfiguration)) {
                parquetWriter.write(testEntity);
            }
        }
        InputFile file = HadoopInputFile.fromPath(new Path("/tmp/foo.parquet"), new Configuration());
        try (ParquetFileReader reader = new ParquetFileReader(file, ParquetReadOptions.builder().build())) {
            MessageType schema = reader.getFileMetaData().getSchema();
            assertTrue(schema.containsField("fooString"));
            assertTrue(schema.getFields().get(0).isRepetition(Repetition.REQUIRED));
        }

        try (ParquetReader<GenericRecord> parquetReader = serializeAndRead(entityInfo, testEntity)) {
            GenericRecord genericRecord = parquetReader.read();
            assertEquals("some value 1", genericRecord.get("fooString").toString());
        }

        assertThrows(InvalidRecordException.class, () -> {
            TestEntity testEntityNull = new TestEntity();
            try (FileOutputStream outputStream = new FileOutputStream("/tmp/foo.parquet")) {
                ParquetConfiguration<TestEntity> parquetConfiguration = new ParquetConfiguration.Builder<TestEntity>(
                        outputStream, entityInfo).enableValidation().build();
                try (JFleetParquetWriter<TestEntity> parquetWriter = new JFleetParquetWriter<>(parquetConfiguration)) {
                    parquetWriter.write(testEntityNull);
                }
            }
        });
    }

    @Test
    void valuesWithNull() throws IOException {
        EntityInfo entityInfo = new EntityInfoBuilder<>(TestEntity.class)
                .addColumn("fooString", STRING, false, TestEntity::getFooString)
                .addColumn("fooInt", INT, false, TestEntity::getFooInt)
                .build();

        TestEntity testEntity1 = new TestEntity();
        testEntity1.setFooString("some value 1");
        testEntity1.setFooInt(null);
        TestEntity testEntity2 = new TestEntity();
        testEntity2.setFooString(null);
        testEntity2.setFooInt(2);

        try (ParquetReader<GenericRecord> parquetReader = serializeAndRead(entityInfo, testEntity1, testEntity2)) {
            GenericRecord record1 = parquetReader.read();
            assertNotNull(record1);
            assertEquals("some value 1", record1.get("fooString").toString());
            assertNull(record1.get("fooInt"));
            GenericRecord record2 = parquetReader.read();
            assertNotNull(record2);
            assertNull(record2.get("fooString"));
            assertEquals(2, record2.get("fooInt"));
        }
    }

    @Test
    void shouldConvertEntityInfoWithNumericTypesToParquet() throws IOException {
        EntityInfo entityInfo = new EntityInfoBuilder<>(TestEntity.class)
                .addColumn("fooInt", INT, TestEntity::getFooInt)
                .addColumn("fooShort", SHORT, TestEntity::getFooShort)
                .addColumn("fooByte", BYTE, TestEntity::getFooByte)
                .addColumn("fooDouble", DOUBLE, TestEntity::getFooDouble)
                .addColumn("fooLong", LONG, TestEntity::getFooLong)
                .addColumn("fooFloat", FLOAT, TestEntity::getFooFloat)
                .build();

        TestEntity testEntity = new TestEntity();
        testEntity.setFooInt(1);
        testEntity.setFooShort((short) 2);
        testEntity.setFooByte((byte) 3);
        testEntity.setFooDouble(10.2);
        testEntity.setFooLong(100L);
        testEntity.setFooFloat(50.1F);

        try (ParquetReader<GenericRecord> parquetReader = serializeAndRead(entityInfo, testEntity)) {
            GenericRecord genericRecord = parquetReader.read();
            assertNotNull(genericRecord);
            assertEquals(1, genericRecord.get("fooInt"));
            assertEquals(2, genericRecord.get("fooShort"));
            assertEquals(3, genericRecord.get("fooByte"));
            assertEquals(10.2, genericRecord.get("fooDouble"));
            assertEquals(100L, genericRecord.get("fooLong"));
            assertEquals(50.1F, genericRecord.get("fooFloat"));
        }
    }

    @Test
    void shouldConvertEntityInfoWithNullNumericTypesToParquet() throws IOException {
        EntityInfo entityInfo = new EntityInfoBuilder<>(TestEntity.class)
                .addColumn("fooInt", INT, TestEntity::getFooInt)
                .addColumn("fooShort", SHORT, TestEntity::getFooShort)
                .addColumn("fooByte", BYTE, TestEntity::getFooByte)
                .addColumn("fooDouble", DOUBLE, TestEntity::getFooDouble)
                .addColumn("fooLong", LONG, TestEntity::getFooLong)
                .addColumn("fooFloat", FLOAT, TestEntity::getFooFloat)
                .build();

        TestEntity testEntity = new TestEntity();

        try (ParquetReader<GenericRecord> parquetReader = serializeAndRead(entityInfo, testEntity)) {
            GenericRecord genericRecord = parquetReader.read();
            assertNotNull(genericRecord);
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
                .addColumn("foo", BIGDECIMAL, a -> BigDecimal.ZERO)
                .build();

        ParquetConfiguration<TestEntity> parquetConfiguration = new ParquetConfiguration.Builder<TestEntity>(
                new ByteArrayOutputStream(), entityInfo).build();

        assertThrows(UnsupportedTypeException.class, () -> {
            try (JFleetParquetWriter<TestEntity> writer = new JFleetParquetWriter<>(parquetConfiguration)) {
                writer.write(new TestEntity());
            }
        });
    }

    @Test
    void shouldConvertEnumTypesToParquet() throws IOException {
        EntityInfo entityInfo = new EntityInfoBuilder<>(TestEntityWithEnum.class)
                .addColumn("foo", ENUMORDINAL, TestEntityWithEnum::getFoo)
                .addColumn("bar", ENUMSTRING, TestEntityWithEnum::getBar)
                .build();

        TestEntityWithEnum testEntityWithEnum = new TestEntityWithEnum();
        testEntityWithEnum.setFoo(FRIDAY);
        testEntityWithEnum.setBar(SATURDAY);

        try (ParquetReader<GenericRecord> parquetReader = serializeAndRead(entityInfo, testEntityWithEnum)) {
            GenericRecord genericRecord = parquetReader.read();
            assertNotNull(genericRecord);
            assertEquals(4, genericRecord.get("foo"));
            assertEquals(new Utf8("SATURDAY"), genericRecord.get("bar"));
        }
    }

    @Test
    void shouldConvertNullableEnumTypesToParquet() throws IOException {
        EntityInfo entityInfo = new EntityInfoBuilder<>(TestEntityWithEnum.class)
                .addColumn("foo", ENUMORDINAL, TestEntityWithEnum::getFoo)
                .addColumn("bar", ENUMSTRING, TestEntityWithEnum::getBar)
                .build();

        TestEntityWithEnum testEntityWithEnum = new TestEntityWithEnum();

        try (ParquetReader<GenericRecord> parquetReader = serializeAndRead(entityInfo, testEntityWithEnum)) {
            GenericRecord genericRecord = parquetReader.read();
            assertNotNull(genericRecord);
            assertNull(genericRecord.get("foo"));
            assertNull(genericRecord.get("bar"));
        }
    }

    private <T> ParquetReader<GenericRecord> serializeAndRead(EntityInfo entityInfo, T... testEntity)
            throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream("/tmp/foo.parquet")) {
            ParquetConfiguration<T> parquetConfiguration = new ParquetConfiguration.Builder<T>(outputStream, entityInfo)
                    .withValidation(true)
                    .build();
            try (JFleetParquetWriter<T> parquetWriter = new JFleetParquetWriter<>(parquetConfiguration)) {
                parquetWriter.writeAll(Arrays.asList(testEntity));
            }
        }
        InputFile file = HadoopInputFile.fromPath(new Path("/tmp/foo.parquet"), new Configuration());
        return AvroParquetReader.<GenericRecord>builder(file)
                .withDataModel(GenericData.get())
                .build();
    }

}
