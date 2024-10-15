/**
 * Copyright 2022 Jerónimo López Bezanilla
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

import org.jfleet.BulkInsert;
import org.jfleet.parameterized.DBs;
import org.jfleet.parameterized.TestDBs;
import org.jfleet.util.Database;
import org.jfleet.util.SqlUtil;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.jfleet.parameterized.Databases.JdbcPosgres;
import static org.jfleet.parameterized.Databases.Postgres;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PostgresReservedWordTest {

    @Entity
    @Table(name = "\"select\"")
    public class ReservedWordEntity {

        private int id;
        @Column(name = "\"user\"")
        private String user;

        public ReservedWordEntity(int id, String user) {
            this.id = id;
            this.user = user;
        }

        public int getId() {
            return id;
        }

        public String getUser() {
            return user;
        }

    }

    @TestDBs
    @DBs({ Postgres, JdbcPosgres })
    public void canPersistWithReservedWords(Database database) throws Exception {
        int times = 1000;
        BulkInsert<ReservedWordEntity> insert = database.getBulkInsert(ReservedWordEntity.class);
        Stream<ReservedWordEntity> stream = IntStream.range(0, times)
                .mapToObj(i -> new ReservedWordEntity(i, "user_" + i));

        try (Connection conn = database.getConnection()) {
            SqlUtil.createTableForEntity(conn, ReservedWordEntity.class);
            insert.insertAll(conn, stream);

            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery("SELECT id, \"user\" FROM \"select\" ORDER BY id ASC")) {
                    for (int i = 0; i < times; i++) {
                        assertTrue(rs.next());
                        assertEquals("user_" + i, rs.getString("user"));
                    }
                }
            }
        }
    }

}
