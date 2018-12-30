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
package org.jfleet.postgres;

import org.jfleet.EntityInfo;
import org.jfleet.inspection.JpaEntityInspector;

public class PgCopyConfiguration {

    private EntityInfo entityInfo;
    private int batchSize;
    private boolean autocommit;
    private boolean concurrent;

    public EntityInfo getEntityInfo() {
        return entityInfo;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public boolean isAutocommit() {
        return autocommit;
    }

    public boolean isConcurrent() {
        return concurrent;
    }

    public static class PgCopyConfigurationBuilder {

        private Class<?> clazz;
        private EntityInfo entityInfo;
        private int batchSize = 10 * 1_024 * 1_024;
        private boolean autocommit = true;
        private boolean concurrent = true;

        public static PgCopyConfigurationBuilder from(Class<?> clazz) {
            return new PgCopyConfigurationBuilder(clazz);
        }

        public static PgCopyConfigurationBuilder from(EntityInfo entityInfo) {
            return new PgCopyConfigurationBuilder(entityInfo);
        }

        private PgCopyConfigurationBuilder(Class<?> clazz) {
            this.clazz = clazz;
        }

        private PgCopyConfigurationBuilder(EntityInfo entityInfo) {
            this.entityInfo = entityInfo;
        }

        public PgCopyConfigurationBuilder batchSize(int batchSize) {
            this.batchSize = batchSize;
            return this;
        }

        public PgCopyConfigurationBuilder autocommit(boolean autocommit) {
            this.autocommit = autocommit;
            return this;
        }

        public PgCopyConfigurationBuilder concurrent(boolean concurrent) {
            this.concurrent = concurrent;
            return this;
        }

        public PgCopyConfiguration build() {
            if (entityInfo == null) {
                JpaEntityInspector inspector = new JpaEntityInspector(clazz);
                entityInfo = inspector.inspect();
            }
            PgCopyConfiguration conf = new PgCopyConfiguration();
            conf.autocommit = this.autocommit;
            conf.batchSize = this.batchSize;
            conf.concurrent = this.concurrent;
            conf.entityInfo = this.entityInfo;
            return conf;
        }
    }
}
