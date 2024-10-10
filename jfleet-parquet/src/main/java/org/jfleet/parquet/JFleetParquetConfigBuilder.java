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
package org.jfleet.parquet;

import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.api.WriteSupport;
import org.apache.parquet.io.OutputFile;
import org.apache.parquet.schema.MessageType;
import org.jfleet.EntityInfo;

class JFleetParquetConfigBuilder<T> extends ParquetWriter.Builder<T, JFleetParquetConfigBuilder<T>> {

    private EntityInfo entityInfo = null;
    private Map<String, String> extraMetaData = new HashMap<>();

    public static <T> JFleetParquetConfigBuilder<T> builder(OutputFile file, EntityInfo entityInfo) {
        return new JFleetParquetConfigBuilder<>(file, entityInfo);
    }

    private JFleetParquetConfigBuilder(OutputFile file, EntityInfo entityInfo) {
        super(file);
        this.entityInfo = entityInfo;
    }

    public JFleetParquetConfigBuilder<T> withExtraMetaData(Map<String, String> extraMetaData) {
        this.extraMetaData = extraMetaData;
        return this;
    }

    @Override
    protected JFleetParquetConfigBuilder<T> self() {
        return this;
    }

    @Override
    protected WriteSupport<T> getWriteSupport(Configuration conf) {
        MessageType schema = new ParquetSchemaBuilder(entityInfo).build();
        return new JFleetWriteSupport<>(entityInfo, schema, extraMetaData);
    }

}