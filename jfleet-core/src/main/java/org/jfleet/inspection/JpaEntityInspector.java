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

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.jfleet.ColumnInfo;
import org.jfleet.EntityInfo;
import org.jfleet.FieldInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JpaEntityInspector {

    private static Logger logger = LoggerFactory.getLogger(JpaEntityInspector.class);

    private final Class<?> entityClass;

    public JpaEntityInspector(Class<?> entityClass) {
        try {
            Class.forName("javax.persistence.Entity");
        } catch (ClassNotFoundException e) {
            logger.error("javax.persistence dependency not found. Add it to your project "
                    + "https://mvnrepository.com/artifact/javax.persistence/persistence-api/1.0.2 "
                    + "or configure the entity with org.jfleet.EntityInfoBuilder");
            throw new RuntimeException("javax.persistence dependency not found. Add it to your project "
                    + "or configure the entity with org.jfleet.EntityInfoBuilder");
        }
        this.entityClass = entityClass;
        ensureHasEntity();
    }

    public EntityInfo inspect() {
        JpaFieldsInspector fieldsInspector = new JpaFieldsInspector();
        List<FieldInfo> fieldsFromClass = fieldsInspector.getFieldsFromClass(entityClass);

        EntityFieldAccesorFactory factory = new EntityFieldAccesorFactory();
        List<ColumnInfo> columns = fieldsFromClass.stream().map(field -> {
            Function<Object, Object> accessor = factory.getAccessor(entityClass, field.getFieldName());
            return new ColumnInfo(field.getColumnName(), field.getFieldType(), accessor);
        }).collect(Collectors.toList());
        return new EntityInfo(entityClass, getTableName(), columns);
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

}
