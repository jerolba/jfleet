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
import org.jfleet.JFleetException;
import org.jfleet.mysql.LoadDataBulkInsert;
import org.jfleet.samples.entities.StationEmbedded;
import org.jfleet.samples.entities.TripEntity;
import org.jfleet.samples.shared.CSVParser;
import org.jfleet.samples.shared.CitiBikeReader;
import org.jfleet.samples.shared.TableHelper;
import org.jfleet.util.MySqlTestConnectionProvider;

/*
 * This sample persists information from an entity with two embedded objects,
 * showing how it works with @Embedded and @AttributeOverrides anotations
 *
 */
public class FromClassWithEmbeddedAnnotation {

    public static void main(String[] args) throws IOException, SQLException {
        Supplier<Connection> connectionSuplier = new MySqlTestConnectionProvider();
        try (Connection connection = connectionSuplier.get()) {
            TableHelper.createTable(connection);
            CitiBikeReader<TripEntity> reader = new CitiBikeReader<>("/tmp", str -> new TripEntityParser(str));
            BulkInsert<TripEntity> bulkInsert = new LoadDataBulkInsert<>(TripEntity.class);
            reader.forEachCsvInZip(trips -> {
                try {
                    bulkInsert.insertAll(connection, trips);
                } catch (JFleetException | SQLException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static class TripEntityParser extends CSVParser<TripEntity> {

        public TripEntityParser(String line) {
            super(line, 15);
        }

        @Override
        public TripEntity parse() {
            TripEntity trip = new TripEntity();
            trip.setTripduration(nextInteger());
            trip.setStarttime(nextDate());
            trip.setStoptime(nextDate());
            StationEmbedded startStation = new StationEmbedded();
            startStation.setStationId(nextInteger());
            startStation.setStationName(nextString());
            startStation.setStationLatitude(nextDouble());
            startStation.setStationLongitude(nextDouble());
            trip.setStartStation(startStation);
            StationEmbedded endStation = new StationEmbedded();
            endStation.setStationId(nextInteger());
            endStation.setStationName(nextString());
            endStation.setStationLatitude(nextDouble());
            endStation.setStationLongitude(nextDouble());
            trip.setEndStation(endStation);
            trip.setBikeId(nextLong());
            trip.setUserType(nextString());
            trip.setBirthYear(nextInteger());
            trip.setGender(nextChar());
            return trip;
        }

    }
}
