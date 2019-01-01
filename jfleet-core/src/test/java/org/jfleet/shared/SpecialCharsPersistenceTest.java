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
package org.jfleet.shared;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.stream.Stream;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.jfleet.BulkInsert;
import org.jfleet.parameterized.Database;
import org.jfleet.parameterized.TestAllDBs;
import org.jfleet.parameterized.WithDB;
import org.jfleet.util.SqlUtil;

public class SpecialCharsPersistenceTest {

    @Entity
    @Table(name = "table_with_strings")
    class EnityWithStrings {

        private String foo;
        private String bar;

        public String getFoo() {
            return foo;
        }

        public void setFoo(String foo) {
            this.foo = foo;
        }

        public String getBar() {
            return bar;
        }

        public void setBar(String bar) {
            this.bar = bar;
        }

    }

    public void testWithString(Database database, String text) throws Exception {
        EnityWithStrings entity = new EnityWithStrings();
        entity.setFoo("Some text");
        entity.setBar(text);

        BulkInsert<EnityWithStrings> insert = database.getBulkInsert(EnityWithStrings.class);

        try (Connection conn = database.getConnection()) {
            SqlUtil.createTableForEntity(conn, EnityWithStrings.class);
            insert.insertAll(conn, Stream.of(entity));

            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery("SELECT foo, bar FROM table_with_strings")) {
                    assertTrue(rs.next());
                    assertEquals("Some text", rs.getString("foo"));
                    assertEquals(text, rs.getString("bar"));
                }
            }
        }
    }

    @TestAllDBs
    public void persistTab(@WithDB Database database) throws Exception {
        testWithString(database, "A text with a tab \t char inside \t");
    }

    @TestAllDBs
    public void persistReturn(@WithDB Database database) throws Exception {
        testWithString(database, "A text with a return \n char \n");
        testWithString(database, "A text with a return \n char \n\r");
    }

    @TestAllDBs
    public void persistWithEscapeChar(@WithDB Database database) throws Exception {
        testWithString(database, "A text with \\ a escape \\");
    }

    @TestAllDBs
    public void persistMixChars(@WithDB Database database) throws Exception {
        testWithString(database, "A text all \\ types \t of \n escape chars\n\r");
    }
}
