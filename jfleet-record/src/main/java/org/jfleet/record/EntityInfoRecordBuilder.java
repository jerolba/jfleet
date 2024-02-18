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

import java.lang.reflect.RecordComponent;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jfleet.ColumnInfo;
import org.jfleet.EntityFieldType;
import org.jfleet.EntityFieldType.FieldTypeEnum;
import org.jfleet.EntityInfo;
import org.jfleet.record.annotation.Ordinal;

public class EntityInfoRecordBuilder {

    public static EntityInfoRecordBuilder fromRecord(Class<?> entityClass) {
        return new EntityInfoRecordBuilder(entityClass);
    }

    public static EntityInfo entityInfoFromRecord(Class<?> entityClass) {
        return new EntityInfoRecordBuilder(entityClass).build();
    }

    private final Class<?> entityClass;
    private String tableName;
    private ColumnNamingStrategy columnNamingStrategy = ColumnNamingStrategy.FIELD_NAME;
    private CaseType caseType;

    public EntityInfoRecordBuilder(Class<?> entityClass) {
        this.entityClass = entityClass;
        if (!entityClass.isRecord()) {
            throw new JFleetRecordException(entityClass.getName() + " is not a record");
        }
    }

    public EntityInfoRecordBuilder name(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public EntityInfoRecordBuilder columnNamingStrategy(ColumnNamingStrategy columnNamingStrategy) {
        this.columnNamingStrategy = columnNamingStrategy;
        return this;
    }

    public EntityInfoRecordBuilder upperCase() {
        this.caseType(CaseType.UPPERCASE);
        return this;
    }

    public EntityInfoRecordBuilder lowerCase() {
        this.caseType(CaseType.LOWERCASE);
        return this;
    }

    public EntityInfoRecordBuilder caseType(CaseType caseType) {
        this.caseType = caseType;
        return this;
    }

    public EntityInfo build() {
        return new Builder(entityClass, tableName, columnNamingStrategy, caseType).build();
    }

    private class Builder {

        private final Class<?> entityClass;
        private final String tableName;
        private final FieldToColumnMapper fieldToColumnMapper;

        private Builder(Class<?> entityClass, String tableName,
                ColumnNamingStrategy columnNamingStrategy, CaseType caseType) {
            this.entityClass = entityClass;
            this.tableName = tableName;
            this.fieldToColumnMapper = new FieldToColumnMapper(columnNamingStrategy, caseType);
        }

        public EntityInfo build() {
            List<ColumnInfo> columns = Stream.of(entityClass.getRecordComponents()).map(this::buildColunm).toList();
            String entityName = tableName == null ? entityClass.getSimpleName().toLowerCase() : tableName;
            return new EntityInfo(entityClass, entityName, columns);
        }

        private ColumnInfo buildColunm(RecordComponent c) {
            String name = fieldToColumnMapper.getColumnName(c);
            EntityFieldType componentFieldType = getComponentFieldType(c);
            Function<Object, Object> accessor = Reflection.recordAccessor(entityClass, c);
            return new ColumnInfo(name, componentFieldType, accessor);
        }

        private static EntityFieldType getComponentFieldType(RecordComponent rc) {
            Class<?> javaType = rc.getType();
            FieldTypeEnum fieldType = getFieldType(javaType);
            if (fieldType == null && javaType.isEnum()) {
                fieldType = getTypeEnum(rc);
            }
            if (fieldType == null) {
                throw new JFleetRecordException(
                        rc.getName() + " of type " + javaType.getName() + " is not a supported JFleet Record type");
            }
            boolean notNull = javaType.isPrimitive() || NotNullField.isNotNull(rc);
            return new EntityFieldType(fieldType, notNull);
        }

        private static FieldTypeEnum getFieldType(Class<?> javaType) {
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
            } else if (java.util.Date.class.isAssignableFrom(javaType)) {
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
            return value;
        }

        private static FieldTypeEnum getTypeEnum(RecordComponent recordComponent) {
            Ordinal annotation = recordComponent.getAnnotation(Ordinal.class);
            if (annotation != null) {
                return FieldTypeEnum.ENUMORDINAL;
            }
            return FieldTypeEnum.ENUMSTRING;
        }
    }

}
