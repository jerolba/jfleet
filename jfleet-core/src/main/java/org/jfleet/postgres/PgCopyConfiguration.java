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
package org.jfleet.postgres;

import java.util.concurrent.Executor;

import org.jfleet.EntityInfo;
import org.jfleet.common.JFleetBatchConfig;
import org.jfleet.inspection.JpaEntityInspector;

public class PgCopyConfiguration implements JFleetBatchConfig {

    private EntityInfo entityInfo;
    private int batchSize;
    private boolean autocommit;
    private boolean concurrent;
    private Executor executor;

    @Override
    public EntityInfo getEntityInfo() {
        return entityInfo;
    }

    @Override
    public int getBatchSize() {
        return batchSize;
    }

    @Override
    public boolean isAutocommit() {
        return autocommit;
    }

    @Override
    public boolean isConcurrent() {
        return concurrent;
    }

    @Override
    public Executor getExecutor() {
        return executor;
    }


    public static class PgCopyConfigurationBuilder {

        private Class<?> clazz;
        private EntityInfo entityInfo;
        private int batchSize = 10 * 1_024 * 1_024;
        private boolean autocommit = true;
        private boolean concurrent = true;
        private Executor executor = null;

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

        public PgCopyConfigurationBuilder executor(Executor executor) {
            this.executor = executor;
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
            conf.executor = this.executor;
            conf.entityInfo = this.entityInfo;
            return conf;
        }
    }
}
