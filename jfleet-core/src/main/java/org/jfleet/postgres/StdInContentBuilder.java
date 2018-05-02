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

import static org.jfleet.postgres.PgCopyConstants.DELIMITER_CHAR;
import static org.jfleet.postgres.PgCopyConstants.NEWLINE_CHAR;

import java.util.List;

import org.jfleet.ColumnInfo;
import org.jfleet.EntityInfo;
import org.jfleet.common.DoubleBufferStringContent;
import org.jfleet.common.StringContent;

public class StdInContentBuilder {

    private final PgCopyEscaper escaper = new PgCopyEscaper();
    private final PostgresTypeSerializer typeSerializer = new PostgresTypeSerializer();
    private final List<ColumnInfo> columns;

    private DoubleBufferStringContent doubleBuffer;
    private StringContent stringContent;

    public StdInContentBuilder(EntityInfo entityInfo, int batchSize, boolean concurrent) {
        this.doubleBuffer = new DoubleBufferStringContent(batchSize, concurrent);
        this.stringContent = doubleBuffer.next();
        this.columns = entityInfo.getNotIdentityColumns();
    }

    public <T> void add(T entity) {
        for (int i = 0; i < columns.size(); i++) {
            ColumnInfo info = columns.get(i);
            Object value = info.getAccessor().apply(entity);
            if (value != null) {
                String valueStr = typeSerializer.toString(value, info.getFieldType());
                String escapedValue = escaper.escapeForStdIn(valueStr);
                stringContent.append(escapedValue);
            } else {
                stringContent.append("\\N");
            }
            if (i < columns.size() - 1) {
                stringContent.append(DELIMITER_CHAR);
            }
        }
        stringContent.append(NEWLINE_CHAR);
        stringContent.inc();
    }

    public void reset() {
        this.stringContent = doubleBuffer.next();
    }

    public boolean isFilled() {
        return stringContent.isFilled();
    }

    public int getContentSize() {
        return stringContent.getContentSize();
    }

    public int getRecords() {
        return stringContent.getRecords();
    }

    public StringContent getContent() {
        return stringContent;
    }

}
