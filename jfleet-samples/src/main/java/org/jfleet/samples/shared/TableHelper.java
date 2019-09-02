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
package org.jfleet.samples.shared;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TableHelper {

    public static void createTable(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS bike_trip");
            stmt.execute(
                    "CREATE TABLE bike_trip (id INT IDENTITY PRIMARY KEY, tripduration INT NOT NULL, "
                    + "starttime DATETIME, stoptime DATETIME, start_station_id INT NOT NULL, start_station_name VARCHAR(255), "
                    + "start_station_latitude FLOAT NOT NULL, start_station_longitude FLOAT NOT NULL, "
                    + "end_station_id INT NOT NULL, end_station_name VARCHAR(255), end_station_latitude FLOAT NOT NULL, "
                    + "end_station_longitude FLOAT NOT NULL, bike_id BIGINT NOT NULL, user_type VARCHAR(255), "
                    + "birth_year INT, gender CHAR)");
        }
    };

    public static void createTableDistance(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS bike_trip_distance");
            stmt.execute(
                    "CREATE TABLE bike_trip_distance (id INT NOT NULL IDENTITY PRIMARY KEY, tripduration INT NOT NULL, distance FLOAT, "
                    + "bike_id BIGINT NOT NULL, user_type VARCHAR(255), "
                    + "birth_year INT, gender CHAR)");
        }
    };

}
