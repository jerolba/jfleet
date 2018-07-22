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
package org.jfleet.csv;

import java.nio.charset.Charset;

import org.jfleet.EntityInfo;
import org.jfleet.common.TypeSerializer;

public class CsvConfiguration<T> {

    private Class<T> clazz;
    private EntityInfo entityInfo;
    private Charset charset = Charset.forName("UTF-8");
    private TypeSerializer typeSerializer = new CsvTypeSerializer();
    private boolean headers = true;
    private char fieldSeparator = ',';
    private char textDelimiter = '"';
    private boolean alwaysDelimitText = false;
    private String lineDelimiter = System.lineSeparator();

    public CsvConfiguration(Class<T> clazz) {
        this.clazz = clazz;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public EntityInfo getEntityInfo() {
        return entityInfo;
    }

    public Charset getCharset() {
        return charset;
    }

    public TypeSerializer getTypeSerializer() {
        return typeSerializer;
    }

    public boolean isHeaders() {
        return headers;
    }

    public char getFieldSeparator() {
        return fieldSeparator;
    }

    public char getTextDelimiter() {
        return textDelimiter;
    }

    public boolean isAlwaysDelimitText() {
        return alwaysDelimitText;
    }

    public String getLineDelimiter() {
        return lineDelimiter;
    }

    public static class Builder<T> {

        private Class<T> clazz;
        private EntityInfo entityInfo;
        private Charset charset = Charset.forName("UTF-8");
        private TypeSerializer typeSerializer = new CsvTypeSerializer();
        private boolean headers = true;
        private char fieldSeparator = ',';
        private char textDelimiter = '"';
        private boolean alwaysDelimitText = false;
        private String lineDelimiter = System.lineSeparator();

        public Builder(Class<T> clazz) {
            this.clazz = clazz;
        }

        public Builder<T> entityInfo(EntityInfo entityInfo) {
            this.entityInfo = entityInfo;
            return this;
        }

        public Builder<T> charset(Charset charset) {
            this.charset = charset;
            return this;
        }

        public Builder<T> typeSerializer(TypeSerializer typeSerializer) {
            this.typeSerializer = typeSerializer;
            return this;
        }

        public Builder<T> headers(boolean headers) {
            this.headers = headers;
            return this;
        }

        public Builder<T> fieldSeparator(char fieldSeparator) {
            this.fieldSeparator = fieldSeparator;
            return this;
        }

        public Builder<T> textDelimiter(char textDelimiter) {
            this.textDelimiter = textDelimiter;
            return this;
        }

        public Builder<T> alwaysDelimitText(boolean alwaysDelimitText) {
            this.alwaysDelimitText = alwaysDelimitText;
            return this;
        }

        public Builder<T> lineDelimiter(String lineDelimiter) {
            this.lineDelimiter = lineDelimiter;
            return this;
        }

        public CsvConfiguration<T> build() {
            CsvConfiguration<T> config = new CsvConfiguration<>(clazz);
            config.entityInfo = entityInfo;
            config.charset = charset;
            config.typeSerializer = typeSerializer;
            config.headers = headers;
            config.fieldSeparator = fieldSeparator;
            config.textDelimiter = textDelimiter;
            config.alwaysDelimitText = alwaysDelimitText;
            config.lineDelimiter = lineDelimiter;
            return config;
        }

    }

}
