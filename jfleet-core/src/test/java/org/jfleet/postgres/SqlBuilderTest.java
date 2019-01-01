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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.jfleet.ColumnInfo;
import org.jfleet.EntityFieldType;
import org.jfleet.EntityFieldType.FieldTypeEnum;
import org.jfleet.EntityInfo;
import org.junit.jupiter.api.Test;

public class SqlBuilderTest {

    public EntityInfo buildEntity() {
        EntityFieldType type1 = new EntityFieldType(FieldTypeEnum.INT);
        ColumnInfo c1 = new ColumnInfo("column1", type1, null);

        EntityFieldType type2 = new EntityFieldType(FieldTypeEnum.STRING);
        ColumnInfo c2 = new ColumnInfo("column2", type2, null);

        return new EntityInfo(null, "simple_table", Arrays.asList(c1, c2));
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
