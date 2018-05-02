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
package org.jfleet.inspection;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.jfleet.EntityFieldType;
import org.jfleet.EntityFieldType.FieldTypeEnum;
import org.jfleet.FieldInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JpaFieldsInspector {

    private static Logger logger = LoggerFactory.getLogger(JpaFieldsInspector.class);

    private static FieldTypeInspector fieldTypeInspector = new FieldTypeInspector();

    public FieldsCollection getFieldsFromClass(Class<?> entityClass) {
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
            EmbeddedId embeddedId = field.getAnnotation(EmbeddedId.class);
            if (embedded != null || embeddedId != null) {
                EmbeddedInspector embeddedInspector = new EmbeddedInspector(field);
                return embeddedInspector.getFields();
            }
            ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
            OneToOne oneToOne = field.getAnnotation(OneToOne.class);
            if (manyToOne != null || oneToOne != null) {
                EntityToOneInspector entityToOneInspector = new EntityToOneInspector(field);
                return entityToOneInspector.getFields();
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
            Optional<EntityFieldType> fieldType = fieldTypeInspector.getFieldType(javaType);
            EntityFieldType type = fieldType.orElseGet(() -> getAnnotatedType(javaType)
                    .orElseThrow(() -> new RuntimeException("Unexpected type on " + field.toString())));

            return new EntityFieldType(type.getFieldType(), type.isPrimitive(), isIdentityId());
        }

        private boolean isIdentityId() {
            Id id = field.getAnnotation(Id.class);
            if (id != null) {
                GeneratedValue generated = field.getAnnotation(GeneratedValue.class);
                return (generated != null && generated.strategy() == GenerationType.IDENTITY);
            }
            return false;
        }

        private Optional<EntityFieldType> getAnnotatedType(Class<?> javaType) {
            if (javaType.isEnum()) {
                return Optional.of(new EntityFieldType(getEnumType(field.getAnnotation(Enumerated.class))));
            } else if (Date.class.isAssignableFrom(javaType)) {
                return Optional.of(new EntityFieldType(getDateFieldType()));
            }
            return Optional.empty();
        }

        private FieldTypeEnum getDateFieldType() {
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

        private FieldTypeEnum getEnumType(Enumerated enumerated) {
            if (enumerated == null) {
                return FieldTypeEnum.ENUMORDINAL;
            }
            EnumType type = enumerated.value();
            if (type == null || type == EnumType.ORDINAL) {
                return FieldTypeEnum.ENUMORDINAL;
            }
            return FieldTypeEnum.ENUMSTRING;
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
                    .map(FieldInspector::new).map(FieldInspector::inspect).flatMap(List::stream));

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

    private static class EntityToOneInspector {

        private final Field field;

        EntityToOneInspector(Field field) {
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
            FieldsCollection currentClassFields = new FieldsCollection(
                    Stream.of(entityClass.getDeclaredFields()).filter(this::isIdAnnotated).map(FieldInspector::new)
                            .map(FieldInspector::inspect).flatMap(List::stream));

            currentClassFields.addNotPresent(parentClassFields);
            return currentClassFields;
        }

        private boolean isIdAnnotated(Field field) {
            return field.getAnnotation(Id.class) != null;
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
