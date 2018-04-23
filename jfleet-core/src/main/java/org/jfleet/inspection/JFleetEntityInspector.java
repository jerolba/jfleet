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
import java.util.Optional;

import org.jfleet.EntityFieldType;
import org.jfleet.EntityFieldType.FieldTypeEnum;

public class JFleetEntityInspector {

    private final Class<?> entityClass;

    public JFleetEntityInspector(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    public EntityFieldType buildFieldTypeByPath(String fieldPath, Optional<FieldTypeEnum> fieldTypeEnum) {
        Field field = findReferencedField(entityClass, fieldPath);
        JFleetFieldInspector fieldTypeInspector = new JFleetFieldInspector(field);
        EntityFieldType fieldType = fieldTypeInspector.getFieldType();
        return fieldTypeEnum.map(type -> new EntityFieldType(type, fieldType.isPrimitive())).orElse(fieldType);
    }

    private Field findReferencedField(Class<?> entity, String fieldPath) {
        String headPath = fieldPath;
        String tailPath = "";
        int idx = fieldPath.indexOf(".");
        if (idx > 0) {
            headPath = fieldPath.substring(0, idx);
            tailPath = fieldPath.substring(idx + 1);
        }
        Field referencedField = findFieldAccessor(entity, headPath)
                .orElseThrow(() -> new NoSuchFieldException(
                        "No field found with path '" + fieldPath + "' in class " + entity.getName()));
        if (tailPath.length() == 0) {
            return referencedField;
        }
        return findReferencedField(referencedField.getType(), tailPath);
    }

    private Optional<Field> findFieldAccessor(Class<?> entity, String name) {
        if (entity == Object.class) {
            return Optional.empty();
        }
        try {
            return Optional.of(entity.getDeclaredField(name));
        } catch (java.lang.NoSuchFieldException e) {
            return findFieldAccessor(entity.getSuperclass(), name);
        }
    }

}
