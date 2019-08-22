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
package org.jfleet.jdbc;

import org.jfleet.EntityInfo;
import org.jfleet.inspection.JpaEntityInspector;

public class JdbcConfiguration {

    private EntityInfo entityInfo;
    private int batchSize;
    private boolean autocommit;
    private ParameterSetter parameterSetter;

    private JdbcConfiguration() {
    }

    public EntityInfo getEntityInfo() {
        return entityInfo;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public boolean isAutocommit() {
        return autocommit;
    }
    
    public ParameterSetter getParameterSetter() {
        return parameterSetter;
    }

    public static class JdbcConfigurationBuilder {

        private Class<?> clazz;
        private EntityInfo entityInfo;
        private int batchSize = 10_000;
        private boolean autocommit = true;
        private ParameterSetter parameterSetter;

        public static JdbcConfigurationBuilder from(Class<?> clazz) {
            return new JdbcConfigurationBuilder(clazz);
        }

        public static JdbcConfigurationBuilder from(EntityInfo entityInfo) {
            return new JdbcConfigurationBuilder(entityInfo);
        }

        private JdbcConfigurationBuilder(Class<?> clazz) {
            this.clazz = clazz;
        }

        private JdbcConfigurationBuilder(EntityInfo entityInfo) {
            this.entityInfo = entityInfo;
        }

        public JdbcConfigurationBuilder batchSize(int batchSize) {
            this.batchSize = batchSize;
            return this;
        }

        public JdbcConfigurationBuilder autocommit(boolean autocommit) {
            this.autocommit = autocommit;
            return this;
        }

        public JdbcConfigurationBuilder parameterSetter(ParameterSetter parameterSetter) {
            this.parameterSetter = parameterSetter;
            return this;
        }

        public JdbcConfiguration build() {
            if (entityInfo == null) {
                JpaEntityInspector inspector = new JpaEntityInspector(clazz);
                entityInfo = inspector.inspect();
            }
            JdbcConfiguration conf = new JdbcConfiguration();
            conf.autocommit = this.autocommit;
            conf.batchSize = this.batchSize;
            conf.entityInfo = this.entityInfo;
            conf.parameterSetter = this.parameterSetter;
            return conf;
        }
    }

}
