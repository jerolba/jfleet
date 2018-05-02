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
import java.util.function.Function;

import org.jfleet.EntityFieldType.FieldTypeEnum;
import org.jfleet.inspection.JFleetEntityInspector;

public class EntityInfoBuilder<T> {

    private final JFleetEntityInspector entityInspector;

    private final Class<?> entityClass;
    private final String tableName;
    private final List<ColumnInfo> columns = new ArrayList<>();
    private final EntityFieldAccesorFactory accesorFactory = new EntityFieldAccesorFactory();

    public EntityInfoBuilder(Class<T> entityClass, String tableName) {
        this.entityInspector = new JFleetEntityInspector(entityClass);
        this.entityClass = entityClass;
        this.tableName = tableName;
    }

    // Information by fieldPath is automatically inspected by reflection finding the
    // field accessed

    public EntityInfoBuilder<T> addField(String fieldPath) {
        String columnName = fieldPath.toLowerCase().replaceAll("\\.", "_");
        return addField(fieldPath, columnName, false);
    }

    public EntityInfoBuilder<T> addField(String fieldPath, String columnName) {
        return addField(fieldPath, columnName, false);
    }

    public EntityInfoBuilder<T> addField(String fieldPath, String columnName, boolean identityId) {
        EntityFieldType fieldType = entityInspector.buildFieldTypeByPath(fieldPath, Optional.empty());
        return addField(fieldPath, columnName, fieldType, identityId);
    }

    public EntityInfoBuilder<T> addField(String fieldPath, String columnName, FieldTypeEnum fieldTypeEnum) {
        return addField(fieldPath, columnName, fieldTypeEnum, false);
    }

    public EntityInfoBuilder<T> addField(String fieldPath, String columnName, FieldTypeEnum fieldTypeEnum,
            boolean identityId) {
        EntityFieldType fieldType = entityInspector.buildFieldTypeByPath(fieldPath, Optional.of(fieldTypeEnum));
        return addField(fieldPath, columnName, fieldType, identityId);
    }

    public EntityInfoBuilder<T> addField(String fieldPath, String columnName, EntityFieldType fieldType,
            boolean identityId) {
        FieldInfo field = new FieldInfo();
        field.setFieldName(fieldPath);
        field.setColumnName(columnName);
        field.setFieldType(new EntityFieldType(fieldType.getFieldType(), fieldType.isPrimitive(), identityId));
        return addField(field);
    }

    public EntityInfoBuilder<T> addField(FieldInfo fieldInfo) {
        validate(fieldInfo.getColumnName());
        Function<Object, Object> accessor = accesorFactory.getAccessor(entityClass, fieldInfo);
        return addColumn(new ColumnInfo(fieldInfo.getColumnName(), fieldInfo.getFieldType(), accessor));
    }

    // Information by accessor is not linked to a field and can not extract return
    // type by reflection because of type erasure

    public EntityInfoBuilder<T> addColumn(String columnName, FieldTypeEnum fieldTypeEnum,
            Function<T, Object> accessor) {
        return addColumn(columnName, fieldTypeEnum, false, accessor);
    }

    public EntityInfoBuilder<T> addColumn(String columnName, FieldTypeEnum fieldTypeEnum, boolean primitive,
            Function<T, Object> accessor) {
        return addColumn(columnName, fieldTypeEnum, primitive, false, accessor);
    }

    public EntityInfoBuilder<T> addColumn(String columnName, FieldTypeEnum fieldTypeEnum, boolean primitive,
            boolean identityId, Function<T, Object> accessor) {
        EntityFieldType fieldType = new EntityFieldType(fieldTypeEnum, primitive, identityId);
        return addColumn(columnName, fieldType, accessor);
    }

    @SuppressWarnings("unchecked")
    public EntityInfoBuilder<T> addColumn(String columnName, EntityFieldType fieldType, Function<T, Object> accessor) {
        return addColumn(new ColumnInfo(columnName, fieldType, (Function<Object, Object>) accessor));
    }

    public EntityInfoBuilder<T> addColumn(ColumnInfo columnInfo) {
        columns.add(columnInfo);
        return this;
    }

    private void validate(String columnName) {
        long count = columns.stream().filter(f -> f.getColumnName().equals(columnName)).count();
        if (count > 0) {
            throw new RuntimeException("Column " + columnName + " declared more than one time");
        }
    }

    public EntityInfo build() {
        return  new EntityInfo(entityClass, tableName, columns);
    }

}
