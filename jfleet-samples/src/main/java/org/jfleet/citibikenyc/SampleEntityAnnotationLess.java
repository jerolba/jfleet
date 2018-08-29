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
package org.jfleet.citibikenyc;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

import org.jfleet.BulkInsert;
import org.jfleet.EntityInfo;
import org.jfleet.EntityInfoBuilder;
import org.jfleet.JFleetException;
import org.jfleet.citibikenyc.entities.Trip;
import org.jfleet.mysql.LoadDataBulkInsert;
import org.jfleet.util.MySqlTestConnectionProvider;


/*
 * This example shows how to configure JFleet without javax.persistence annotations
 *
 */
public class SampleEntityAnnotationLess {

    public static void main(String[] args) throws IOException, SQLException {
        Supplier<Connection> connectionSuplier = new MySqlTestConnectionProvider();
        try (Connection connection = connectionSuplier.get()) {
            TableHelper.createTable(connection);
            CitiBikeReader<Trip> reader = new CitiBikeReader<>("/tmp", str -> new TripParser(str));

            EntityInfo entityInfo= new EntityInfoBuilder<>(Trip.class, "bike_trip")
                    .addField("id", "id", true)
                    .addField("tripDuration", "tripduration")
                    .addField("startTime", "starttime")
                    .addField("stopTime", "stoptime")
                    .addField("startStation.id", "start_station_id")
                    .addField("startStation.name", "start_station_name")
                    .addField("startStation.latitude", "start_station_latitude")
                    .addField("startStation.longitude", "start_station_longitude")
                    .addField("endStation.id", "end_station_id")
                    .addField("endStation.name", "end_station_name")
                    .addField("endStation.latitude", "end_station_latitude")
                    .addField("endStation.longitude", "end_station_longitude")
                    .addField("bikeId", "bike_id")
                    .addField("userType", "user_type")
                    .addField("birthYear", "birth_year")
                    .addField("gender", "gender")
                    .build();

            LoadDataBulkInsert.Configuration<Trip> config = new LoadDataBulkInsert.Configuration<>(entityInfo);

            BulkInsert<Trip> bulkInsert = new LoadDataBulkInsert<>(config);
            reader.forEachCsvInZip(trips -> {
                try {
                    bulkInsert.insertAll(connection, trips);
                } catch (JFleetException | SQLException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
