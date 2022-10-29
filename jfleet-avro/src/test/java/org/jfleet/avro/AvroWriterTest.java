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
import static org.jfleet.avro.TestEntityWithEnum.WeekDays.FRIDAY;
import static org.jfleet.avro.TestEntityWithEnum.WeekDays.SATURDAY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.util.Utf8;
import org.jfleet.EntityInfo;
import org.jfleet.EntityInfoBuilder;
import org.junit.jupiter.api.Test;

class AvroWriterTest {

    @Test
    void shouldFillOutputStream() throws IOException {
        EntityInfo entityInfo = new EntityInfoBuilder<>(TestEntity.class)
                .addColumn("foo", STRING, TestEntity::getFooString)
                .build();
        TestEntity testEntity = new TestEntity();
        testEntity.setFooString("foo");

        AvroConfiguration<TestEntity> avroConfiguration = new AvroConfiguration<>(entityInfo);
        AvroWriter<TestEntity> avroWriter = new AvroWriter<>(avroConfiguration);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        avroWriter.writeAll(outputStream, Arrays.asList(testEntity));
        assertTrue(outputStream.size() > 0);
    }

    @Test
    void shouldConvertEntityInfoWithStringTypesToAvro() throws IOException {
        EntityInfo entityInfo = new EntityInfoBuilder<>(TestEntity.class)
                .addColumn("foo", STRING, TestEntity::getFooString)
                .build();
        TestEntity testEntity = new TestEntity();
        testEntity.setFooString("foo");

        try (DataFileReader<GenericRecord> dataFileReader = serializeAndRead(entityInfo, testEntity)) {
            assertTrue(dataFileReader.hasNext());
            GenericRecord genericRecord = dataFileReader.next();
            assertEquals(new Utf8("foo"), genericRecord.get("foo"));
        }
    }

    @Test
    void shouldConvertEntityInfoWithNullStringType() throws IOException {
        EntityInfo entityInfo = new EntityInfoBuilder<>(TestEntity.class)
                .addColumn("foo", STRING, TestEntity::getFooString)
                .build();

        TestEntity testEntity = new TestEntity();

        try (DataFileReader<GenericRecord> dataFileReader = serializeAndRead(entityInfo, testEntity)) {
            assertTrue(dataFileReader.hasNext());
            GenericRecord genericRecord = dataFileReader.next();
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

        try (DataFileReader<GenericRecord> dataFileReader = serializeAndRead(entityInfo, testEntity)) {
            assertTrue(dataFileReader.hasNext());
            GenericRecord genericRecord = dataFileReader.next();
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

        try (DataFileReader<GenericRecord> dataFileReader = serializeAndRead(entityInfo, testEntity)) {
            assertTrue(dataFileReader.hasNext());
            GenericRecord genericRecord = dataFileReader.next();
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

        try (DataFileReader<GenericRecord> dataFileReader = serializeAndRead(entityInfo, testEntity)) {
            Schema schema = dataFileReader.getSchema();
            assertFieldIsNotUnionType(schema, "fooBoolean");
            assertFieldIsNotUnionType(schema, "fooInt");
            assertFieldIsNotUnionType(schema, "fooShort");
            assertFieldIsNotUnionType(schema, "fooByte");
            assertFieldIsNotUnionType(schema, "fooDouble");
            assertFieldIsNotUnionType(schema, "fooLong");
            assertFieldIsNotUnionType(schema, "fooFloat");
        }
    }

    private static void assertFieldIsNotUnionType(Schema schema, String fieldName) {
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

        try (DataFileReader<GenericRecord> dataFileReader = serializeAndRead(entityInfo, testEntity)) {
            Schema schema = dataFileReader.getSchema();
            assertUnionFieldContainsNullType(schema, "fooBoolean");
            assertUnionFieldContainsNullType(schema, "fooInt");
            assertUnionFieldContainsNullType(schema, "fooShort");
            assertUnionFieldContainsNullType(schema, "fooByte");
            assertUnionFieldContainsNullType(schema, "fooDouble");
            assertUnionFieldContainsNullType(schema, "fooLong");
            assertUnionFieldContainsNullType(schema, "fooFloat");
        }
    }

    private static void assertUnionFieldContainsNullType(Schema schema, String fieldName) {
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

        try (DataFileReader<GenericRecord> dataFileReader = serializeAndRead(entityInfo, testEntity)) {
            assertTrue(dataFileReader.hasNext());
            GenericRecord genericRecord = dataFileReader.next();
            assertNull(genericRecord.get("fooBoolean"));
        }
    }

    @Test
    void shouldConvertEntityInfoWithNumericTypesToAvro() throws IOException {
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

        try (DataFileReader<GenericRecord> dataFileReader = serializeAndRead(entityInfo, testEntity)) {
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
                .addColumn("fooInt", INT, TestEntity::getFooInt)
                .addColumn("fooShort", SHORT, TestEntity::getFooShort)
                .addColumn("fooByte", BYTE, TestEntity::getFooByte)
                .addColumn("fooDouble", DOUBLE, TestEntity::getFooDouble)
                .addColumn("fooLong", LONG, TestEntity::getFooLong)
                .addColumn("fooFloat", FLOAT, TestEntity::getFooFloat)
                .build();

        TestEntity testEntity = new TestEntity();

        try (DataFileReader<GenericRecord> dataFileReader = serializeAndRead(entityInfo, testEntity)) {
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
                .addColumn("foo", BIGDECIMAL, a -> BigDecimal.ZERO)
                .build();

        AvroConfiguration<TestEntity> avroConfiguration = new AvroConfiguration<>(entityInfo);
        assertThrows(UnsupportedTypeException.class, () -> new AvroWriter<>(avroConfiguration));
    }

    @Test
    void shouldConvertEnumTypesToAvro() throws IOException {
        EntityInfo entityInfo = new EntityInfoBuilder<>(TestEntityWithEnum.class)
                .addColumn("foo", ENUMORDINAL, TestEntityWithEnum::getFoo)
                .addColumn("bar", ENUMSTRING, TestEntityWithEnum::getBar)
                .build();

        TestEntityWithEnum testEntityWithEnum = new TestEntityWithEnum();
        testEntityWithEnum.setFoo(FRIDAY);
        testEntityWithEnum.setBar(SATURDAY);

        try (DataFileReader<GenericRecord> dataFileReader = serializeAndRead(entityInfo, testEntityWithEnum)) {
            assertTrue(dataFileReader.hasNext());
            GenericRecord genericRecord = dataFileReader.next();
            assertEquals(4, genericRecord.get("foo"));
            assertEquals(new Utf8("SATURDAY"), genericRecord.get("bar"));
        }
    }

    @Test
    void shouldConvertNullableEnumTypesToAvro() throws IOException {
        EntityInfo entityInfo = new EntityInfoBuilder<>(TestEntityWithEnum.class)
                .addColumn("foo", ENUMORDINAL, TestEntityWithEnum::getFoo)
                .addColumn("bar", ENUMSTRING, TestEntityWithEnum::getBar)
                .build();

        TestEntityWithEnum testEntityWithEnum = new TestEntityWithEnum();

        try (DataFileReader<GenericRecord> dataFileReader = serializeAndRead(entityInfo, testEntityWithEnum)) {
            assertTrue(dataFileReader.hasNext());
            GenericRecord genericRecord = dataFileReader.next();
            assertNull(genericRecord.get("foo"));
            assertNull(genericRecord.get("bar"));
        }
    }

    private <T> DataFileReader<GenericRecord> serializeAndRead(EntityInfo entityInfo, T testEntity) throws IOException {
        AvroConfiguration<T> avroConfiguration = new AvroConfiguration<>(entityInfo);
        AvroWriter<T> avroWriter = new AvroWriter<>(avroConfiguration);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        avroWriter.writeAll(outputStream, Arrays.asList(testEntity));

        try (FileOutputStream fos = new FileOutputStream("/tmp/foo.avro")) {
            fos.write(outputStream.toByteArray());
        }

        DatumReader<GenericRecord> datumReader = new GenericDatumReader<>();
        return new DataFileReader<>(new File("/tmp/foo.avro"), datumReader);
    }

}
