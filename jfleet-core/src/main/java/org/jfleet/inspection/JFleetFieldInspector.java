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

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Optional;

import org.jfleet.EntityFieldType;
import org.jfleet.EntityFieldType.FieldTypeEnum;
import org.jfleet.metadata.JFleetEnumType;
import org.jfleet.metadata.JFleetEnumerated;
import org.jfleet.metadata.JFleetTemporal;
import org.jfleet.metadata.JFleetTemporalType;

public class JFleetFieldInspector {

    private static FieldTypeInspector fieldTypeInspector = new FieldTypeInspector();
    private final Field field;

    public JFleetFieldInspector(Field field) {
        this.field = field;
    }

    public EntityFieldType getFieldType() {
        Class<?> javaType = field.getType();
        Optional<EntityFieldType> fieldType = fieldTypeInspector.getFieldType(javaType);
        EntityFieldType type = fieldType.orElseGet(() -> getAnnotatedType(javaType)
                .orElseThrow(() -> new RuntimeException("Unexpected type on " + field.toString())));
        return type;
    }

    private Optional<EntityFieldType> getAnnotatedType(Class<?> javaType) {
        if (javaType.isEnum()) {
            return Optional.of(new EntityFieldType(getEnumType(field.getAnnotation(JFleetEnumerated.class)), false));
        } else if (Date.class.isAssignableFrom(javaType)) {
            return Optional.of(new EntityFieldType(getDateFieldType(javaType), false));
        }
        return Optional.empty();
    }

    private FieldTypeEnum getDateFieldType(Class<?> javaType) {
        JFleetTemporal temporal = field.getAnnotation(JFleetTemporal.class);
        if (temporal != null) {
            JFleetTemporalType temporalType = temporal.value();
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

    private FieldTypeEnum getEnumType(JFleetEnumerated enumerated) {
        if (enumerated == null) {
            return FieldTypeEnum.ENUMORDINAL;
        }
        JFleetEnumType type = enumerated.value();
        if (type == null || type == JFleetEnumType.ORDINAL) {
            return FieldTypeEnum.ENUMORDINAL;
        }
        return FieldTypeEnum.ENUMSTRING;
    }

}
