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
package org.jfleet.common;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.jfleet.EntityFieldType;
import org.jfleet.EntityFieldType.FieldTypeEnum;

public class BaseTypeSerializer {

    public interface Mapper extends Function<Object, String> {
    }

    private Map<FieldTypeEnum, Mapper> mappers = new HashMap<>();

    public BaseTypeSerializer() {
        add(FieldTypeEnum.BOOLEAN, FROM_BOOLEAN);
        add(FieldTypeEnum.BYTE, FROM_BYTE);
        add(FieldTypeEnum.CHAR, FROM_CHAR);
        add(FieldTypeEnum.DOUBLE, FROM_DOUBLE);
        add(FieldTypeEnum.FLOAT, FROM_FLOAT);
        add(FieldTypeEnum.INT, FROM_INT);
        add(FieldTypeEnum.LONG, FROM_LONG);
        add(FieldTypeEnum.SHORT, FROM_SHORT);
        add(FieldTypeEnum.BIGDECIMAL, FROM_BIGDECIMAL);
        add(FieldTypeEnum.BIGINTEGER, FROM_BIGINTEGER);
        add(FieldTypeEnum.STRING, FROM_STRING);
        add(FieldTypeEnum.TIMESTAMP, FROM_TIMESTAMP);
        add(FieldTypeEnum.DATE, FROM_DATE);
        add(FieldTypeEnum.TIME, FROM_TIME);
    }

    public void add(FieldTypeEnum type, Mapper mapper) {
        mappers.put(type, mapper);
    }

    public String toString(Object obj, EntityFieldType entityFieldType) {
        if (obj == null) {
            return null;
        }
        FieldTypeEnum fieldType = entityFieldType.getFieldType();
        Function<Object, String> function = mappers.get(fieldType);
        return function.apply(obj);
    }

    private static final Mapper FROM_BOOLEAN = (obj) -> ((Boolean) obj).booleanValue() ? "1" : "0";

    private static final Mapper FROM_BYTE = (obj) -> ((Byte) obj).toString();

    private static final Mapper FROM_CHAR = (obj) -> ((Character) obj).toString();

    private static final Mapper FROM_DOUBLE = (obj) -> ((Double) obj).toString();

    private static final Mapper FROM_FLOAT = (obj) -> ((Float) obj).toString();

    private static final Mapper FROM_INT = (obj) -> ((Integer) obj).toString();

    private static final Mapper FROM_LONG = (obj) -> ((Long) obj).toString();

    private static final Mapper FROM_SHORT = (obj) -> ((Short) obj).toString();

    private static final Mapper FROM_BIGDECIMAL = (obj) -> ((BigDecimal) obj).toString();

    private static final Mapper FROM_BIGINTEGER = (obj) -> ((BigInteger) obj).toString();

    private static final Mapper FROM_STRING = (obj) -> (String) obj;

    private static final Mapper FROM_TIMESTAMP = (obj) -> {
        if (obj instanceof java.util.Date) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.format((java.util.Date) obj);
        }
        return null;
    };

    private static final Mapper FROM_DATE = (obj) -> {
        if (obj instanceof java.util.Date) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format((java.util.Date) obj);
        }
        return null;
    };

    private static final Mapper FROM_TIME = (obj) -> {
        if (obj instanceof java.util.Date) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            return sdf.format((java.util.Date) obj);
        }
        return null;
    };

}
