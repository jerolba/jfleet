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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.jfleet.BulkInsert;
import org.jfleet.parameterized.TestDBs;
import org.jfleet.util.Database;
import org.jfleet.util.SqlUtil;

public class EncodingTest {

    @Entity
    @Table(name = "table_utf8_encoding")
    public class EncodingUtf8Entity {

        private String lang;
        private String text;

        public EncodingUtf8Entity(String lang, String text) {
            this.lang = lang;
            this.text = text;
        }

        public String getLang() {
            return lang;
        }

        public String getText() {
            return text;
        }

    }

    @TestDBs
    public void persistAllTypes(Database database) throws Exception {
        List<EncodingUtf8Entity> values = fetchTestValues();
        BulkInsert<EncodingUtf8Entity> insert = database.getBulkInsert(EncodingUtf8Entity.class);

        try (Connection conn = database.getConnection()) {
            SqlUtil.createTableForEntity(conn, EncodingUtf8Entity.class);
            insert.insertAll(conn, values);
            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery("SELECT * FROM table_utf8_encoding ORDER BY lang ASC")) {
                    for (int i = 0; i < values.size(); i++) {
                        EncodingUtf8Entity expected = values.get(i);
                        assertTrue(rs.next());
                        assertEquals(expected.getLang(), rs.getString("lang"));
                        assertEquals(expected.getText(), rs.getString("text"));
                    }
                }
            }
        }
    }

    private List<EncodingUtf8Entity> fetchTestValues() throws IOException {
        try (InputStream resource = this.getClass().getClassLoader().getResourceAsStream("utf8-encoded.txt")) {
            return new BufferedReader(new InputStreamReader(resource, StandardCharsets.UTF_8)).lines()
                    .map(l -> l.split("=")).map(arr -> new EncodingUtf8Entity(arr[0], arr[1]))
                    .collect(Collectors.toList());
        }
    }
}
