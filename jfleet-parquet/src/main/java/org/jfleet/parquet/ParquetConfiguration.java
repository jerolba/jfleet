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

import java.io.OutputStream;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.column.ParquetProperties.WriterVersion;
import org.apache.parquet.crypto.FileEncryptionProperties;
import org.apache.parquet.hadoop.ParquetFileWriter.Mode;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.apache.parquet.io.OutputFile;
import org.jfleet.EntityInfo;
import org.jfleet.inspection.JpaEntityInspector;

public class ParquetConfiguration<T> {

    private final JFleetParquetConfigBuilder<T> builder;

    ParquetConfiguration(JFleetParquetConfigBuilder<T> builder) {
        this.builder = builder;
    }

    JFleetParquetConfigBuilder<T> getWriterBuilder() {
        return builder;
    }

    public static class Builder<T> {

        private final JFleetParquetConfigBuilder<T> builder;

        public Builder(OutputFile path, Class<T> recordClass) {
            this(path, new JpaEntityInspector(recordClass).inspect());
        }

        public Builder(OutputFile path, EntityInfo entityInfo) {
            JFleetParquetConfigBuilder<T> builder = JFleetParquetConfigBuilder.builder(path, entityInfo);
            this.builder = builder.withWriteMode(Mode.OVERWRITE)
                    .withCompressionCodec(CompressionCodecName.SNAPPY);
        }

        public Builder(OutputStream outputStream, Class<T> recordClass) {
            this(new OutputStreamOutputFile(outputStream), recordClass);
        }

        public Builder(OutputStream outputStream, EntityInfo entityInfo) {
            this(new OutputStreamOutputFile(outputStream), entityInfo);
        }

        public Builder<T> withConf(Configuration conf) {
            builder.withConf(conf);
            return this;
        }

        public Builder<T> withWriteMode(Mode mode) {
            builder.withWriteMode(mode);
            return this;
        }

        public Builder<T> withCompressionCodec(CompressionCodecName codecName) {
            builder.withCompressionCodec(codecName);
            return this;
        }

        public Builder<T> withEncryption(FileEncryptionProperties encryptionProperties) {
            builder.withEncryption(encryptionProperties);
            return this;
        }

        public Builder<T> withRowGroupSize(long rowGroupSize) {
            builder.withRowGroupSize(rowGroupSize);
            return this;
        }

        public Builder<T> withMaxPaddingSize(int maxPaddingSize) {
            builder.withMaxPaddingSize(maxPaddingSize);
            return this;
        }

        public Builder<T> enableValidation() {
            builder.enableValidation();
            return this;
        }

        public Builder<T> withValidation(boolean enableValidation) {
            builder.withValidation(enableValidation);
            return this;
        }

        public Builder<T> withPageSize(int pageSize) {
            builder.withPageSize(pageSize);
            return this;
        }

        public Builder<T> withPageRowCountLimit(int rowCount) {
            builder.withPageRowCountLimit(rowCount);
            return this;
        }

        public Builder<T> withDictionaryPageSize(int dictionaryPageSize) {
            builder.withDictionaryPageSize(dictionaryPageSize);
            return this;
        }

        public Builder<T> enableDictionaryEncoding() {
            builder.enableDictionaryEncoding();
            return this;
        }

        public Builder<T> withDictionaryEncoding(boolean enableDictionary) {
            builder.withDictionaryEncoding(enableDictionary);
            return this;
        }

        public Builder<T> withByteStreamSplitEncoding(boolean enableByteStreamSplit) {
            builder.withByteStreamSplitEncoding(enableByteStreamSplit);
            return this;
        }

        public Builder<T> withDictionaryEncoding(String columnPath, boolean enableDictionary) {
            builder.withDictionaryEncoding(columnPath, enableDictionary);
            return this;
        }

        public Builder<T> withWriterVersion(WriterVersion version) {
            builder.withWriterVersion(version);
            return this;
        }

        public Builder<T> enablePageWriteChecksum() {
            builder.withPageWriteChecksumEnabled(true);
            return this;
        }

        public Builder<T> withPageWriteChecksumEnabled(boolean enablePageWriteChecksum) {
            builder.withPageWriteChecksumEnabled(enablePageWriteChecksum);
            return this;
        }

        public Builder<T> withBloomFilterNDV(String columnPath, long ndv) {
            builder.withBloomFilterNDV(columnPath, ndv);
            return this;
        }

        public Builder<T> withBloomFilterEnabled(boolean enabled) {
            builder.withBloomFilterEnabled(enabled);
            return this;
        }

        public Builder<T> withBloomFilterEnabled(String columnPath, boolean enabled) {
            builder.withBloomFilterEnabled(columnPath, enabled);
            return this;
        }

        public Builder<T> withMinRowCountForPageSizeCheck(int min) {
            builder.withMinRowCountForPageSizeCheck(min);
            return this;
        }

        public Builder<T> withMaxRowCountForPageSizeCheck(int max) {
            builder.withMaxRowCountForPageSizeCheck(max);
            return this;
        }

        public Builder<T> config(String property, String value) {
            builder.config(property, value);
            return this;
        }

        public Builder<T> withExtraMetaData(Map<String, String> extraMetaData) {
            builder.withExtraMetaData(extraMetaData);
            return this;
        }

        public ParquetConfiguration<T> build() {
            return new ParquetConfiguration<>(builder);
        }

    }

}
