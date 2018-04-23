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

import static org.junit.Assert.assertEquals;

import org.jfleet.EntityFieldType;
import org.jfleet.EntityFieldType.FieldTypeEnum;
import org.jfleet.EntityInfo;
import org.jfleet.FieldInfo;
import org.junit.Test;

public class SqlBuilderTest {

    public EntityInfo buildEntity() {
        EntityInfo entityInfo = new EntityInfo();
        entityInfo.setTableName("simple_table");

        FieldInfo f1 = new FieldInfo();
        f1.setColumnName("column1");
        EntityFieldType type1 = new EntityFieldType(FieldTypeEnum.INT, false);
        type1.setPrimitive(false);
        type1.setIdentityId(false);
        f1.setFieldType(type1);
        entityInfo.addField(f1);

        FieldInfo f2 = new FieldInfo();
        f2.setColumnName("column2");
        EntityFieldType type2 = new EntityFieldType(FieldTypeEnum.STRING, false);
        type2.setPrimitive(false);
        type2.setIdentityId(false);
        f2.setFieldType(type2);
        entityInfo.addField(f2);

        return entityInfo;
    }

    @Test
    public void testCopyTable() {
        SqlBuilder sqlBuilder = new SqlBuilder(buildEntity());
        sqlBuilder.addCopyTable();

        String sql = sqlBuilder.getSql();
        assertEquals("COPY simple_table ", sql);
    }

    @Test
    public void testColumnNames() {
        SqlBuilder sqlBuilder = new SqlBuilder(buildEntity());
        sqlBuilder.addColumnNames();

        String sql = sqlBuilder.getSql();
        assertEquals("(column1, column2)", sql);
    }

    @Test
    public void testFileConfig() {
        SqlBuilder sqlBuilder = new SqlBuilder(buildEntity());
        sqlBuilder.addFileConfig();

        String sql = sqlBuilder.getSql();
        // :( testing the same value
        assertEquals(" FROM STDIN WITH (ENCODING 'UTF-8', DELIMITER '\t', HEADER false)", sql);
    }

    @Test
    public void testCompleteQuery() {
        SqlBuilder sqlBuilder = new SqlBuilder(buildEntity());

        String sql = sqlBuilder.build();
        String expectedSql = "COPY simple_table (column1, column2) "
                + "FROM STDIN WITH (ENCODING 'UTF-8', DELIMITER '\t', HEADER false)";
        assertEquals(expectedSql, sql);
    }

}
