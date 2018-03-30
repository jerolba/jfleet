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

import static org.jfleet.mysql.LoadDataConstants.ESCAPED_BY_CHAR;
import static org.jfleet.mysql.LoadDataConstants.FIELD_TERMINATED_CHAR;
import static org.jfleet.mysql.LoadDataConstants.LINE_TERMINATED_CHAR;

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
        addLoadDataIntoTable();
        addFileConfig();
        addColumnNames();
        return getSql();
    }

    public void addLoadDataIntoTable() {
        sb.append("LOAD DATA LOCAL INFILE '' INTO TABLE ");
        sb.append(scapeName(entityInfo.getTableName())).append(" ");
    }

    public void addFileConfig() {
        sb.append("CHARACTER SET UTF8 ")
          .append("FIELDS TERMINATED BY '").append(FIELD_TERMINATED_CHAR).append("' ")
          .append("ENCLOSED BY '' ")
          .append("ESCAPED BY '").append(ESCAPED_BY_CHAR).append(ESCAPED_BY_CHAR).append("' ")
          .append("LINES TERMINATED BY '").append(LINE_TERMINATED_CHAR).append("' ")
          .append("STARTING BY '' ");
    }

    public void addColumnNames() {
        sb.append("(");
        List<FieldInfo> fields = entityInfo.getFields();
        sb.append(fields.stream().map(FieldInfo::getColumnName).map(this::scapeName).collect(Collectors.joining(", ")));
        sb.append(")");
    }

    public String getSql() {
        return sb.toString();
    }

    private String scapeName(String name) {
        if (name.startsWith("\"") && name.endsWith("\"")) {
            return "`" + name.substring(1, name.length() - 1) + "`";
        }
        return name;
    }
}
