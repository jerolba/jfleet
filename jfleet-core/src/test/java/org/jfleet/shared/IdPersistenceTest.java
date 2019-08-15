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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.jfleet.BulkInsert;
import org.jfleet.parameterized.TestDBs;
import org.jfleet.util.Database;
import org.jfleet.util.SqlUtil;

public class IdPersistenceTest {

    @Entity
    @Table(name = "simple_table")
    public class EntityWithAssignedId {

        @Id
        private Long id;
        private String name;

        public EntityWithAssignedId(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

    }

    @TestDBs
    public void canPersistWithAssignedId(Database database) throws Exception {
        int times = 1000;
        BulkInsert<EntityWithAssignedId> insert = database.getBulkInsert(EntityWithAssignedId.class);
        Stream<EntityWithAssignedId> stream = LongStream.range(0, times)
                .mapToObj(i -> new EntityWithAssignedId(i, "name_" + i));

        try (Connection conn = database.getConnection()) {
            SqlUtil.createTableForEntity(conn, EntityWithAssignedId.class);
            insert.insertAll(conn, stream);

            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery("SELECT id, name FROM simple_table ORDER BY id ASC")) {
                    for (int i = 0; i < times; i++) {
                        assertTrue(rs.next());
                        assertEquals(i, rs.getLong("id"));
                        assertEquals("name_" + i, rs.getString("name"));
                    }
                }
            }
        }
    }


    @Entity
    @Table(name = "simple_table")
    public class EntityWithIdentityId {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String name;

        public EntityWithIdentityId(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

    }

    @TestDBs
    public void canPersistWithIdentityId(Database database) throws Exception {
        int times = 1000;
        BulkInsert<EntityWithIdentityId> insert = database.getBulkInsert(EntityWithIdentityId.class);
        Stream<EntityWithIdentityId> stream = LongStream.range(0, times)
                .mapToObj(i -> new EntityWithIdentityId(null, "name_" + i));

        try (Connection conn = database.getConnection()) {
            SqlUtil.createTableForEntity(conn, EntityWithIdentityId.class);
            insert.insertAll(conn, stream);

            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery("SELECT id, name FROM simple_table ORDER BY id ASC")) {
                    for (int i = 0; i < times; i++) {
                        assertTrue(rs.next());
                        assertNotNull(rs.getLong("id"));
                        assertEquals("name_" + i, rs.getString("name"));
                    }
                }
            }
        }
    }
}
