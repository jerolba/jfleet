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

import static org.jfleet.mysql.LoadDataConstants.FIELD_TERMINATED_CHAR;
import static org.jfleet.mysql.LoadDataConstants.LINE_TERMINATED_CHAR;

import java.util.ArrayList;
import java.util.List;

import org.jfleet.EntityFieldAccesorFactory;
import org.jfleet.EntityFieldAccessor;
import org.jfleet.EntityInfo;
import org.jfleet.FieldInfo;

public class FileContentBuilder {

    private final Escaper escaper = new Escaper();
    private final MySqlTypeSerializer typeSerializer = new MySqlTypeSerializer();
    private final List<EntityFieldAccessor> accessors = new ArrayList<>();

    private final List<FieldInfo> fields;

    private final StringBuilder sb = new StringBuilder();
    private int records = 0;

    public FileContentBuilder(EntityInfo entityInfo) {
        this.fields = entityInfo.getFields();
        EntityFieldAccesorFactory factory = new EntityFieldAccesorFactory();
        for (FieldInfo f : fields) {
            EntityFieldAccessor accesor = factory.getAccesor(entityInfo.getEntityClass(), f);
            accessors.add(accesor);
        }
    }

    public void reset() {
        records = 0;
        sb.setLength(0);
    }

    public <T> void add(T entity) {
        for (int i = 0; i < fields.size(); i++) {
            FieldInfo info = fields.get(i);
            EntityFieldAccessor accessor = accessors.get(i);
            Object value = accessor.getValue(entity);
            if (value != null) {
                String valueStr = typeSerializer.toString(value, info.getFieldType());
                String escapedValue = escaper.escapeForLoadFile(valueStr);
                sb.append(escapedValue);
            } else {
                sb.append("\\N");
            }
            sb.append(FIELD_TERMINATED_CHAR);
        }
        sb.append(LINE_TERMINATED_CHAR);
        records++;
    }

    public int getContentSize() {
        return sb.length();
    }

    public int getRecords() {
        return records;
    }

    public String getContent() {
        return sb.toString();
    }

}
