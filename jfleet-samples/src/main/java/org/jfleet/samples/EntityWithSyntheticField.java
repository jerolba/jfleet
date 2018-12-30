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

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

import org.jfleet.BulkInsert;
import org.jfleet.EntityFieldType.FieldTypeEnum;
import org.jfleet.EntityInfo;
import org.jfleet.EntityInfoBuilder;
import org.jfleet.JFleetException;
import org.jfleet.mysql.LoadDataBulkInsert;
import org.jfleet.samples.entities.Trip;
import org.jfleet.samples.shared.CitiBikeReader;
import org.jfleet.samples.shared.TableHelper;
import org.jfleet.samples.shared.TripParser;
import org.jfleet.util.MySqlTestConnectionProvider;

/*
 * This example shows how to configure JFleet to use a class without annotations, creating synthetic
 * column calculated in persistence time and which is not located in the class as a getter or transient field.
 * The new column uses a function which calculates the distance between two coordinates.
 */
public class EntityWithSyntheticField {

    public static void main(String[] args) throws IOException, SQLException {
        Supplier<Connection> connectionSuplier = new MySqlTestConnectionProvider();
        try (Connection connection = connectionSuplier.get()) {
            TableHelper.createTableDistance(connection);
            CitiBikeReader<Trip> reader = new CitiBikeReader<>("/tmp", str -> new TripParser(str));

            EntityInfo entityInfo = new EntityInfoBuilder<>(Trip.class, "bike_trip_distance").addField("id", "id", true)
                    .addField("tripDuration", "tripduration").addField("bikeId", "bike_id")
                    .addField("userType", "user_type").addField("birthYear", "birth_year").addField("gender", "gender")
                    .addColumn("distance", FieldTypeEnum.DOUBLE,
                            trip -> distance(trip.getStartStation().getLatitude(),
                                    trip.getStartStation().getLongitude(), trip.getEndStation().getLatitude(),
                                    trip.getEndStation().getLongitude()))
                    .build();

            BulkInsert<Trip> bulkInsert = new LoadDataBulkInsert<>(entityInfo);
            reader.forEachCsvInZip(trips -> {
                try {
                    bulkInsert.insertAll(connection, trips);
                } catch (JFleetException | SQLException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static Double distance(double lat1, double lon1, double lat2, double lon2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1))
        * Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        distance = Math.pow(distance, 2);

        double result = Math.sqrt(distance);
        if (Math.abs(result) < 0.0001) {
            return null;
        }
        return result;
    }
}
