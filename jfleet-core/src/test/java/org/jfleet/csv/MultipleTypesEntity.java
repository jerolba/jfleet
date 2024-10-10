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
package org.jfleet.csv;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.jfleet.shared.entities.EnumForTest;

public class MultipleTypesEntity {

    private final boolean booleanType;
    private final byte byteType;
    private final short shortType;
    private final int intType;
    private final long longType;
    private final float floatType;
    private final double doubleType;
    private final char charType;
    private final String stringType;
    private final BigDecimal bigDecimalType;
    private final BigInteger bigIntegerType;
    private final EnumForTest enumType;

    public MultipleTypesEntity(boolean booleanType, byte byteType, short shortType, int intType, long longType,
            float floatType, double doubleType, char charType, String stringType, BigDecimal bigDecimalType,
            BigInteger bigIntegerType, EnumForTest enumType) {
        super();
        this.booleanType = booleanType;
        this.byteType = byteType;
        this.shortType = shortType;
        this.intType = intType;
        this.longType = longType;
        this.floatType = floatType;
        this.doubleType = doubleType;
        this.charType = charType;
        this.stringType = stringType;
        this.bigDecimalType = bigDecimalType;
        this.bigIntegerType = bigIntegerType;
        this.enumType = enumType;
    }

    public boolean isBooleanType() {
        return booleanType;
    }

    public byte getByteType() {
        return byteType;
    }

    public short getShortType() {
        return shortType;
    }

    public int getIntType() {
        return intType;
    }

    public long getLongType() {
        return longType;
    }

    public float getFloatType() {
        return floatType;
    }

    public double getDoubleType() {
        return doubleType;
    }

    public char getCharType() {
        return charType;
    }

    public String getStringType() {
        return stringType;
    }

    public BigDecimal getBigDecimalType() {
        return bigDecimalType;
    }

    public BigInteger getBigIntegerType() {
        return bigIntegerType;
    }

    public EnumForTest getEnumType() {
        return enumType;
    }

}
