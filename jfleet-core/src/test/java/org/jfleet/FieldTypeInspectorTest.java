/**
 * Copyright 2017 Jerónimo López Bezanilla
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
package org.jfleet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.jfleet.EntityFieldType.FieldTypeEnum;
import org.junit.Test;

public class FieldTypeInspectorTest {

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

    }

    private static EntityInfo entityInfo = new JpaEntityInspector(EntityWithTypes.class).inspect();

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

    private EntityFieldType getField(String fieldName) {
        return entityInfo.getFields().stream().filter(f -> f.getFieldName().equals(fieldName)).findFirst().get()
                .getFieldType();
    }
}
