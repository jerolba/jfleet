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

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.jfleet.EntityFieldType.FieldTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JpaEntityInspector {

    private static Logger logger = LoggerFactory.getLogger(JpaEntityInspector.class);

    private final Class<?> entityClass;

    public JpaEntityInspector(Class<?> entityClass) {
        this.entityClass = entityClass;
        ensureHasEntity();
    }

    public EntityInfo inspect() {
        EntityInfo info = new EntityInfo();
        info.setEntityClass(entityClass);
        info.setTableName(getTableName());
        info.addFields(getFieldsFromClass(entityClass));
        return info;
    }

    private boolean ensureHasEntity() {
        Entity annotation = entityClass.getAnnotation(Entity.class);
        if (annotation != null) {
            return true;
        }
        throw new RuntimeException(entityClass.getName() + " has no Entity annotation");
    }

    private String getTableName() {
        Table annotation = entityClass.getAnnotation(Table.class);
        if (annotation != null) {
            String tableName = annotation.name();
            if (tableName != null && tableName.trim().length() > 0) {
                return tableName;
            }
        }
        return entityClass.getSimpleName().toLowerCase();
    }

    private FieldsCollection getFieldsFromClass(Class<?> entityClass) {
        if (entityClass == Object.class) {
            return EMPTY_FIELD_COLLECTION;
        }
        FieldsCollection parentClassFields = getFieldsFromClass(entityClass.getSuperclass());
        if (!isEntityOrMapped(entityClass)) {
            return parentClassFields;
        }
        MappingOverride mappingOverride = new MappingOverride(entityClass);
        FieldsCollection overwrittenClassFields = mappingOverride.override(parentClassFields);

        FieldsCollection currentClassFields = new FieldsCollection(Stream.of(entityClass.getDeclaredFields())
                .map(FieldInspector::new).map(FieldInspector::inspect).flatMap(List::stream));

        currentClassFields.addNotPresent(overwrittenClassFields);
        return currentClassFields;
    }

    private boolean isEntityOrMapped(Class<?> entityClass) {
        Entity entity = entityClass.getAnnotation(Entity.class);
        if (entity != null) {
            return true;
        }
        MappedSuperclass mapped = entityClass.getAnnotation(MappedSuperclass.class);
        if (mapped != null) {
            return true;
        }
        return false;
    }

    private static class FieldInspector {

        private final Field field;

        FieldInspector(Field field) {
            this.field = field;
        }

        public List<FieldInfo> inspect() {
            if (isSkippable()) {
                return Collections.emptyList();
            }
            Embedded embedded = field.getAnnotation(Embedded.class);
            if (embedded != null) {
                EmbeddedInspector embeddedInspector = new EmbeddedInspector(field);
                return embeddedInspector.getFields();
            }
            ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
            if (manyToOne != null) {
                ManyToOneInspector manyToOneInspector = new ManyToOneInspector(field);
                return manyToOneInspector.getFields();
            }

            FieldInfo fieldInfo = new FieldInfo();
            fieldInfo.setColumnName(getColumnName());
            fieldInfo.setFieldName(field.getName());
            fieldInfo.setFieldType(getFieldType());
            return asList(fieldInfo);
        }

        private String getColumnName() {
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                String name = column.name();
                if (name != null && name.trim().length() > 0) {
                    return name;
                }
            }
            return field.getName();
        }

        private EntityFieldType getFieldType() {
            Class<?> javaType = field.getType();
            EntityFieldType type = new EntityFieldType();
            boolean primitive = javaType.isPrimitive();
            type.setPrimitive(primitive);
            if (Long.class.equals(javaType) || long.class.equals(javaType)) {
                type.setFieldType(FieldTypeEnum.LONG);
            } else if (Boolean.class.equals(javaType) || boolean.class.equals(javaType)) {
                type.setFieldType(FieldTypeEnum.BOOLEAN);
            } else if (Byte.class.equals(javaType) || byte.class.equals(javaType)) {
                type.setFieldType(FieldTypeEnum.BYTE);
            } else if (Character.class.equals(javaType) || char.class.equals(javaType)) {
                type.setFieldType(FieldTypeEnum.CHAR);
            } else if (Double.class.equals(javaType) || double.class.equals(javaType)) {
                type.setFieldType(FieldTypeEnum.DOUBLE);
            } else if (Float.class.equals(javaType) || float.class.equals(javaType)) {
                type.setFieldType(FieldTypeEnum.FLOAT);
            } else if (Integer.class.equals(javaType) || int.class.equals(javaType)) {
                type.setFieldType(FieldTypeEnum.INT);
            } else if (Short.class.equals(javaType) || short.class.equals(javaType)) {
                type.setFieldType(FieldTypeEnum.SHORT);
            } else if (String.class.equals(javaType)) {
                type.setFieldType(FieldTypeEnum.STRING);
            } else if (Date.class.isAssignableFrom(javaType)) {
                type.setFieldType(getDateFieldType(javaType));
            } else if (BigDecimal.class.equals(javaType)) {
                type.setFieldType(FieldTypeEnum.BIGDECIMAL);
            } else if (BigInteger.class.equals(javaType)) {
                type.setFieldType(FieldTypeEnum.BIGINTEGER);
            } else {
                throw new RuntimeException("Unexpected type on " + field.toString());
            }
            return type;
        }

        private FieldTypeEnum getDateFieldType(Class<?> javaType) {
            if (java.sql.Timestamp.class.isAssignableFrom(javaType)) {
                return FieldTypeEnum.TIMESTAMP;
            }
            if (java.sql.Time.class.isAssignableFrom(javaType)) {
                return FieldTypeEnum.TIME;
            }
            if (java.sql.Date.class.isAssignableFrom(javaType)) {
                return FieldTypeEnum.DATE;
            }
            Temporal temporal = field.getAnnotation(Temporal.class);
            if (temporal != null) {
                TemporalType temporalType = temporal.value();
                switch (temporalType) {
                case DATE:
                    return FieldTypeEnum.DATE;
                case TIME:
                    return FieldTypeEnum.TIME;
                case TIMESTAMP:
                    return FieldTypeEnum.TIMESTAMP;
                }
            }
            return FieldTypeEnum.TIMESTAMP;
        }

        private boolean isSkippable() {
            return isStaticField() || isTransient() || isInnerClassThisReference();
        }

        private boolean isTransient() {
            return field.getAnnotation(Transient.class) != null;
        }

        private boolean isStaticField() {
            return Modifier.isStatic(field.getModifiers());
        }

        private boolean isInnerClassThisReference() {
            return field.getName().startsWith("this$");
        }

    }

    private final static FieldsCollection EMPTY_FIELD_COLLECTION = new FieldsCollection();

    private static class FieldsCollection extends ArrayList<FieldInfo> {

        private static final long serialVersionUID = -1272424641343347918L;

        FieldsCollection() {
        }

        FieldsCollection(Stream<FieldInfo> fieldsStream) {
            fieldsStream.forEach(this::add);
        }

        public void addNotPresent(FieldsCollection fields) {
            fields.stream().filter(field -> !this.isPresent(field)).forEach(this::add);
        }

        // TODO: review how to deal with repeated fields or columns
        private boolean isPresent(FieldInfo field) {
            String columnName = field.getColumnName();
            String fieldName = field.getFieldName();
            for (FieldInfo f : this) {
                if (f.getFieldName().equals(fieldName) || f.getColumnName().equals(columnName)) {
                    return true;
                }
            }
            return false;
        }

        public FieldsCollection overrideAtttributes(Map<String, String> mapping) {
            if (mapping.size() == 0) {
                return this;
            }
            FieldsCollection newCollection = new FieldsCollection();
            for (FieldInfo field : this) {
                String name = field.getFieldName();
                if (mapping.containsKey(name)) {
                    newCollection.add(field.withColumnName(mapping.get(name)));
                } else {
                    newCollection.add(field);
                }
            }
            return newCollection;
        }

    }

    private static class EmbeddedInspector {

        private final Field field;

        EmbeddedInspector(Field field) {
            this.field = field;
        }

        public List<FieldInfo> getFields() {
            String name = field.getName();
            Class<?> javaType = field.getType();
            FieldsCollection currentClassFields = getFieldsFromClass(javaType);
            return currentClassFields.stream().map(field -> field.prependName(name)).collect(toList());
        }

        private FieldsCollection getFieldsFromClass(Class<?> entityClass) {
            if (entityClass == Object.class) {
                return EMPTY_FIELD_COLLECTION;
            }
            FieldsCollection parentClassFields = getFieldsFromClass(entityClass.getSuperclass());
            FieldsCollection currentClassFields = new FieldsCollection(Stream.of(entityClass.getDeclaredFields())
                    .map(FieldInspector::new)
                    .map(FieldInspector::inspect)
                    .flatMap(List::stream)
            );

            currentClassFields.addNotPresent(parentClassFields);

            MappingOverride mappingOverride = new MappingOverride(field);
            return mappingOverride.override(currentClassFields);
        }

    }

    private static class MappingOverride {

        private final Map<String, String> mapping;

        MappingOverride(Field field) {
            AttributeOverrides multiple = field.getAnnotation(AttributeOverrides.class);
            AttributeOverride simple = field.getAnnotation(AttributeOverride.class);
            this.mapping = getMappingOverrride(multiple, simple);
        }

        MappingOverride(Class<?> entityClass) {
            AttributeOverrides multiple = entityClass.getAnnotation(AttributeOverrides.class);
            AttributeOverride simple = entityClass.getAnnotation(AttributeOverride.class);
            this.mapping = getMappingOverrride(multiple, simple);
        }

        private Map<String, String> getMappingOverrride(AttributeOverrides multiple, AttributeOverride simple) {
            Stream<AttributeOverride> overrides = getMappingOverride(multiple)
                    .orElseGet(() -> getMappingOverride(simple).orElse(Stream.empty()));
            return overrides.collect(toMap(attr -> attr.name(), attr -> attr.column().name()));
        }

        private Optional<Stream<AttributeOverride>> getMappingOverride(AttributeOverrides multiple) {
            if (multiple == null) {
                return Optional.empty();
            }
            if (multiple.value().length == 0) {
                logger.warn("An @AttributeOverrides has no @AttributeOverride elements.");
                return Optional.empty();
            }
            return Optional.of(Stream.of(multiple.value()));
        }

        private Optional<Stream<AttributeOverride>> getMappingOverride(AttributeOverride simple) {
            if (simple == null) {
                return Optional.empty();
            }
            return Optional.of(Stream.of(simple));
        }

        public FieldsCollection override(FieldsCollection fieldsCollection) {
            return fieldsCollection.overrideAtttributes(mapping);
        }

    }

    private static class ManyToOneInspector {

        private final Field field;

        ManyToOneInspector(Field field) {
            this.field = field;
        }

        public List<FieldInfo> getFields() {
            if (!reviewSupportedRelations(field)) {
                return Collections.emptyList();
            }
            String name = field.getName();
            Class<?> javaType = field.getType();
            FieldsCollection currentClassFields = getIdFieldsFromClass(javaType);
            if (currentClassFields.size() == 0) {
                logger.warn(javaType.getName() + " has no simple @id annotation. Relation not persisted.");
                return Collections.emptyList();
            }
            if (currentClassFields.size() == 1) {
                FieldInfo fieldId = currentClassFields.get(0);
                JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
                String columnName = null;
                if (joinColumn != null) {
                    columnName = joinColumn.name();
                } else {
                    columnName = name + "_" + fieldId.getColumnName();
                }
                return asList(fieldId.prependName(name).withColumnName(columnName));
            }
            return currentClassFields.stream().map(field -> {
                    String columnName = name + "_" + field.getColumnName();
                    return field.prependName(name).withColumnName(columnName);
                }).collect(toList());
        }

        private FieldsCollection getIdFieldsFromClass(Class<?> entityClass) {
            if (entityClass == Object.class) {
                return EMPTY_FIELD_COLLECTION;
            }
            FieldsCollection parentClassFields = getIdFieldsFromClass(entityClass.getSuperclass());
            FieldsCollection currentClassFields = new FieldsCollection(Stream.of(entityClass.getDeclaredFields())
                    .filter(this::isIdAnnotated)
                    .map(FieldInspector::new)
                    .map(FieldInspector::inspect)
                    .flatMap(List::stream)
            );

            currentClassFields.addNotPresent(parentClassFields);
            return currentClassFields;
        }

        private boolean isIdAnnotated(Field field) {
            Id id = field.getAnnotation(Id.class);
            return id != null;
        }

        private boolean reviewSupportedRelations(Field field) {
            JoinTable joinTable = field.getDeclaredAnnotation(JoinTable.class);
            if (joinTable != null) {
                logger.warn("JoinTable mapping unsupported, has no effect on join table persistence if needed");
                return false;
            }
            JoinColumns joinColumns = field.getDeclaredAnnotation(JoinColumns.class);
            if (joinColumns != null) {
                throw new UnsupportedOperationException("@JoinColumns annotation not supported");
            }
            return true;
        }

    }

}
