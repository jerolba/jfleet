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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.jfleet.BulkInsert;
import org.jfleet.JFleetException;
import org.jfleet.util.SqlUtil;
import org.junit.Test;

public class SimpleEntityPersistenceTest extends AllDatabasesBaseTest {

    @Entity
    @Table(name = "simple_table")
    public class SimpleEntity {

        private String name;
        private boolean active;
        private int age;

        public SimpleEntity(String name, boolean active, int age) {
            this.name = name;
            this.active = active;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public boolean isActive() {
            return active;
        }

        public int getAge() {
            return age;
        }

    }

    @Test
    public void canPersistCollectionOfEntities() throws JFleetException, SQLException, IOException {
        int times = 1000;
        BulkInsert<SimpleEntity> insert = database.getBulkInsert(SimpleEntity.class);
        Stream<SimpleEntity> stream = IntStream.range(0, times)
                .mapToObj(i -> new SimpleEntity("name_" + i, i % 2 == 0, i));

        try (Connection conn = database.getConnection()) {
            SqlUtil.createTableForEntity(conn, SimpleEntity.class);
            insert.insertAll(conn, stream);

            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery("SELECT name, active, age FROM simple_table ORDER BY age ASC")) {
                    for (int i = 0; i < times; i++) {
                        assertTrue(rs.next());
                        assertEquals("name_" + i, rs.getString("name"));
                        assertEquals(i % 2 == 0, rs.getBoolean("active"));
                        assertEquals(i, rs.getInt("age"));
                    }
                }
            }
        }
    }

}
