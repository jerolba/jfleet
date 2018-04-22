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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jfleet.EntityFieldType.FieldTypeEnum;
import org.jfleet.inspection.JFleetEntityInspector;

public class EntityInfoBuilder {

    private final JFleetEntityInspector entityInspector;
    private final Class<?> entityClass;
    private final String tableName;
    private final List<FieldInfo> fields = new ArrayList<>();

    public EntityInfoBuilder(Class<?> entityClass, String tableName) {
        this.entityInspector = new JFleetEntityInspector(entityClass);
        this.entityClass = entityClass;
        this.tableName = tableName;
    }

    public EntityInfoBuilder addField(String fieldPath) {
        String columnName = fieldPath.toLowerCase().replaceAll("\\.", "_");
        return addField(fieldPath, columnName, false);
    }

    public EntityInfoBuilder addField(String fieldPath, String columnName) {
        return addField(fieldPath, columnName, false);
    }

    public EntityInfoBuilder addField(String fieldPath, String columnName, boolean isIdentityId) {
        EntityFieldType fieldType = entityInspector.buildFieldTypeByPath(fieldPath, Optional.empty());
        return this.addField(fieldPath, columnName, fieldType, isIdentityId);
    }

    public EntityInfoBuilder addField(String fieldPath, String columnName, FieldTypeEnum fieldTypeEnum) {
        return this.addField(fieldPath, columnName, fieldTypeEnum, false);
    }

    public EntityInfoBuilder addField(String fieldPath, String columnName, FieldTypeEnum fieldTypeEnum,
            boolean isIdentityId) {
        EntityFieldType fieldType = entityInspector.buildFieldTypeByPath(fieldPath, Optional.of(fieldTypeEnum));
        return this.addField(fieldPath, columnName, fieldType, isIdentityId);
    }

    public EntityInfoBuilder addField(String fieldPath, String columnName, EntityFieldType fieldType,
            boolean isIdentityId) {
        validate(fieldPath, columnName);
        FieldInfo field = new FieldInfo();
        field.setFieldName(fieldPath);
        field.setColumnName(columnName);
        field.setFieldType(fieldType);
        fieldType.setIdentityId(isIdentityId);
        fields.add(field);
        return this;
    }

    private void validate(String fieldPath, String columnName) {
        long count = fields.stream().filter(f -> f.getColumnName().equals(columnName)).count();
        if (count > 0) {
            throw new RuntimeException("Column " + columnName + " declared more than one time");
        }
    }

    public EntityInfo build() {
        EntityInfo entityInfo = new EntityInfo();
        entityInfo.setEntityClass(entityClass);
        entityInfo.setTableName(tableName);
        entityInfo.addFields(fields);
        return entityInfo;
    }
}
