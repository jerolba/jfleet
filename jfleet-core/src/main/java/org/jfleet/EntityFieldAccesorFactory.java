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

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO: Look for better, fastest and safest method. May be with bytecode
 * generator.
 */
public class EntityFieldAccesorFactory {

    private static Logger logger = LoggerFactory.getLogger(EntityFieldAccesorFactory.class);

    public EntityFieldAccessor getAccessor(Class<?> entityClass, FieldInfo fieldInfo) {
        String fieldName = fieldInfo.getFieldName();
        return getAccessorByPublicField(entityClass, fieldName)
                .orElseGet(() -> getAccessorByPropertyDescriptor(entityClass, fieldName)
                        .orElseGet(() -> getAccessorByPrivateField(entityClass, fieldName).orElse(null)));

    }

    private Optional<EntityFieldAccessor> getAccessorByPropertyDescriptor(Class<?> entityClass, String fieldName) {
        try {
            PropertyDescriptor objPropertyDescriptor = new PropertyDescriptor(fieldName, entityClass);
            Method readMethod = objPropertyDescriptor.getReadMethod();
            readMethod.setAccessible(true);
            EntityFieldAccessor accesor = new EntityFieldAccessor() {

                @Override
                public Object getValue(Object obj) {
                    try {
                        return readMethod.invoke(obj);
                    } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                        logger.error("Can not access to field getter: " + fieldName + " on class "
                                + entityClass.getName() + ": " + e.getMessage());
                        return null;
                    }
                }
            };
            return Optional.of(accesor);
        } catch (IntrospectionException e) {
            return Optional.empty();
        }
    }

    private Optional<EntityFieldAccessor> getAccessorByPublicField(Class<?> entityClass, String fieldName) {
        try {
            Field field = entityClass.getField(fieldName);
            if (field.isAccessible()) {
                return newAccessorByField(field);
            }
        } catch (NoSuchFieldException | SecurityException e) {
            logger.trace("Can not access to field \"" + fieldName + "\" on class " + entityClass.getName() + ": "
                    + e.getMessage());
        }
        return Optional.empty();
    }

    private Optional<EntityFieldAccessor> getAccessorByPrivateField(Class<?> entityClass, String fieldName) {
        try {
            Field field = entityClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            return newAccessorByField(field);
        } catch (NoSuchFieldException | SecurityException e) {
            if (entityClass.getSuperclass() == Object.class) {
                logger.trace("Can not access to field \"" + fieldName + "\" on class " + entityClass.getName() + ": "
                        + e.getMessage());
            } else {
                return getAccessorByPrivateField(entityClass.getSuperclass(), fieldName);
            }
        }
        return Optional.empty();
    }

    private Optional<EntityFieldAccessor> newAccessorByField(Field field) {
        return Optional.of(new EntityFieldAccessor() {

            @Override
            public Object getValue(Object obj) {
                try {
                    return field.get(obj);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    logger.error("Can not access to field \"" + field.getName() + "\" on class "
                            + field.getDeclaringClass().getName() + ": " + e.getMessage());
                    return null;
                }
            }
        });
    }

}
