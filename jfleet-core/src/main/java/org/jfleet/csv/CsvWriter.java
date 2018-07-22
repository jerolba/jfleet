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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.jfleet.ColumnInfo;
import org.jfleet.EntityInfo;
import org.jfleet.common.TypeSerializer;
import org.jfleet.inspection.JpaEntityInspector;

public class CsvWriter<T> {

    private final CsvConfiguration<T> config;
    private final TypeSerializer typeSerializer;
    private final List<ColumnInfo> columns;
    private final CsvEscaper csvEscaper;
    private final String emptyText;

    public CsvWriter(Class<T> clazz) {
        this(new CsvConfiguration<>(clazz));
    }

    public CsvWriter(CsvConfiguration<T> config) {
        this.config = config;
        this.typeSerializer = config.getTypeSerializer();
        this.columns = getEntityInfo(config).getColumns();
        this.csvEscaper = new CsvEscaper(config);
        this.emptyText = Character.toString(config.getTextDelimiter()) + config.getTextDelimiter();
    }
    
    private EntityInfo getEntityInfo(CsvConfiguration<T> config) {
        EntityInfo entityInfo = config.getEntityInfo();
        if (entityInfo != null) {
            return entityInfo;
        }
        return new JpaEntityInspector(config.getClazz()).inspect();
    }

    public void writeAll(OutputStream conn, Collection<T> collection) throws IOException {
        writeAll(conn, collection.stream());
    }

    public void writeAll(OutputStream conn, Stream<T> stream) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn, config.getCharset()));
        if (config.isHeaders()) {
            writeHeader(writer, columns);
        }
        Iterator<T> iterator = stream.iterator();
        while (iterator.hasNext()) {
            add(writer, iterator.next());
        }
        writer.flush();
    }

    private void writeHeader(BufferedWriter writer, List<ColumnInfo> columns) throws IOException {
        for (int i = 0; i < columns.size(); i++) {
            ColumnInfo info = columns.get(i);
            writer.write(csvEscaper.escapeAndDelimite(info.getColumnName()));
            if (i < columns.size() - 1) {
                writer.write(config.getFieldSeparator());
            }
        }
        writer.write(config.getLineDelimiter());
    }

    private void add(BufferedWriter writer, T entity) throws IOException {
        for (int i = 0; i < columns.size(); i++) {
            ColumnInfo column = columns.get(i);
            Object value = column.getAccessor().apply(entity);
            if (value != null) {
                String valueStr = typeSerializer.toString(value, column.getFieldType());
                String escapedValue = csvEscaper.escapeAndDelimite(valueStr);
                writer.write(escapedValue);
            } else if (config.isAlwaysDelimitText()) {
                writer.write(emptyText);
            }
            if (i < columns.size() - 1) {
                writer.write(config.getFieldSeparator());
            }
        }
        writer.write(config.getLineDelimiter());
    }

}
