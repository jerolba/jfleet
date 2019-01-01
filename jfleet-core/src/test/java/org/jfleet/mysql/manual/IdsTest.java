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
package org.jfleet.mysql.manual;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jfleet.util.Database;
import org.jfleet.util.MySqlDatabase;
import org.junit.jupiter.api.Test;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

public class IdsTest {

    private Database database = new MySqlDatabase();

    @Test
    public void canExecuteLoadDataWithAutoIncrement() throws SQLException, IOException {
        try (Connection conn = (Connection) database.getConnection()) {
            conn.setAllowLoadLocalInfile(true);
            try (Statement stmt = (Statement) conn.createStatement()) {
                stmt.execute("CREATE TEMPORARY TABLE table_with_id "
                        + "(id INT NOT NULL AUTO_INCREMENT, some_column VARCHAR(255), PRIMARY KEY (id))");

                String row1 = "foo\t\n";
                String row2 = "bar\t\n";
                String someData = row1 + row2;
                stmt.setLocalInfileInputStream(new ByteArrayInputStream(someData.getBytes(Charset.forName("UTF-8"))));
                String sql = "LOAD DATA LOCAL INFILE '' INTO TABLE table_with_id "
                        + "FIELDS TERMINATED BY '\t' ENCLOSED BY '' ESCAPED BY '\\\\' "
                        + "LINES TERMINATED BY '\n' STARTING BY ''"
                        + "(some_column)";
                stmt.execute(sql);

                try (ResultSet rs = stmt.executeQuery("SELECT id, some_column FROM table_with_id")) {
                    assertTrue(rs.next());
                    assertEquals(1, rs.getInt(1));
                    assertEquals("foo", rs.getString(2));
                    assertTrue(rs.next());
                    assertEquals(2, rs.getInt(1));
                    assertEquals("bar", rs.getString(2));
                }
            }
        }
    }

    @Test
    public void canExecuteLoadDataWithAutoIncrementIdSetted() throws SQLException, IOException {
        try (Connection conn = (Connection) database.getConnection()) {
            conn.setAllowLoadLocalInfile(true);
            try (Statement stmt = (Statement) conn.createStatement()) {
                stmt.execute("CREATE TEMPORARY TABLE table_with_id "
                        + "(id INT NOT NULL AUTO_INCREMENT, some_column VARCHAR(255), PRIMARY KEY (id))");

                String row1 = "10\tfoo\t\n";
                String row2 = "\\N\tbar\t\n";
                String someData = row1 + row2;
                stmt.setLocalInfileInputStream(new ByteArrayInputStream(someData.getBytes(Charset.forName("UTF-8"))));
                String sql = "LOAD DATA LOCAL INFILE '' INTO TABLE table_with_id "
                        + "FIELDS TERMINATED BY '\t' ENCLOSED BY '' ESCAPED BY '\\\\' "
                        + "LINES TERMINATED BY '\n' STARTING BY ''"
                        + "(id, some_column)";
                stmt.execute(sql);

                try (ResultSet rs = stmt.executeQuery("SELECT id, some_column FROM table_with_id ORDER BY id ASC")) {
                    assertTrue(rs.next());
                    assertEquals(10, rs.getInt(1));
                    assertEquals("foo", rs.getString(2));
                    assertTrue(rs.next());
                    assertEquals(11, rs.getInt(1));
                    assertEquals("bar", rs.getString(2));
                }
            }
        }
    }
}
