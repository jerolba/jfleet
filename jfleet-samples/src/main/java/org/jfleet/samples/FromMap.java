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
package org.jfleet.samples;

import static org.jfleet.EntityFieldType.FieldTypeEnum.CHAR;
import static org.jfleet.EntityFieldType.FieldTypeEnum.DOUBLE;
import static org.jfleet.EntityFieldType.FieldTypeEnum.INT;
import static org.jfleet.EntityFieldType.FieldTypeEnum.LONG;
import static org.jfleet.EntityFieldType.FieldTypeEnum.STRING;
import static org.jfleet.EntityFieldType.FieldTypeEnum.TIMESTAMP;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.jfleet.BulkInsert;
import org.jfleet.EntityInfo;
import org.jfleet.EntityInfoBuilder;
import org.jfleet.JFleetException;
import org.jfleet.mysql.LoadDataBulkInsert;
import org.jfleet.samples.shared.CSVParser;
import org.jfleet.samples.shared.CitiBikeReader;
import org.jfleet.samples.shared.TableHelper;
import org.jfleet.util.MySqlTestConnectionProvider;

/*
 * This example shows how to use JFleet to persist a collection of Maps. Probably it's more expensive
 * than using an object, but it's a sample of the flexibility of JFleet mapping
 */
public class FromMap {

    public static void main(String[] args) throws IOException, SQLException {
        Supplier<Connection> connectionSuplier = new MySqlTestConnectionProvider();
        try (Connection connection = connectionSuplier.get()) {
            TableHelper.createTable(connection);
            CitiBikeReader<Map<String, Object>> reader = new CitiBikeReader<>("/tmp", str -> new TripParserToMap(str));

            EntityInfo entityInfo= new EntityInfoBuilder<>(Map.class, "bike_trip")
                    .addColumn("tripduration", INT, map -> map.get("TripDuration"))
                    .addColumn("starttime", TIMESTAMP, map -> map.get("StartTime"))
                    .addColumn("stoptime", TIMESTAMP, map -> map.get("StopTime"))
                    .addColumn("start_station_id", INT, map -> map.get("StartStationId"))
                    .addColumn("start_station_name", STRING, map -> map.get("StartStationName"))
                    .addColumn("start_station_latitude", DOUBLE, map -> map.get("StartStationLatitude"))
                    .addColumn("start_station_longitude", DOUBLE, map -> map.get("StartStationLongitude"))
                    .addColumn("end_station_id", INT, map -> map.get("EndStationId"))
                    .addColumn("end_station_name", STRING, map -> map.get("EndStationName"))
                    .addColumn("end_station_latitude", DOUBLE, map -> map.get("EndStationLatitude"))
                    .addColumn("end_station_longitude", DOUBLE, map -> map.get("EndStationLongitude"))
                    .addColumn("bike_id", LONG, map -> map.get("BikeId"))
                    .addColumn("user_type", STRING, map -> map.get("UserType"))
                    .addColumn("birth_year", INT, map -> map.get("BirthYear"))
                    .addColumn("gender", CHAR, map -> map.get("Gender"))
                    .build();

            LoadDataBulkInsert.Configuration<Map<String, Object>> config = new LoadDataBulkInsert.Configuration<>(entityInfo);

            BulkInsert<Map<String, Object>> bulkInsert = new LoadDataBulkInsert<>(config);
            reader.forEachCsvInZip(trips -> {
                try {
                    bulkInsert.insertAll(connection, trips);
                } catch (JFleetException | SQLException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static class TripParserToMap extends CSVParser<Map<String, Object>> {

        public TripParserToMap(String line) {
            super(line, 15);
        }

        @Override
        public Map<String, Object> parse() {
            Map<String, Object> trip = new HashMap<>();
            trip.put("TripDuration", nextInteger());
            trip.put("StartTime", nextDate());
            trip.put("StopTime", nextDate());
            trip.put("StartStationId", nextInteger());
            trip.put("StartStationName", nextString());
            trip.put("StartStationLatitude", nextDouble());
            trip.put("StartStationLongitude", nextDouble());
            trip.put("EndStationId", nextInteger());
            trip.put("EndStationName", nextString());
            trip.put("EndStationLatitude", nextDouble());
            trip.put("EndStationLongitude", nextDouble());
            trip.put("BikeId", nextLong());
            trip.put("UserType", nextString());
            trip.put("BirthYear", nextInteger());
            trip.put("Gender", nextChar());
            return trip;
        }
    }
}
