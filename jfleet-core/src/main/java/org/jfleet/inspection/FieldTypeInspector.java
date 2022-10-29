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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import org.jfleet.EntityFieldType;
import org.jfleet.EntityFieldType.FieldTypeEnum;

public class FieldTypeInspector {

    public Optional<EntityFieldType> getFieldType(Class<?> javaType) {
        FieldTypeEnum value = null;
        if (Long.class.equals(javaType) || long.class.equals(javaType)) {
            value = FieldTypeEnum.LONG;
        } else if (Boolean.class.equals(javaType) || boolean.class.equals(javaType)) {
            value = FieldTypeEnum.BOOLEAN;
        } else if (Byte.class.equals(javaType) || byte.class.equals(javaType)) {
            value = FieldTypeEnum.BYTE;
        } else if (Character.class.equals(javaType) || char.class.equals(javaType)) {
            value = FieldTypeEnum.CHAR;
        } else if (Double.class.equals(javaType) || double.class.equals(javaType)) {
            value = FieldTypeEnum.DOUBLE;
        } else if (Float.class.equals(javaType) || float.class.equals(javaType)) {
            value = FieldTypeEnum.FLOAT;
        } else if (Integer.class.equals(javaType) || int.class.equals(javaType)) {
            value = FieldTypeEnum.INT;
        } else if (Short.class.equals(javaType) || short.class.equals(javaType)) {
            value = FieldTypeEnum.SHORT;
        } else if (String.class.equals(javaType)) {
            value = FieldTypeEnum.STRING;
        } else if (java.sql.Timestamp.class.isAssignableFrom(javaType)) {
            value = FieldTypeEnum.TIMESTAMP;
        } else if (java.sql.Time.class.isAssignableFrom(javaType)) {
            value = FieldTypeEnum.TIME;
        } else if (java.sql.Date.class.isAssignableFrom(javaType)) {
            value = FieldTypeEnum.DATE;
        } else if (BigDecimal.class.equals(javaType)) {
            value = FieldTypeEnum.BIGDECIMAL;
        } else if (BigInteger.class.equals(javaType)) {
            value = FieldTypeEnum.BIGINTEGER;
        } else if (LocalDate.class.equals(javaType)) {
            value = FieldTypeEnum.LOCALDATE;
        } else if (LocalTime.class.equals(javaType)) {
            value = FieldTypeEnum.LOCALTIME;
        } else if (LocalDateTime.class.equals(javaType)) {
            value = FieldTypeEnum.LOCALDATETIME;
        }
        if (value == null) {
            return Optional.empty();
        }
        return Optional.of(new EntityFieldType(value, javaType.isPrimitive()));
    }
}
