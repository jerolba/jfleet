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

public class EntityInfo {

    private Class<?> entityClass;
    private String tableName;
    private List<FieldInfo> fields = new ArrayList<>();

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<FieldInfo> getFields() {
        return fields;
    }

    public void addField(FieldInfo field) {
        this.fields.add(field);
    }

    public void addFields(List<FieldInfo> fields) {
        this.fields.addAll(fields);
    }

    public FieldInfo findField(String fieldName) {
        return getFields().stream().filter(f -> f.getFieldName().equals(fieldName)).findFirst().get();
    }


}
