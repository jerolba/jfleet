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
package org.jfleet.mssql.manual;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.stream.Stream;

import org.jfleet.EntityInfo;
import org.jfleet.EntityInfoBuilder;
import org.jfleet.mssql.JFleetSQLServerBulkRecord;
import org.jfleet.util.MsSqlDatabase;
import org.junit.jupiter.api.Test;

import com.microsoft.sqlserver.jdbc.SQLServerBulkCSVFileRecord;
import com.microsoft.sqlserver.jdbc.SQLServerBulkCopy;
import com.microsoft.sqlserver.jdbc.SQLServerBulkCopyOptions;

public class ConnectionTest {

    @Test
    public void canConnectToTestDB() throws SQLException, IOException {
        try (java.sql.Connection conn = new MsSqlDatabase().getConnection()) {
            assertNotNull(conn);
        }
    }

    @Test
    public void canExecuteBulkCopyFromCSV() throws SQLException, IOException {
        int someValue = 12345;
        String otherValue = "foobar";

        try (Connection conn =  new MsSqlDatabase().getConnection()) {
            try (Statement stmt = (Statement) conn.createStatement()) {
                stmt.execute("DROP TABLE IF EXISTS simple_table");
                stmt.execute("CREATE TABLE simple_table (some_column INTEGER, other_column VARCHAR(255))");

                String someData = someValue + "," + otherValue + ",\n";
                ByteArrayInputStream inputStream = new ByteArrayInputStream(someData.getBytes(StandardCharsets.UTF_8.name()));
                
                SQLServerBulkCSVFileRecord fileRecord = new SQLServerBulkCSVFileRecord(
                        inputStream, StandardCharsets.UTF_8.name(), ",", false);
                
                fileRecord.addColumnMetadata(1, "some_column", Types.INTEGER, 0, 0);
                fileRecord.addColumnMetadata(2, "other_column", Types.VARCHAR, 0, 0);
                
                SQLServerBulkCopyOptions opt = new SQLServerBulkCopyOptions();
                opt.setBatchSize(100);
                try (SQLServerBulkCopy copy = new SQLServerBulkCopy(conn)){
                    copy.setBulkCopyOptions(opt);
                    copy.setDestinationTableName("simple_table");
                    copy.writeToServer(fileRecord);
                }
                
                try (ResultSet rs = stmt.executeQuery("SELECT some_column, other_column FROM simple_table")) {
                    assertTrue(rs.next());
                    assertEquals(rs.getInt(1), someValue);
                    assertEquals(rs.getString(2), otherValue);
                }

            }
        }
    }
    
    @Test
    public void canExecuteLoadDataJFleet() throws SQLException, IOException {
        
        int someValue = 12345;
        String otherValue = "foobar";
        
        EntityInfo entityInfo= new EntityInfoBuilder<>(MyEntity.class, "simple_table")
                .addField("someColumn", "some_column")
                .addField("otherColumn", "other_column")
                .build();

        try (Connection conn =  new MsSqlDatabase().getConnection()) {
            try (Statement stmt = (Statement) conn.createStatement()) {
                stmt.execute("DROP TABLE IF EXISTS simple_table");
                stmt.execute("CREATE TABLE simple_table (some_column INTEGER, other_column VARCHAR(255))");
                
                Stream<MyEntity> stream = Stream.of(new MyEntity(someValue, otherValue));

                JFleetSQLServerBulkRecord fileRecord = new JFleetSQLServerBulkRecord(entityInfo, stream);
                
                SQLServerBulkCopyOptions opt = new SQLServerBulkCopyOptions();
                opt.setBatchSize(100);
                try (SQLServerBulkCopy copy = new SQLServerBulkCopy(conn)){
                    copy.setBulkCopyOptions(opt);
                    copy.setDestinationTableName("simple_table");
                    copy.writeToServer(fileRecord);
                }
                
                try (ResultSet rs = stmt.executeQuery("SELECT some_column, other_column FROM simple_table")) {
                    assertTrue(rs.next());
                    assertEquals(rs.getInt(1), someValue);
                    assertEquals(rs.getString(2), otherValue);
                }
            }
        }
    }
    
    public static class MyEntity {
        
        private int someColumn;
        private String otherColumn;
        
        public MyEntity(int someColumn, String otherColumn) {
            this.someColumn = someColumn;
            this.otherColumn = otherColumn;
        }

        public int getSomeColumn() {
            return someColumn;
        }

        public void setSomeColumn(int someColumn) {
            this.someColumn = someColumn;
        }

        public String getOtherColumn() {
            return otherColumn;
        }

        public void setOtherColumn(String otherColumn) {
            this.otherColumn = otherColumn;
        }
    }

}
