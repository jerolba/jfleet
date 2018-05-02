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

public class EntityFieldType {

    public enum FieldTypeEnum {
        BOOLEAN, BYTE, SHORT, INT, LONG, CHAR, FLOAT, DOUBLE, STRING, DATE, TIME, TIMESTAMP,
        BIGDECIMAL, BIGINTEGER, LOCALDATE, LOCALTIME, LOCALDATETIME, ENUMSTRING, ENUMORDINAL;
    }

    private final boolean primitive;
    private final FieldTypeEnum fieldType;
    private final boolean identityId;

    public EntityFieldType(FieldTypeEnum fieldType, boolean primitive, boolean identityId) {
        this.fieldType = fieldType;
        this.primitive = primitive;
        this.identityId = identityId;
    }

    public EntityFieldType(FieldTypeEnum fieldType, boolean primitive) {
        this(fieldType, primitive, false);
    }

    public EntityFieldType(FieldTypeEnum fieldType) {
        this(fieldType, false);
    }

    public boolean isPrimitive() {
        return primitive;
    }

    public FieldTypeEnum getFieldType() {
        return fieldType;
    }

    public boolean isIdentityId() {
        return identityId;
    }

}
