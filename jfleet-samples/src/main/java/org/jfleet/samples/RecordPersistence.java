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
package org.jfleet.samples;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.function.Supplier;

import org.jfleet.BulkInsert;
import org.jfleet.EntityInfo;
import org.jfleet.JFleetException;
import org.jfleet.mysql.LoadDataBulkInsert;
import org.jfleet.record.ColumnNamingStrategy;
import org.jfleet.record.EntityInfoRecordBuilder;
import org.jfleet.samples.shared.CSVParser;
import org.jfleet.samples.shared.CitiBikeReader;
import org.jfleet.util.MySqlTestConnectionProvider;

public class RecordPersistence {

    public static void main(String[] args) throws IOException, SQLException {
        Supplier<Connection> connectionSuplier = new MySqlTestConnectionProvider();
        try (Connection connection = connectionSuplier.get()) {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("DROP TABLE IF EXISTS bike_trip");
                stmt.execute(
                        "CREATE TABLE bike_trip (id INT NOT NULL AUTO_INCREMENT, trip_duration INT NOT NULL, "
                                + "start_time DATETIME, stop_time DATETIME, start_station_id INT NOT NULL, start_station_name VARCHAR(255), "
                                + "start_station_latitude DOUBLE NOT NULL, start_station_longitude DOUBLE NOT NULL, "
                                + "end_station_id INT NOT NULL, end_station_name VARCHAR(255), end_station_latitude DOUBLE NOT NULL, "
                                + "end_station_longitude DOUBLE NOT NULL, bike_id BIGINT NOT NULL, user_type VARCHAR(255), "
                                + "birth_year INT, gender CHAR, PRIMARY KEY (id))");
            }

            CitiBikeReader<TripRecord> reader = new CitiBikeReader<>("/tmp", TripRecordParser::new);

            EntityInfo entityInfo = EntityInfoRecordBuilder.fromRecord(TripRecord.class).name("bike_trip")
                    .columnNamingStrategy(ColumnNamingStrategy.SNAKE_CASE).build();

            BulkInsert<TripRecord> bulkInsert = new LoadDataBulkInsert<>(entityInfo);
            reader.forEachCsvInZip(trips -> {
                try {
                    bulkInsert.insertAll(connection, trips);
                } catch (JFleetException | SQLException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public record TripRecord(int tripDuration, Date startTime, Date stopTime,
            int startStationId, String startStationName, double startStationLatitude, double startStationLongitude,
            int endStationId, String endStationName, double endStationLatitude, double endStationLongitude,
            long bikeId, String userType, Integer birthYear, Character gender) {

    }

    static class TripRecordParser extends CSVParser<TripRecord> {

        public TripRecordParser(String line) {
            super(line, 15);
        }

        @Override
        public TripRecord parse() {
            return new TripRecord(
                    nextInteger(), nextDate(), nextDate(),
                    nextInteger(), nextString(), nextDouble(), nextDouble(),
                    nextInteger(), nextString(), nextDouble(), nextDouble(),
                    nextLong(), nextString(), nextInteger(), nextChar());
        }

    }

}
