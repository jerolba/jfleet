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

import static org.jfleet.EntityFieldType.FieldTypeEnum.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
 * This example shows how to use JFleet to persist a collection of Lists
 */
public class FromArray {

    public static void main(String[] args) throws IOException, SQLException {
        Supplier<Connection> connectionSuplier = new MySqlTestConnectionProvider();
        try (Connection connection = connectionSuplier.get()) {
            TableHelper.createTable(connection);
            CitiBikeReader<List<Object>> reader = new CitiBikeReader<>("/tmp", str -> new TripParserToMap(str));

            EntityInfo entityInfo= new EntityInfoBuilder<>(List.class, "bike_trip")
                    .addColumn("tripduration", INT, map -> map.get(0))
                    .addColumn("starttime", TIMESTAMP, map -> map.get(1))
                    .addColumn("stoptime", TIMESTAMP, map -> map.get(2))
                    .addColumn("start_station_id", INT, map -> map.get(3))
                    .addColumn("start_station_name", STRING, map -> map.get(4))
                    .addColumn("start_station_latitude", DOUBLE, map -> map.get(5))
                    .addColumn("start_station_longitude", DOUBLE, map -> map.get(6))
                    .addColumn("end_station_id", INT, map -> map.get(7))
                    .addColumn("end_station_name", STRING, map -> map.get(8))
                    .addColumn("end_station_latitude", DOUBLE, map -> map.get(9))
                    .addColumn("end_station_longitude", DOUBLE, map -> map.get(10))
                    .addColumn("bike_id", LONG, map -> map.get(11))
                    .addColumn("user_type", STRING, map -> map.get(12))
                    .addColumn("birth_year", INT, map -> map.get(13))
                    .addColumn("gender", CHAR, map -> map.get(14))
                    .build();

            BulkInsert<List<Object>> bulkInsert = new LoadDataBulkInsert<>(entityInfo);
            reader.forEachCsvInZip(trips -> {
                try {
                    bulkInsert.insertAll(connection, trips);
                } catch (JFleetException | SQLException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static class TripParserToMap extends CSVParser<List<Object>> {

        public TripParserToMap(String line) {
            super(line, 15);
        }

        @Override
        public List<Object> parse() {
            List<Object> trip = new ArrayList<>();
            trip.add(nextInteger());
            trip.add(nextDate());
            trip.add(nextDate());
            trip.add(nextInteger());
            trip.add(nextString());
            trip.add(nextDouble());
            trip.add(nextDouble());
            trip.add(nextInteger());
            trip.add(nextString());
            trip.add(nextDouble());
            trip.add(nextDouble());
            trip.add(nextLong());
            trip.add(nextString());
            trip.add(nextInteger());
            trip.add(nextChar());
            return trip;
        }
    }
}
