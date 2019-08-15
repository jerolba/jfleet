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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.jfleet.BulkInsert;
import org.jfleet.parameterized.IsMySql5Present;
import org.jfleet.util.MySqlDatabase;
import org.jfleet.util.SqlUtil;
import org.junit.jupiter.api.Test;

public class MySqlReservedWordTest {

    @Entity
    @Table(name = "\"select\"")
    public class ReservedWordEntity {

        private int id;
        @Column(name = "\"current_user\"")
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

    @Test
    @IsMySql5Present
    public void canPersistWithReservedWords() throws Exception {
        int times = 1000;
        BulkInsert<ReservedWordEntity> insert = new LoadDataBulkInsert<>(ReservedWordEntity.class);
        Stream<ReservedWordEntity> stream = IntStream.range(0, times)
                .mapToObj(i -> new ReservedWordEntity(i, "current_user_" + i));

        try (Connection conn = new MySqlDatabase().getConnection()) {
            SqlUtil.createTableForEntity(conn, ReservedWordEntity.class);
            insert.insertAll(conn, stream);

            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery("SELECT id, `current_user` FROM `select` ORDER BY id ASC")) {
                    for (int i = 0; i < times; i++) {
                        assertTrue(rs.next());
                        assertEquals("current_user_" + i, rs.getString("current_user"));
                    }
                }
            }
        }
    }

}
