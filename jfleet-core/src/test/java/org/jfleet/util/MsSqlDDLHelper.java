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
package org.jfleet.util;

import java.util.List;

import org.jfleet.ColumnInfo;
import org.jfleet.EntityFieldType;
import org.jfleet.EntityFieldType.FieldTypeEnum;
import org.jfleet.EntityInfo;
import org.jfleet.util.Dialect.DDLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MsSqlDDLHelper implements DDLHelper {

    private static Logger logger = LoggerFactory.getLogger(MsSqlDDLHelper.class);

    @Override
    public String dropTableSentence(EntityInfo entityInfo) {
        StringBuilder sb = new StringBuilder("DROP TABLE IF EXISTS ");
        sb.append(scapeName(entityInfo.getTableName()));
        return sb.toString();
    }

    @Override
    public String createTableSentence(EntityInfo entityInfo) {
        StringBuilder sb = new StringBuilder("CREATE TABLE ");
        sb.append(scapeName(entityInfo.getTableName()));
        sb.append(" (");
        List<ColumnInfo> columns = entityInfo.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            ColumnInfo columnInfo = columns.get(i);
            String dbType = getDbType(columnInfo.getFieldType());
            if (dbType == null) {
                throw new RuntimeException("Type not found for " + columnInfo.getFieldType().getFieldType().name());
            }
            sb.append(scapeName(columnInfo.getColumnName())).append(" ");
            sb.append(dbType);
            if (columnInfo.getFieldType().isPrimitive()) {
                sb.append(" NOT NULL");
            }
            sb.append(getAutoIncrement(columnInfo));
            if (i < columns.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(") ");//DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci");
        return sb.toString();
    }

    private String getDbType(EntityFieldType fieldType) {
        FieldTypeEnum type = fieldType.getFieldType();
        switch (type) {
        case BOOLEAN:
            return "BIT";
        case BYTE:
            return "TINYINT";
        case CHAR:
            return "CHAR";
        case DOUBLE:
            return "FLOAT";
        case FLOAT:
            return "REAL";
        case INT:
            return "INT";
        case LONG:
            return "BIGINT";
        case SHORT:
            return "SMALLINT";
        case BIGDECIMAL:
            return "DECIMAL(10,2)";
        case BIGINTEGER:
            return "BIGINT";
        case STRING:
            return "NVARCHAR(255)";
        case DATE:
            return "DATE";
        case TIME:
            return "TIME";
        case TIMESTAMP:
            return "DATETIME";
        case LOCALDATE:
            return "DATE";
        case LOCALTIME:
            return "TIME";
        case LOCALDATETIME:
            return "DATETIME";
        case ENUMORDINAL:
            return "TINYINT";
        case ENUMSTRING:
            return "VARCHAR(255)";
        }
        return null;
    }

    private String getAutoIncrement(ColumnInfo columnInfo) {
        EntityFieldType fieldType = columnInfo.getFieldType();
        if (!fieldType.isIdentityId()) {
            return "";
        }
        FieldTypeEnum type = fieldType.getFieldType();
        switch (type) {
        case BYTE:
        case INT:
        case LONG:
        case SHORT:
        case BIGINTEGER:
            return " IDENTITY PRIMARY KEY";
        default:
            logger.warn("Declared IDENTITY @Id strategy over non int type");
            return "";
        }
    }

    private String scapeName(String name) {
        if (name.startsWith("\"") && name.endsWith("\"")) {
            return "`" + name.substring(1, name.length() - 1) + "`";
        }
        return name;
    }
}
