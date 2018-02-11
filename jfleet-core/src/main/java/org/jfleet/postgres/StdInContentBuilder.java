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

import java.util.ArrayList;
import java.util.List;

import org.jfleet.EntityFieldAccesorFactory;
import org.jfleet.EntityFieldAccessor;
import org.jfleet.EntityInfo;
import org.jfleet.FieldInfo;
import org.jfleet.common.StringContent;

public class StdInContentBuilder {

    private final PgCopyEscaper escaper = new PgCopyEscaper();
    private final PostgresTypeSerializer typeSerializer = new PostgresTypeSerializer();
    private final List<EntityFieldAccessor> accessors = new ArrayList<>();

    private final List<FieldInfo> fields;

    private StringContent sc;

    public StdInContentBuilder(EntityInfo entityInfo, int batchSize) {
        this.sc = new StringContent(batchSize);
        this.fields = entityInfo.getFields();
        EntityFieldAccesorFactory factory = new EntityFieldAccesorFactory();
        for (FieldInfo f : fields) {
            EntityFieldAccessor accesor = factory.getAccessor(entityInfo.getEntityClass(), f);
            accessors.add(accesor);
        }
    }

    public void reset() {
        sc.reset();
    }

    public <T> void add(T entity) {
        for (int i = 0; i < fields.size(); i++) {
            FieldInfo info = fields.get(i);
            EntityFieldAccessor accessor = accessors.get(i);
            Object value = accessor.getValue(entity);
            if (value != null) {
                String valueStr = typeSerializer.toString(value, info.getFieldType());
                String escapedValue = escaper.escapeForStdIn(valueStr);
                sc.append(escapedValue);
            } else {
                sc.append("\\N");
            }
            if (i < fields.size() - 1) {
                sc.append(DELIMITER_CHAR);
            }
        }
        sc.append(NEWLINE_CHAR);
        sc.inc();
    }

    public boolean isFilled() {
        return sc.isFilled();
    }

    public int getContentSize() {
        return sc.getContentSize();
    }

    public int getRecords() {
        return sc.getRecords();
    }

    public StringContent getContent() {
        return sc;
    }

}
