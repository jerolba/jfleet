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
package org.jfleet.avro;

import org.jfleet.EntityInfo;

public class AvroConfiguration<T> {

    private EntityInfo entityInfo;
    private Class<T> clazz;

    @SuppressWarnings("unchecked")
    public AvroConfiguration(EntityInfo entityInfo) {
        this.entityInfo = entityInfo;
        clazz = (Class<T>) entityInfo.getEntityClass();
    }

    public AvroConfiguration(Class<T> clazz) {
        this.clazz = clazz;
    }

    public EntityInfo getEntityInfo() {
        return entityInfo;
    }

    public Class<T> getClazz() {
        return clazz;
    }
}
