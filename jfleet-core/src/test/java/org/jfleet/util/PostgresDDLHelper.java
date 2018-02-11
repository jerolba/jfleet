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
import java.util.Optional;

import org.jfleet.EntityFieldType;
import org.jfleet.EntityFieldType.FieldTypeEnum;
import org.jfleet.EntityInfo;
import org.jfleet.FieldInfo;
import org.jfleet.util.Dialect.DDLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostgresDDLHelper implements DDLHelper {

    private static Logger logger = LoggerFactory.getLogger(PostgresDDLHelper.class);

    @Override
    public String dropTableSentence(EntityInfo entityInfo) {
        StringBuilder sb = new StringBuilder("DROP TABLE IF EXISTS ");
        sb.append(entityInfo.getTableName());
        return sb.toString();
    }

    @Override
    public String createTableSentence(EntityInfo entityInfo) {
        StringBuilder sb = new StringBuilder("CREATE TABLE ");
        sb.append(entityInfo.getTableName());
        sb.append(" (");
        List<FieldInfo> fields = entityInfo.getFields();
        for (int i = 0; i < fields.size(); i++) {
            FieldInfo fieldInfo = fields.get(i);
            String dbType = getDbType(fieldInfo.getFieldType());
            if (dbType == null) {
                throw new RuntimeException("Type not found for " + fieldInfo.getFieldType().getFieldType().name());
            }
            sb.append(fieldInfo.getColumnName()).append(" ");
            sb.append(dbType);
            if (fieldInfo.getFieldType().isPrimitive()) {
                sb.append(" NOT NULL");
            }
            if (i < fields.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(getPrimaryKey(fields));
        sb.append(")");
        return sb.toString();
    }

    private String getDbType(EntityFieldType fieldType) {
        if (fieldType.isIdentityId()) {
            return getSerial(fieldType);
        }
        FieldTypeEnum type = fieldType.getFieldType();
        switch (type) {
        case BOOLEAN:
            return "BOOLEAN";
        case BYTE:
            return "SMALLINT"; // No byte type found
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
            return "VARCHAR(255)";
        case DATE:
            return "DATE";
        case TIME:
            return "TIME";
        case TIMESTAMP:
            return "TIMESTAMPTZ";
        case LOCALDATE:
            return "DATE";
        case LOCALTIME:
            return "TIME";
        case LOCALDATETIME:
            return "TIMESTAMP";
        case ENUMORDINAL:
            return "SMALLINT";
        case ENUMSTRING:
            return "VARCHAR(255)";
        }
        return null;
    }

    private String getSerial(EntityFieldType fieldType) {
        switch (fieldType.getFieldType()) {
        case INT:
            return "SERIAL";
        case LONG:
            return "BIGSERIAL";
        default:
            logger.warn("Declared IDENTITY @Id strategy over non int or long type");
            return "";
        }
    }

    private String getPrimaryKey(List<FieldInfo> fields) {
        Optional<FieldInfo> id = fields.stream().filter(f -> f.getFieldType().isIdentityId()).findFirst();
        return id.map(f -> ", PRIMARY KEY (" + f.getColumnName() + ")").orElse("");
    }

}
