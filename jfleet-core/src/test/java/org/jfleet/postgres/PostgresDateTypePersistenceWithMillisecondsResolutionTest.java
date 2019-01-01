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
package org.jfleet.postgres;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.stream.Stream;

import org.jfleet.BulkInsert;
import org.jfleet.parameterized.TestDBs;
import org.jfleet.parameterized.WithDB;
import org.jfleet.shared.entities.EntityWithDateTypes;
import org.jfleet.util.Database;
import org.jfleet.util.PostgresDatabase;
import org.jfleet.util.SqlUtil;
import org.junit.jupiter.params.provider.ValueSource;

public class PostgresDateTypePersistenceWithMillisecondsResolutionTest {

    @TestDBs
    @ValueSource(strings = {"Postgres", "JdbcPosgres"})
    public void persistWithMillisecondResolution(@WithDB Database database) throws Exception {
        EntityWithDateTypes entity = new EntityWithDateTypes();
        entity.setNonAnnotatedDate(getDate("24/01/2012 23:12:48.132"));
        entity.setTime(getDate("24/01/2012 23:12:48.132"));
        entity.setTimeStamp(getDate("24/01/2012 23:12:48.132"));
        entity.setSqlTime(new java.sql.Time(getDate("24/01/2012 23:12:48.132").getTime()));
        entity.setSqlTimeStamp(new Timestamp(getDate("24/01/2012 23:12:48.132").getTime()));
        entity.setLocalTime(LocalTime.of(23, 12, 48, 132_000_000));
        entity.setLocalDateTime(LocalDateTime.of(2012, 01, 24, 23, 12, 48, 132_000_000));

        BulkInsert<EntityWithDateTypes> insert = database.getBulkInsert(EntityWithDateTypes.class);

        try (Connection conn = new PostgresDatabase().getConnection()) {
            SqlUtil.createTableForEntity(conn, EntityWithDateTypes.class);
            insert.insertAll(conn, Stream.of(entity));

            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery("SELECT nonAnnotatedDate, date, time, "
                        + "timeStamp, sqlDate, sqlTime, sqlTimeStamp, localDate, local_time, localDateTime "
                        + "FROM table_with_date_types")) {
                    assertTrue(rs.next());
                    Time expectedTime = new java.sql.Time(getTime("23:12:48.132").getTime());
                    Timestamp expectedTimeStamp = java.sql.Timestamp.valueOf("2012-1-24 23:12:48.132");
                    assertEquals(getDate("24/01/2012 23:12:48.132"), rs.getTimestamp("nonAnnotatedDate"));
                    assertEquals(expectedTime, rs.getTime("time"));
                    assertEquals(expectedTimeStamp, rs.getTimestamp("timeStamp"));
                    assertEquals(expectedTimeStamp, rs.getTimestamp("sqlTimeStamp"));
                    assertEquals(expectedTime, rs.getTime("local_time"));
                    assertEquals(expectedTimeStamp, rs.getTimestamp("localDateTime"));
                }
            }
        }
    }

    private Date getDate(String str) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
        try {
            return sdf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Date getTime(String str) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        try {
            return sdf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}
