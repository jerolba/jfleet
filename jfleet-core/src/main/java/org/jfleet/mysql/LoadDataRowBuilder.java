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
package org.jfleet.mysql;

import static org.jfleet.mysql.LoadDataConstants.FIELD_TERMINATED_CHAR;
import static org.jfleet.mysql.LoadDataConstants.LINE_TERMINATED_CHAR;

import java.util.List;

import org.jfleet.ColumnInfo;
import org.jfleet.EntityInfo;
import org.jfleet.common.EntityRowBuilder;
import org.jfleet.common.StringContent;

class LoadDataRowBuilder implements EntityRowBuilder {

    private final LoadDataEscaper escaper = new LoadDataEscaper();
    private final MySqlTypeSerializer typeSerializer = new MySqlTypeSerializer();
    private final List<ColumnInfo> columns;

    LoadDataRowBuilder(EntityInfo entityInfo) {
        this.columns = entityInfo.getColumns();
    }

    @Override
    public <T> void add(StringContent stringContent, T entity) {
        for (int i = 0; i < columns.size(); i++) {
            ColumnInfo info = columns.get(i);
            Object value = info.getAccessor().apply(entity);
            if (value != null) {
                String valueStr = typeSerializer.toString(value, info.getFieldType());
                String escapedValue = escaper.escapeForLoadFile(valueStr);
                stringContent.append(escapedValue);
            } else {
                stringContent.append("\\N");
            }
            stringContent.append(FIELD_TERMINATED_CHAR);
        }
        stringContent.append(LINE_TERMINATED_CHAR);
    }

}
