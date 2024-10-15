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
package org.jfleet.inspection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.jfleet.EntityFieldType;
import org.jfleet.EntityFieldType.FieldTypeEnum;
import org.jfleet.FieldInfo;
import org.junit.jupiter.api.Test;

public class FieldTypeInspectorTest {

    public enum EnumField {
        one, two, three, four
    }

    @Entity
    public class EntityWithTypes {

        public boolean booleanPrimitive;
        public Boolean booleanObject;

        public byte bytePrimitive;
        public Byte byteObject;

        public short shortPrimitive;
        public Short shortObject;

        public int intPrimitive;
        public Integer intObject;

        public long longPrimitive;
        public Long longObject;

        public char charPrimitive;
        public Character charObject;

        public float floatPrimitive;
        public Float floatObject;

        public double doublePrimitive;
        public Double doubleObject;

        public String string;

        public Date nonAnnotatedDate;
        @Temporal(TemporalType.DATE)
        public Date date;
        @Temporal(TemporalType.TIME)
        public Date time;
        @Temporal(TemporalType.TIMESTAMP)
        public Date timeStamp;

        public java.sql.Date sqlDate;
        public java.sql.Time sqlTime;
        public java.sql.Timestamp sqlTimeStamp;

        public LocalDate localDate;
        public LocalTime localTime;
        public LocalDateTime localDateTime;

        public EnumField enumUnAnnotated;
        @Enumerated
        public EnumField enumDefault;
        @Enumerated(EnumType.STRING)
        public EnumField enumString;
        @Enumerated(EnumType.ORDINAL)
        public EnumField enumOrdinal;
    }

    private List<FieldInfo> fields = new JpaFieldsInspector().getFieldsFromClass(EntityWithTypes.class);

    @Test
    public void booleanPrimitiveTest() {
        EntityFieldType type = getField("booleanPrimitive");
        assertTrue(type.isPrimitive());
        assertEquals(FieldTypeEnum.BOOLEAN, type.getFieldType());
    }

    @Test
    public void booleanObjectTest() {
        EntityFieldType type = getField("booleanObject");
        assertFalse(type.isPrimitive());
        assertEquals(FieldTypeEnum.BOOLEAN, type.getFieldType());
    }

    @Test
    public void bytePrimitiveTest() {
        EntityFieldType type = getField("bytePrimitive");
        assertTrue(type.isPrimitive());
        assertEquals(FieldTypeEnum.BYTE, type.getFieldType());
    }

    @Test
    public void byteObjectTest() {
        EntityFieldType type = getField("byteObject");
        assertFalse(type.isPrimitive());
        assertEquals(FieldTypeEnum.BYTE, type.getFieldType());
    }

    @Test
    public void shortPrimitiveTest() {
        EntityFieldType type = getField("shortPrimitive");
        assertTrue(type.isPrimitive());
        assertEquals(FieldTypeEnum.SHORT, type.getFieldType());
    }

    @Test
    public void shortObjectTest() {
        EntityFieldType type = getField("shortObject");
        assertFalse(type.isPrimitive());
        assertEquals(FieldTypeEnum.SHORT, type.getFieldType());
    }

    @Test
    public void intPrimitiveTest() {
        EntityFieldType type = getField("intPrimitive");
        assertTrue(type.isPrimitive());
        assertEquals(FieldTypeEnum.INT, type.getFieldType());
    }

    @Test
    public void intObjectTest() {
        EntityFieldType type = getField("intObject");
        assertFalse(type.isPrimitive());
        assertEquals(FieldTypeEnum.INT, type.getFieldType());
    }

    @Test
    public void longPrimitiveTest() {
        EntityFieldType type = getField("longPrimitive");
        assertTrue(type.isPrimitive());
        assertEquals(FieldTypeEnum.LONG, type.getFieldType());
    }

    @Test
    public void longObjectTest() {
        EntityFieldType type = getField("longObject");
        assertFalse(type.isPrimitive());
        assertEquals(FieldTypeEnum.LONG, type.getFieldType());
    }

    @Test
    public void charPrimitiveTest() {
        EntityFieldType type = getField("charPrimitive");
        assertTrue(type.isPrimitive());
        assertEquals(FieldTypeEnum.CHAR, type.getFieldType());
    }

    @Test
    public void charObjectTest() {
        EntityFieldType type = getField("charObject");
        assertFalse(type.isPrimitive());
        assertEquals(FieldTypeEnum.CHAR, type.getFieldType());
    }

    @Test
    public void floatPrimitiveTest() {
        EntityFieldType type = getField("floatPrimitive");
        assertTrue(type.isPrimitive());
        assertEquals(FieldTypeEnum.FLOAT, type.getFieldType());
    }

    @Test
    public void floatObjectTest() {
        EntityFieldType type = getField("floatObject");
        assertFalse(type.isPrimitive());
        assertEquals(FieldTypeEnum.FLOAT, type.getFieldType());
    }

    @Test
    public void doublePrimitiveTest() {
        EntityFieldType type = getField("doublePrimitive");
        assertTrue(type.isPrimitive());
        assertEquals(FieldTypeEnum.DOUBLE, type.getFieldType());
    }

    @Test
    public void doubleObjectTest() {
        EntityFieldType type = getField("doubleObject");
        assertFalse(type.isPrimitive());
        assertEquals(FieldTypeEnum.DOUBLE, type.getFieldType());
    }

    @Test
    public void stringTest() {
        EntityFieldType type = getField("string");
        assertFalse(type.isPrimitive());
        assertEquals(FieldTypeEnum.STRING, type.getFieldType());
    }

    @Test
    public void nonAnnotatedDateTest() {
        EntityFieldType type = getField("nonAnnotatedDate");
        assertFalse(type.isPrimitive());
        assertEquals(FieldTypeEnum.TIMESTAMP, type.getFieldType());
    }

    @Test
    public void dateTest() {
        EntityFieldType type = getField("date");
        assertFalse(type.isPrimitive());
        assertEquals(FieldTypeEnum.DATE, type.getFieldType());
    }

    @Test
    public void timeTest() {
        EntityFieldType type = getField("time");
        assertFalse(type.isPrimitive());
        assertEquals(FieldTypeEnum.TIME, type.getFieldType());
    }

    @Test
    public void timeStampTest() {
        EntityFieldType type = getField("timeStamp");
        assertFalse(type.isPrimitive());
        assertEquals(FieldTypeEnum.TIMESTAMP, type.getFieldType());
    }

    @Test
    public void sqlDateTest() {
        EntityFieldType type = getField("sqlDate");
        assertFalse(type.isPrimitive());
        assertEquals(FieldTypeEnum.DATE, type.getFieldType());
    }

    @Test
    public void sqlTimeTest() {
        EntityFieldType type = getField("sqlTime");
        assertFalse(type.isPrimitive());
        assertEquals(FieldTypeEnum.TIME, type.getFieldType());
    }

    @Test
    public void sqlTimeStampTest() {
        EntityFieldType type = getField("sqlTimeStamp");
        assertFalse(type.isPrimitive());
        assertEquals(FieldTypeEnum.TIMESTAMP, type.getFieldType());
    }

    @Test
    public void localDateTest() {
        EntityFieldType type = getField("localDate");
        assertFalse(type.isPrimitive());
        assertEquals(FieldTypeEnum.LOCALDATE, type.getFieldType());
    }

    @Test
    public void localTimeTest() {
        EntityFieldType type = getField("localTime");
        assertFalse(type.isPrimitive());
        assertEquals(FieldTypeEnum.LOCALTIME, type.getFieldType());
    }

    @Test
    public void localDateTimeTest() {
        EntityFieldType type = getField("localDateTime");
        assertFalse(type.isPrimitive());
        assertEquals(FieldTypeEnum.LOCALDATETIME, type.getFieldType());
    }

    @Test
    public void enumUnAnnotatedTest() {
        EntityFieldType type = getField("enumUnAnnotated");
        assertFalse(type.isPrimitive());
        assertEquals(FieldTypeEnum.ENUMORDINAL, type.getFieldType());
    }

    @Test
    public void enumDefaultTest() {
        EntityFieldType type = getField("enumDefault");
        assertFalse(type.isPrimitive());
        assertEquals(FieldTypeEnum.ENUMORDINAL, type.getFieldType());
    }

    @Test
    public void enumOrdinalTest() {
        EntityFieldType type = getField("enumOrdinal");
        assertFalse(type.isPrimitive());
        assertEquals(FieldTypeEnum.ENUMORDINAL, type.getFieldType());
    }

    @Test
    public void enumStringTest() {
        EntityFieldType type = getField("enumString");
        assertFalse(type.isPrimitive());
        assertEquals(FieldTypeEnum.ENUMSTRING, type.getFieldType());
    }

    private EntityFieldType getField(String fieldName) {
        return fields.stream().filter(f -> f.getFieldName().equals(fieldName)).findFirst().get().getFieldType();
    }
}
