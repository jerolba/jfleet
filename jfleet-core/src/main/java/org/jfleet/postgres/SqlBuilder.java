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

import java.util.List;
import java.util.stream.Collectors;

import org.jfleet.EntityInfo;
import org.jfleet.FieldInfo;

public class SqlBuilder {

    private final EntityInfo entityInfo;
    private final StringBuilder sb = new StringBuilder();

    public SqlBuilder(EntityInfo entityInfo) {
        this.entityInfo = entityInfo;
    }

    public String build() {
        addCopyTable();
        addColumnNames();
        addFileConfig();
        return getSql();
    }

    public void addCopyTable() {
        sb.append("COPY ").append(entityInfo.getTableName()).append(" ");
    }

    public void addColumnNames() {
        sb.append("(");
        List<FieldInfo> fields = entityInfo.getNotIdentityField();
        sb.append(fields.stream().map(FieldInfo::getColumnName).collect(Collectors.joining(", ")));
        sb.append(")");
    }

    public void addFileConfig() {
        sb.append(" FROM STDIN WITH (")
            .append("ENCODING 'UTF-8', ") //TODO: review if needed when setting on connection
            .append("DELIMITER '\t', ")
            .append("HEADER false")
            .append(")");
    }

    public String getSql() {
        return sb.toString();
    }

}
