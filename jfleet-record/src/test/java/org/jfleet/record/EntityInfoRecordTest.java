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
package org.jfleet.record;

import static java.nio.file.Files.createTempFile;
import static java.nio.file.Files.readAllLines;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jfleet.record.ColumnNamingStrategy.SNAKE_CASE;
import static org.jfleet.record.EntityInfoRecordBuilder.fromRecord;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jfleet.EntityFieldType;
import org.jfleet.EntityFieldType.FieldTypeEnum;
import org.jfleet.EntityInfo;
import org.jfleet.csv.JFleetCsvWriter;
import org.jfleet.record.annotation.Alias;
import org.jfleet.record.annotation.Ordinal;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class EntityInfoRecordTest {

    enum Category {
        FOO, BAR;
    }

    @Nested
    class TypeMapping {

        @Test
        void mapPrimitiveField() {

            record SomeEntity(byte a, short b, int c, long d, float e, double f, boolean g, char h) {
            }

            EntityInfo entityInfo = fromRecord(SomeEntity.class).build();
            assertThat(columnType(entityInfo, "a"))
                    .isEqualTo(new EntityFieldType(FieldTypeEnum.BYTE, true));
            assertThat(columnType(entityInfo, "b"))
                    .isEqualTo(new EntityFieldType(FieldTypeEnum.SHORT, true));
            assertThat(columnType(entityInfo, "c"))
                    .isEqualTo(new EntityFieldType(FieldTypeEnum.INT, true));
            assertThat(columnType(entityInfo, "d"))
                    .isEqualTo(new EntityFieldType(FieldTypeEnum.LONG, true));
            assertThat(columnType(entityInfo, "e"))
                    .isEqualTo(new EntityFieldType(FieldTypeEnum.FLOAT, true));
            assertThat(columnType(entityInfo, "f"))
                    .isEqualTo(new EntityFieldType(FieldTypeEnum.DOUBLE, true));
            assertThat(columnType(entityInfo, "g"))
                    .isEqualTo(new EntityFieldType(FieldTypeEnum.BOOLEAN, true));
            assertThat(columnType(entityInfo, "h"))
                    .isEqualTo(new EntityFieldType(FieldTypeEnum.CHAR, true));
        }

        @Test
        void mapNonPrimitiveField() {

            record SomeEntity(Byte a, Short b, Integer c, Long d, Float e, Double f, Boolean g, Character h, String i) {
            }

            EntityInfo entityInfo = fromRecord(SomeEntity.class).build();
            assertThat(columnType(entityInfo, "a"))
                    .isEqualTo(new EntityFieldType(FieldTypeEnum.BYTE));
            assertThat(columnType(entityInfo, "b"))
                    .isEqualTo(new EntityFieldType(FieldTypeEnum.SHORT));
            assertThat(columnType(entityInfo, "c"))
                    .isEqualTo(new EntityFieldType(FieldTypeEnum.INT));
            assertThat(columnType(entityInfo, "d"))
                    .isEqualTo(new EntityFieldType(FieldTypeEnum.LONG));
            assertThat(columnType(entityInfo, "e"))
                    .isEqualTo(new EntityFieldType(FieldTypeEnum.FLOAT));
            assertThat(columnType(entityInfo, "f"))
                    .isEqualTo(new EntityFieldType(FieldTypeEnum.DOUBLE));
            assertThat(columnType(entityInfo, "g"))
                    .isEqualTo(new EntityFieldType(FieldTypeEnum.BOOLEAN));
            assertThat(columnType(entityInfo, "h"))
                    .isEqualTo(new EntityFieldType(FieldTypeEnum.CHAR));
            assertThat(columnType(entityInfo, "i"))
                    .isEqualTo(new EntityFieldType(FieldTypeEnum.STRING));
        }

        @Test
        void mapEnumField() {

            record SomeEntity(Category a, @Ordinal Category b) {
            }

            EntityInfo entityInfo = fromRecord(SomeEntity.class).build();
            assertThat(columnType(entityInfo, "a"))
                    .isEqualTo(new EntityFieldType(FieldTypeEnum.ENUMSTRING));
            assertThat(columnType(entityInfo, "b"))
                    .isEqualTo(new EntityFieldType(FieldTypeEnum.ENUMORDINAL));
        }

        @Test
        void dateAndTime() {

            record SomeEntity(Date a, LocalDate b, LocalTime c, LocalDateTime d) {
            }

            EntityInfo entityInfo = fromRecord(SomeEntity.class).build();
            assertThat(columnType(entityInfo, "a"))
                    .isEqualTo(new EntityFieldType(FieldTypeEnum.DATE));
            assertThat(columnType(entityInfo, "b"))
                    .isEqualTo(new EntityFieldType(FieldTypeEnum.LOCALDATE));
            assertThat(columnType(entityInfo, "c"))
                    .isEqualTo(new EntityFieldType(FieldTypeEnum.LOCALTIME));
            assertThat(columnType(entityInfo, "d"))
                    .isEqualTo(new EntityFieldType(FieldTypeEnum.LOCALDATETIME));
        }

        @Test
        void bigNumber() {

            record SomeEntity(BigInteger a, BigDecimal b) {
            }

            EntityInfo entityInfo = fromRecord(SomeEntity.class).build();
            assertThat(columnType(entityInfo, "a"))
                    .isEqualTo(new EntityFieldType(FieldTypeEnum.BIGINTEGER));
            assertThat(columnType(entityInfo, "b"))
                    .isEqualTo(new EntityFieldType(FieldTypeEnum.BIGDECIMAL));
        }

        @Nested
        class Nesting {

            @Test
            void nestedClassesAreNotSupported() {

                record SomeNested(String code, int value) {
                }
                record SomeEntity(String id, SomeNested nested) {
                }

                assertThrowsExactly(JFleetRecordException.class, () -> fromRecord(SomeEntity.class).build());
            }

            @Test
            void collectionsAreNotSupported() {

                record SomeEntity(String id, List<String> value) {
                }

                assertThrowsExactly(JFleetRecordException.class, () -> fromRecord(SomeEntity.class).build());
            }

            @Test
            void mapsAreNotSupported() {

                record SomeEntity(String id, Map<String, Integer> value) {
                }

                assertThrowsExactly(JFleetRecordException.class, () -> fromRecord(SomeEntity.class).build());
            }

        }

    }

    @Nested
    class ColumnMapping {

        record SomeEntity(String someName, Integer some_code, Long ID, Short some_VALUE, Byte OTHER_VALUE,
                @Alias("bar") Float foo) {
        }

        @Nested
        class ByFieldName {

            @Test
            void fieldNameMapping() {

                EntityInfo entityInfo = fromRecord(SomeEntity.class).build();
                assertThat(columnType(entityInfo, "someName"))
                        .isEqualTo(new EntityFieldType(FieldTypeEnum.STRING));
                assertThat(columnType(entityInfo, "some_code"))
                        .isEqualTo(new EntityFieldType(FieldTypeEnum.INT));
                assertThat(columnType(entityInfo, "ID"))
                        .isEqualTo(new EntityFieldType(FieldTypeEnum.LONG));
                assertThat(columnType(entityInfo, "some_VALUE"))
                        .isEqualTo(new EntityFieldType(FieldTypeEnum.SHORT));
                assertThat(columnType(entityInfo, "OTHER_VALUE"))
                        .isEqualTo(new EntityFieldType(FieldTypeEnum.BYTE));
                assertThat(columnType(entityInfo, "bar"))
                        .isEqualTo(new EntityFieldType(FieldTypeEnum.FLOAT));
            }

            @Test
            void upperCase() {
                EntityInfo entityInfo = fromRecord(SomeEntity.class).upperCase().build();
                assertThat(columnType(entityInfo, "SOMENAME"))
                        .isEqualTo(new EntityFieldType(FieldTypeEnum.STRING));
                assertThat(columnType(entityInfo, "SOME_CODE"))
                        .isEqualTo(new EntityFieldType(FieldTypeEnum.INT));
                assertThat(columnType(entityInfo, "ID"))
                        .isEqualTo(new EntityFieldType(FieldTypeEnum.LONG));
                assertThat(columnType(entityInfo, "SOME_VALUE"))
                        .isEqualTo(new EntityFieldType(FieldTypeEnum.SHORT));
                assertThat(columnType(entityInfo, "OTHER_VALUE"))
                        .isEqualTo(new EntityFieldType(FieldTypeEnum.BYTE));
                assertThat(columnType(entityInfo, "bar"))
                        .isEqualTo(new EntityFieldType(FieldTypeEnum.FLOAT));
            }

            @Test
            void lowerCase() {
                EntityInfo entityInfo = fromRecord(SomeEntity.class).lowerCase().build();
                assertThat(columnType(entityInfo, "somename"))
                        .isEqualTo(new EntityFieldType(FieldTypeEnum.STRING));
                assertThat(columnType(entityInfo, "some_code"))
                        .isEqualTo(new EntityFieldType(FieldTypeEnum.INT));
                assertThat(columnType(entityInfo, "id"))
                        .isEqualTo(new EntityFieldType(FieldTypeEnum.LONG));
                assertThat(columnType(entityInfo, "some_value"))
                        .isEqualTo(new EntityFieldType(FieldTypeEnum.SHORT));
                assertThat(columnType(entityInfo, "other_value"))
                        .isEqualTo(new EntityFieldType(FieldTypeEnum.BYTE));
                assertThat(columnType(entityInfo, "bar"))
                        .isEqualTo(new EntityFieldType(FieldTypeEnum.FLOAT));
            }
        }

        @Nested
        class SnakeCase {

            @Test
            void snakeCaseMapping() {
                EntityInfo entityInfo = fromRecord(SomeEntity.class).columnNamingStrategy(SNAKE_CASE).build();
                assertThat(columnType(entityInfo, "some_name"))
                        .isEqualTo(new EntityFieldType(FieldTypeEnum.STRING));
                assertThat(columnType(entityInfo, "some_code"))
                        .isEqualTo(new EntityFieldType(FieldTypeEnum.INT));
                assertThat(columnType(entityInfo, "id"))
                        .isEqualTo(new EntityFieldType(FieldTypeEnum.LONG));
                assertThat(columnType(entityInfo, "some_value"))
                        .isEqualTo(new EntityFieldType(FieldTypeEnum.SHORT));
                assertThat(columnType(entityInfo, "other_value"))
                        .isEqualTo(new EntityFieldType(FieldTypeEnum.BYTE));
                assertThat(columnType(entityInfo, "bar"))
                        .isEqualTo(new EntityFieldType(FieldTypeEnum.FLOAT));
            }

            @Test
            void upperCase() {
                EntityInfo entityInfo = fromRecord(SomeEntity.class).columnNamingStrategy(SNAKE_CASE)
                        .upperCase().build();
                assertThat(columnType(entityInfo, "SOME_NAME"))
                        .isEqualTo(new EntityFieldType(FieldTypeEnum.STRING));
                assertThat(columnType(entityInfo, "SOME_CODE"))
                        .isEqualTo(new EntityFieldType(FieldTypeEnum.INT));
                assertThat(columnType(entityInfo, "ID"))
                        .isEqualTo(new EntityFieldType(FieldTypeEnum.LONG));
                assertThat(columnType(entityInfo, "SOME_VALUE"))
                        .isEqualTo(new EntityFieldType(FieldTypeEnum.SHORT));
                assertThat(columnType(entityInfo, "OTHER_VALUE"))
                        .isEqualTo(new EntityFieldType(FieldTypeEnum.BYTE));
                assertThat(columnType(entityInfo, "bar"))
                        .isEqualTo(new EntityFieldType(FieldTypeEnum.FLOAT));
            }

            @Test
            void lowerCase() {
                EntityInfo entityInfo = fromRecord(SomeEntity.class).columnNamingStrategy(SNAKE_CASE)
                        .lowerCase().build();
                assertThat(columnType(entityInfo, "some_name"))
                        .isEqualTo(new EntityFieldType(FieldTypeEnum.STRING));
                assertThat(columnType(entityInfo, "some_code"))
                        .isEqualTo(new EntityFieldType(FieldTypeEnum.INT));
                assertThat(columnType(entityInfo, "id"))
                        .isEqualTo(new EntityFieldType(FieldTypeEnum.LONG));
                assertThat(columnType(entityInfo, "some_value"))
                        .isEqualTo(new EntityFieldType(FieldTypeEnum.SHORT));
                assertThat(columnType(entityInfo, "other_value"))
                        .isEqualTo(new EntityFieldType(FieldTypeEnum.BYTE));
                assertThat(columnType(entityInfo, "bar"))
                        .isEqualTo(new EntityFieldType(FieldTypeEnum.FLOAT));
            }
        }

    }

    @Test
    void canBeUSedToWriteCSV() throws IOException {

        record SomeEntity(String userCode, Long userId, int value, double amount) {
        }

        EntityInfo entityInfo = fromRecord(SomeEntity.class).columnNamingStrategy(SNAKE_CASE).upperCase().build();
        Path file = createTempFile("jfleet", "csv");
        try (OutputStream os = new FileOutputStream(file.toFile())) {
            try (JFleetCsvWriter<SomeEntity> writer = new JFleetCsvWriter<>(os, entityInfo)) {
                writer.write(new SomeEntity("A", 10L, 100, 1.2));
                writer.write(new SomeEntity("B", 20L, 200, 2.3));
                writer.write(new SomeEntity("C,A", 30L, 300, 3.4));
            }
        }

        List<String> content = readAllLines(file);
        assertThat(content).hasSize(4)
                .contains("USER_CODE,USER_ID,VALUE,AMOUNT")
                .contains("A,10,100,1.2")
                .contains("B,20,200,2.3")
                .contains("\"C,A\",30,300,3.4");
        System.out.println(content);
    }

    private static EntityFieldType columnType(EntityInfo entityInfo, String name) {
        return entityInfo.getColumns().stream().filter(c -> c.getColumnName().equals(name))
                .findFirst().orElseThrow().getFieldType();
    }
}
