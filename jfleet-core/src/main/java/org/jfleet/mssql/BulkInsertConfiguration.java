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
package org.jfleet.mssql;

import org.jfleet.EntityInfo;
import org.jfleet.inspection.JpaEntityInspector;

public class BulkInsertConfiguration {

    private EntityInfo entityInfo;
    private int batchSize;
    private boolean checkConstraints;
    private boolean fireTriggers;
    private boolean keepIdentity;
    private boolean keepNulls;
    private boolean tableLock;
    private boolean autocommit;

    private BulkInsertConfiguration() {
    }

    public EntityInfo getEntityInfo() {
        return entityInfo;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public boolean isCheckConstraints() {
        return checkConstraints;
    }
    
    public boolean isFireTriggers() {
        return fireTriggers;
    }

    public boolean isKeepIdentity() {
        return keepIdentity;
    }

    public boolean isKeepNulls() {
        return keepNulls;
    }

    public boolean isTableLock() {
        return tableLock;
    }

    public boolean isAutocommit() {
        return autocommit;
    }

    public static class BulkInsertConfigurationBuilder {

        private Class<?> clazz;
        private EntityInfo entityInfo;
        private int batchSize = 10_000;
        private boolean checkConstraints = true;
        private boolean fireTriggers = true;
        private boolean keepIdentity = true;
        private boolean keepNulls = false;
        private boolean tableLock = false;
        private boolean autocommit = true;

        public static BulkInsertConfigurationBuilder from(Class<?> clazz) {
            return new BulkInsertConfigurationBuilder(clazz);
        }

        public static BulkInsertConfigurationBuilder from(EntityInfo entityInfo) {
            return new BulkInsertConfigurationBuilder(entityInfo);
        }

        private BulkInsertConfigurationBuilder(Class<?> clazz) {
            this.clazz = clazz;
        }

        private BulkInsertConfigurationBuilder(EntityInfo entityInfo) {
            this.entityInfo = entityInfo;
        }

        public BulkInsertConfigurationBuilder batchSize(int batchSize) {
            this.batchSize = batchSize;
            return this;
        }
        
        public BulkInsertConfigurationBuilder checkConstraints(boolean checkConstraints) {
            this.checkConstraints = checkConstraints;
            return this;
        }

        public BulkInsertConfigurationBuilder fireTriggers(boolean fireTriggers) {
            this.fireTriggers = fireTriggers;
            return this;
        }

        public BulkInsertConfigurationBuilder keepIdentity(boolean keepIdentity) {
            this.keepIdentity = keepIdentity;
            return this;
        }

        public BulkInsertConfigurationBuilder keepNulls(boolean keepNulls) {
            this.keepNulls = keepNulls;
            return this;
        }

        public BulkInsertConfigurationBuilder tableLock(boolean tableLock) {
            this.tableLock = tableLock;
            return this;
        }

        public BulkInsertConfigurationBuilder autocommit(boolean autocommit) {
            this.autocommit = autocommit;
            return this;
        }

        public BulkInsertConfiguration build() {
            if (entityInfo == null) {
                JpaEntityInspector inspector = new JpaEntityInspector(clazz);
                entityInfo = inspector.inspect();
            }
            BulkInsertConfiguration conf = new BulkInsertConfiguration();
            conf.autocommit = this.autocommit;
            conf.batchSize = this.batchSize;
            conf.entityInfo = this.entityInfo;
            conf.checkConstraints = this.checkConstraints;
            conf.fireTriggers = this.fireTriggers;
            conf.keepIdentity = this.keepIdentity;
            conf.keepNulls = this.keepNulls;
            conf.tableLock = this.tableLock;
            return conf;
        }
    }

}
