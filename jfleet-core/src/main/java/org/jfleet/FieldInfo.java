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

public class FieldInfo {

    private String fieldName;
    private String columnName;
    private EntityFieldType fieldType;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public EntityFieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(EntityFieldType fieldType) {
        this.fieldType = fieldType;
    }

    public FieldInfo prependName(String name) {
        FieldInfo newOne = new FieldInfo();
        newOne.fieldType = fieldType;
        newOne.columnName = columnName;
        newOne.fieldName = name + "." + fieldName;
        return newOne;
    }

    @Override
    public String toString() {
        return fieldName + " (" + columnName + "): " + fieldType.getFieldType().name();
    }

    public FieldInfo withColumnName(String newColumnName) {
        FieldInfo newOne = new FieldInfo();
        newOne.setFieldName(this.fieldName);
        newOne.setColumnName(newColumnName);
        newOne.setFieldType(this.fieldType);
        return newOne;
    }
}
