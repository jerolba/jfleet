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
package org.jfleet.postgres.manual;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.jfleet.postgres.PostgresTestConnectionProvider;
import org.jfleet.postgres.StringBuilderReader;
import org.junit.Test;
import org.postgresql.copy.CopyManager;
import org.postgresql.jdbc.PgConnection;

public class ConnectionTest {

    @Test
    public void canConnectToTestDB() throws SQLException, IOException {
        PostgresTestConnectionProvider connectionProvider = new PostgresTestConnectionProvider();
        try (java.sql.Connection conn = connectionProvider.get()) {
            assertNotNull(conn);
        }
    }

    @Test
    public void canExecuteCopy() throws SQLException, IOException {
        int someValue = 12345;
        String otherValue = "foobar";

        PostgresTestConnectionProvider connectionProvider = new PostgresTestConnectionProvider();
        try (Connection conn = connectionProvider.get()) {
            try (Statement stmt = conn.createStatement()) {
                PgConnection unwrapped = conn.unwrap(PgConnection.class);
                stmt.execute("DROP TABLE IF EXISTS simple_table");
                stmt.execute("CREATE TABLE simple_table (some_column INTEGER, other_column VARCHAR(255))");

                CopyManager copyManager = unwrapped.getCopyAPI();

                String sql = "COPY simple_table (some_column, other_column) FROM STDIN WITH ("
                        + "ENCODING 'UTF-8', DELIMITER '\t', HEADER false" + ")";
                String someData = someValue + "\t" + otherValue + "\n";

                StringBuilder sb = new StringBuilder(someData);
                Reader reader = new StringBuilderReader(sb);
                copyManager.copyIn(sql, reader);

                try (ResultSet rs = stmt.executeQuery("SELECT some_column, other_column FROM simple_table")) {
                    assertTrue(rs.next());
                    assertEquals(rs.getInt(1), someValue);
                    assertEquals(rs.getString(2), otherValue);
                }
            }
        }
    }

    @Test
    public void canExecuteLoadDataWithNullValues() throws SQLException, IOException {
        int someValue = 12345;
        String otherValue = "foobar";

        PostgresTestConnectionProvider connectionProvider = new PostgresTestConnectionProvider();
        try (Connection conn = connectionProvider.get()) {
            try (Statement stmt = conn.createStatement()) {
                PgConnection unwrapped = conn.unwrap(PgConnection.class);
                stmt.execute("DROP TABLE IF EXISTS simple_table");
                stmt.execute("CREATE TABLE simple_table (some_column INTEGER, other_column VARCHAR(255))");

                CopyManager copyManager = unwrapped.getCopyAPI();

                String sql = "COPY simple_table (some_column, other_column) FROM STDIN WITH ("
                        + "ENCODING 'UTF-8', DELIMITER '\t', HEADER false" + ")";

                // Empty string, without "" identify a null value
                String row1 = someValue + "\t\\N\n";
                String row2 = "\\N\t" + otherValue + "\n";
                StringBuilder sb = new StringBuilder(row1).append(row2);
                Reader reader = new StringBuilderReader(sb);
                copyManager.copyIn(sql, reader);

                try (ResultSet rs = stmt.executeQuery("SELECT some_column, other_column FROM simple_table")) {
                    assertTrue(rs.next());
                    assertEquals(someValue, rs.getInt(1));
                    assertEquals(null, rs.getString(2));
                    assertTrue(rs.next());
                    assertEquals(0, rs.getInt(1));
                    assertTrue(rs.wasNull());
                    assertEquals(otherValue, rs.getString(2));
                }
            }
        }
    }
}
