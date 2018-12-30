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
package org.jfleet.mysql;

import java.nio.charset.Charset;

import org.jfleet.EntityInfo;
import org.jfleet.inspection.JpaEntityInspector;

public class LoadDataConfiguration {

    private EntityInfo entityInfo;
    private Charset encoding;
    private int batchSize;
    private boolean autocommit;
    private boolean concurrent;
    private boolean errorOnMissingRow;

    public EntityInfo getEntityInfo() {
        return entityInfo;
    }

    public Charset getEncoding() {
        return encoding;
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

    public boolean isErrorOnMissingRow() {
        return errorOnMissingRow;
    }

    public static class LoadDataConfigurationBuilder {

        private Class<?> clazz;
        private EntityInfo entityInfo;
        private Charset encoding = Charset.forName("UTF-8");
        private int batchSize = 10 * 1_024 * 1_024;
        private boolean autocommit = true;
        private boolean concurrent = true;
        private boolean errorOnMissingRow = false;

        public static LoadDataConfigurationBuilder from(Class<?> clazz) {
            return new LoadDataConfigurationBuilder(clazz);
        }

        public static LoadDataConfigurationBuilder from(EntityInfo entityInfo) {
            return new LoadDataConfigurationBuilder(entityInfo);
        }

        private LoadDataConfigurationBuilder(Class<?> clazz) {
            this.clazz = clazz;
        }

        private LoadDataConfigurationBuilder(EntityInfo entityInfo) {
            this.entityInfo = entityInfo;
        }

        public LoadDataConfigurationBuilder encoding(Charset encoding) {
            this.encoding = encoding;
            return this;
        }

        public LoadDataConfigurationBuilder batchSize(int batchSize) {
            this.batchSize = batchSize;
            return this;
        }

        public LoadDataConfigurationBuilder autocommit(boolean autocommit) {
            this.autocommit = autocommit;
            return this;
        }

        public LoadDataConfigurationBuilder concurrent(boolean concurrent) {
            this.concurrent = concurrent;
            return this;
        }

        public LoadDataConfigurationBuilder errorOnMissingRow(boolean errorOnMissingRow) {
            this.errorOnMissingRow = errorOnMissingRow;
            return this;
        }

        public LoadDataConfiguration build() {
            if (entityInfo == null) {
                JpaEntityInspector inspector = new JpaEntityInspector(clazz);
                entityInfo = inspector.inspect();
            }
            LoadDataConfiguration conf = new LoadDataConfiguration();
            conf.autocommit = this.autocommit;
            conf.batchSize = this.batchSize;
            conf.concurrent = this.concurrent;
            conf.encoding = this.encoding;
            conf.entityInfo = this.entityInfo;
            conf.errorOnMissingRow = this.errorOnMissingRow;
            return conf;
        }
    }
}
