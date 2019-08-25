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
package org.jfleet.shared.entities;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
@Table(name = "table_with_basic_types")
public class EntityWithBasicTypes {

    private Boolean booleanObject;
    private Byte byteObject;
    private Short shortObject;
    private Integer intObject;
    private Long longObject;
    private Character charObject;
    private Float floatObject;
    private Double doubleObject;
    private BigDecimal bigDecimal;
    private BigInteger bigInteger;
    private String string;
    private EnumForTest enumOrdinal;
    @Enumerated(EnumType.STRING)
    private EnumForTest enumString;

    private boolean booleanPrimitive;
    private byte bytePrimitive;
    private short shortPrimitive;
    private int intPrimitive;
    private long longPrimitive;
    private char charPrimitive;
    private float floatPrimitive;
    private double doublePrimitive;

    public Boolean getBooleanObject() {
        return booleanObject;
    }

    public void setBooleanObject(Boolean booleanObject) {
        this.booleanObject = booleanObject;
    }

    public Byte getByteObject() {
        return byteObject;
    }

    public void setByteObject(Byte byteObject) {
        this.byteObject = byteObject;
    }

    public Short getShortObject() {
        return shortObject;
    }

    public void setShortObject(Short shortObject) {
        this.shortObject = shortObject;
    }

    public Integer getIntObject() {
        return intObject;
    }

    public void setIntObject(Integer intObject) {
        this.intObject = intObject;
    }

    public Long getLongObject() {
        return longObject;
    }

    public void setLongObject(Long longObject) {
        this.longObject = longObject;
    }

    public Character getCharObject() {
        return charObject;
    }

    public void setCharObject(Character charObject) {
        this.charObject = charObject;
    }

    public Float getFloatObject() {
        return floatObject;
    }

    public void setFloatObject(Float floatObject) {
        this.floatObject = floatObject;
    }

    public Double getDoubleObject() {
        return doubleObject;
    }

    public void setDoubleObject(Double doubleObject) {
        this.doubleObject = doubleObject;
    }

    public BigDecimal getBigDecimal() {
        return bigDecimal;
    }

    public void setBigDecimal(BigDecimal bigDecimal) {
        this.bigDecimal = bigDecimal;
    }

    public BigInteger getBigInteger() {
        return bigInteger;
    }

    public void setBigInteger(BigInteger bigInteger) {
        this.bigInteger = bigInteger;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public EnumForTest getEnumOrdinal() {
        return enumOrdinal;
    }

    public void setEnumOrdinal(EnumForTest enumOrdinal) {
        this.enumOrdinal = enumOrdinal;
    }

    public EnumForTest getEnumString() {
        return enumString;
    }

    public void setEnumString(EnumForTest enumString) {
        this.enumString = enumString;
    }

    public boolean isBooleanPrimitive() {
        return booleanPrimitive;
    }

    public void setBooleanPrimitive(boolean booleanPrimitive) {
        this.booleanPrimitive = booleanPrimitive;
    }

    public byte getBytePrimitive() {
        return bytePrimitive;
    }

    public void setBytePrimitive(byte bytePrimitive) {
        this.bytePrimitive = bytePrimitive;
    }

    public short getShortPrimitive() {
        return shortPrimitive;
    }

    public void setShortPrimitive(short shortPrimitive) {
        this.shortPrimitive = shortPrimitive;
    }

    public int getIntPrimitive() {
        return intPrimitive;
    }

    public void setIntPrimitive(int intPrimitive) {
        this.intPrimitive = intPrimitive;
    }

    public long getLongPrimitive() {
        return longPrimitive;
    }

    public void setLongPrimitive(long longPrimitive) {
        this.longPrimitive = longPrimitive;
    }

    public char getCharPrimitive() {
        return charPrimitive;
    }

    public void setCharPrimitive(char charPrimitive) {
        this.charPrimitive = charPrimitive;
    }

    public float getFloatPrimitive() {
        return floatPrimitive;
    }

    public void setFloatPrimitive(float floatPrimitive) {
        this.floatPrimitive = floatPrimitive;
    }

    public double getDoublePrimitive() {
        return doublePrimitive;
    }

    public void setDoublePrimitive(double doublePrimitive) {
        this.doublePrimitive = doublePrimitive;
    }

}