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

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO: Look for better, fastest and safest method. May be with bytecode
 * generator.
 */
public class EntityFieldAccesorFactory {

    private static Logger logger = LoggerFactory.getLogger(EntityFieldAccesorFactory.class);

    public Function<Object, Object> getAccessor(Class<?> entityClass, String fieldPath) {
        if (!fieldPath.contains(".")) {
            return getFieldAccessor(entityClass, fieldPath);
        }
        return getComposedAccessor(entityClass, fieldPath);
    }

    private Function<Object, Object> getComposedAccessor(Class<?> entityClass, String fieldNameSeq) {
        String[] fieldSeq = fieldNameSeq.split("\\.");
        ComposedEntityFieldAccessor head = new ComposedEntityFieldAccessor();
        ComposedEntityFieldAccessor composed = head;
        for (int i = 0; i < fieldSeq.length; i++) {
            String fieldName = fieldSeq[i];
            Function<Object, Object> accessor = getFieldAccessor(entityClass, fieldName);
            if (i < fieldSeq.length - 1) {
                composed = composed.andThenApply(accessor);
                entityClass = getAccessorTarget(entityClass, fieldName);
                if (entityClass == null) {
                    return null;
                }
            } else {
                composed.andFinally(accessor);
            }
        }
        return head;
    }

    private Function<Object, Object> getFieldAccessor(Class<?> entityClass, String fieldName) {
        return getAccessorByPublicField(entityClass, fieldName)
                .orElseGet(() -> getAccessorByPropertyDescriptor(entityClass, fieldName)
                        .orElseGet(() -> getAccessorByPrivateField(entityClass, fieldName).orElse(null)));
    }

    private Class<?> getAccessorTarget(Class<?> entityClass, String fieldName) {
        try {
            return entityClass.getDeclaredField(fieldName).getType();
        } catch (NoSuchFieldException | SecurityException e) {
            if (entityClass.getSuperclass() != Object.class) {
                return getAccessorTarget(entityClass.getSuperclass(), fieldName);
            }
            traceError(fieldName, entityClass, e);
            return null;
        }
    }

    private Optional<Function<Object, Object>> getAccessorByPropertyDescriptor(Class<?> entityClass, String fieldName) {
        try {
            PropertyDescriptor objPropertyDescriptor = new PropertyDescriptor(fieldName, entityClass);
            Method readMethod = objPropertyDescriptor.getReadMethod();
            readMethod.setAccessible(true);
            return Optional.of(obj -> {
                try {
                    return readMethod.invoke(obj);
                } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                    logger.error("Can not access to field getter: " + fieldName + " on class " + entityClass.getName()
                            + ": " + e.getMessage());
                    return null;
                }
            });
        } catch (IntrospectionException e) {
            return Optional.empty();
        }
    }

    private Optional<Function<Object, Object>> getAccessorByPublicField(Class<?> entityClass, String fieldName) {
        try {
            Field field = entityClass.getField(fieldName);
            if (field.isAccessible()) {
                return newAccessorByField(field);
            }
        } catch (NoSuchFieldException | SecurityException e) {
            traceAccess(fieldName, entityClass, e);
        }
        return Optional.empty();
    }

    private Optional<Function<Object, Object>> getAccessorByPrivateField(Class<?> entityClass, String fieldName) {
        try {
            Field field = entityClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            return newAccessorByField(field);
        } catch (NoSuchFieldException | SecurityException e) {
            if (entityClass.getSuperclass() != Object.class) {
                return getAccessorByPrivateField(entityClass.getSuperclass(), fieldName);
            }
            traceAccess(fieldName, entityClass, e);
        }
        return Optional.empty();
    }

    private Optional<Function<Object, Object>> newAccessorByField(Field field) {
        return Optional.of(obj -> {
            try {
                return field.get(obj);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                traceAccess(field.getName(), field.getDeclaringClass(), e);
                return null;
            }
        });
    }

    private void traceAccess(String fieldName, Class<?> entityClass, Exception e) {
        logger.trace("Unable to access to field \"" + fieldName + "\" on class " + entityClass.getName() + ": "
                + e.getMessage());
    }

    private void traceError(String fieldName, Class<?> entityClass, Exception e) {
        logger.error("Can not access to field \"" + fieldName + "\" on class " + entityClass.getName() + ": "
                + e.getMessage());
    }

}
