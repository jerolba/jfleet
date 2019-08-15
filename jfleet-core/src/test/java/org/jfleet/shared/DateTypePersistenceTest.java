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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.stream.Stream;

import org.jfleet.BulkInsert;
import org.jfleet.parameterized.IsMySql5Present;
import org.jfleet.parameterized.TestDBs;
import org.jfleet.shared.entities.EntityWithDateTypes;
import org.jfleet.util.Database;
import org.jfleet.util.SqlUtil;

public class DateTypePersistenceTest {

    @TestDBs
    @IsMySql5Present("Pending review configuration of timezones with MySql8 driver")
    public void persistAllDateTypes(Database database) throws Exception {
        EntityWithDateTypes entity = new EntityWithDateTypes();
        entity.setNonAnnotatedDate(getDate("24/01/2012 23:12:48"));
        entity.setDate(getDate("24/01/2012 23:12:48"));
        entity.setTime(getDate("24/01/2012 23:12:48"));
        entity.setTimeStamp(getDate("24/01/2012 23:12:48"));
        entity.setSqlDate(java.sql.Date.valueOf("2017-08-02"));
        entity.setSqlTime(java.sql.Time.valueOf("09:13:23"));
        entity.setSqlTimeStamp(java.sql.Timestamp.valueOf("2017-08-02 09:13:23"));
        entity.setLocalDate(LocalDate.of(2012, 01, 24));
        entity.setLocalTime(LocalTime.of(23, 12, 48));
        entity.setLocalDateTime(LocalDateTime.of(2012, 01, 24, 23, 12, 48));

        BulkInsert<EntityWithDateTypes> insert = database.getBulkInsert(EntityWithDateTypes.class);

        try (Connection conn = database.getConnection()) {
            SqlUtil.createTableForEntity(conn, EntityWithDateTypes.class);
            insert.insertAll(conn, Stream.of(entity));

            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery("SELECT nonAnnotatedDate, date, time, "
                        + "timeStamp, sqlDate, sqlTime, sqlTimeStamp, localDate, local_time, localDateTime "
                        + "FROM table_with_date_types")) {
                    assertTrue(rs.next());
                    assertEquals(getDate("24/01/2012 23:12:48"), rs.getTimestamp("nonAnnotatedDate"));
                    assertEquals(java.sql.Date.valueOf("2012-1-24"), rs.getDate("date"));
                    assertEquals(java.sql.Time.valueOf("23:12:48"), rs.getTime("time"));
                    assertEquals(java.sql.Timestamp.valueOf("2012-1-24 23:12:48"), rs.getTimestamp("timeStamp"));
                    assertEquals(java.sql.Date.valueOf("2017-08-02"), rs.getDate("sqlDate"));
                    assertEquals(java.sql.Time.valueOf("09:13:23"), rs.getTime("sqlTime"));
                    assertEquals(java.sql.Timestamp.valueOf("2017-08-02 09:13:23"), rs.getTimestamp("sqlTimeStamp"));
                    assertEquals(java.sql.Date.valueOf("2012-1-24"), rs.getDate("localDate"));
                    assertEquals(java.sql.Time.valueOf("23:12:48"), rs.getTime("local_time"));
                    assertEquals(java.sql.Timestamp.valueOf("2012-1-24 23:12:48"), rs.getTimestamp("localDateTime"));
                }
            }
        }
    }

    @TestDBs
    public void persistNullDateTypes(Database database) throws Exception {
        EntityWithDateTypes entity = new EntityWithDateTypes();
        entity.setNonAnnotatedDate(null);
        entity.setDate(null);
        entity.setTime(null);
        entity.setTimeStamp(null);
        entity.setSqlDate(null);
        entity.setSqlTime(null);
        entity.setSqlTimeStamp(null);
        entity.setLocalDate(null);
        entity.setLocalTime(null);
        entity.setLocalDateTime(null);

        BulkInsert<EntityWithDateTypes> insert = database.getBulkInsert(EntityWithDateTypes.class);

        try (Connection conn = database.getConnection()) {
            SqlUtil.createTableForEntity(conn, EntityWithDateTypes.class);
            insert.insertAll(conn, Stream.of(entity));

            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery("SELECT nonAnnotatedDate, date, time, "
                        + "timeStamp, sqlDate, sqlTime, sqlTimeStamp, localDate, local_time, localDateTime "
                        + "FROM table_with_date_types")) {
                    assertTrue(rs.next());
                    assertEquals(null, rs.getTimestamp("nonAnnotatedDate"));
                    assertTrue(rs.wasNull());
                    assertEquals(null, rs.getDate("date"));
                    assertTrue(rs.wasNull());
                    assertEquals(null, rs.getTime("time"));
                    assertTrue(rs.wasNull());
                    assertEquals(null, rs.getTimestamp("timeStamp"));
                    assertTrue(rs.wasNull());
                    assertEquals(null, rs.getDate("sqlDate"));
                    assertTrue(rs.wasNull());
                    assertEquals(null, rs.getTime("sqlTime"));
                    assertTrue(rs.wasNull());
                    assertEquals(null, rs.getTimestamp("sqlTimeStamp"));
                    assertTrue(rs.wasNull());
                    assertEquals(null, rs.getDate("localDate"));
                    assertTrue(rs.wasNull());
                    assertEquals(null, rs.getTime("local_time"));
                    assertTrue(rs.wasNull());
                    assertEquals(null, rs.getTimestamp("localDateTime"));
                    assertTrue(rs.wasNull());
                }
            }
        }
    }

    private Date getDate(String str) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        try {
            return sdf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}
