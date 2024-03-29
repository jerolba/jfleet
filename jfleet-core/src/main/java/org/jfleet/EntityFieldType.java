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
package org.jfleet;

import java.util.Objects;

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

    @Override
    public int hashCode() {
        return Objects.hash(fieldType, identityId, primitive);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        EntityFieldType other = (EntityFieldType) obj;
        return fieldType == other.fieldType && identityId == other.identityId && primitive == other.primitive;
    }

}
