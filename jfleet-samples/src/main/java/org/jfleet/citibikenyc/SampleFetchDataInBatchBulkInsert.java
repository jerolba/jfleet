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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.jfleet.BulkInsert;
import org.jfleet.JFleetException;
import org.jfleet.citibikenyc.entities.TripFlatEntity;
import org.jfleet.mysql.LoadDataBulkInsert;
import org.jfleet.util.MySqlTestConnectionProvider;

/*
 * In this example read and parse process are separated from insert process. Firstly reads all content
 * and creates entities in memory and secondly writes it.
 * This requires much more memory than streaming it, but allows to meter % time required for each part.
 *
 */
public class SampleFetchDataInBatchBulkInsert {

    public static void main(String[] args) throws JFleetException, IOException, SQLException {
        Supplier<Connection> connectionSuplier = new MySqlTestConnectionProvider();
        try (Connection connection = connectionSuplier.get()) {
            TableHelper.createTable(connection);

            List<List<TripFlatEntity>> tripsData = new ArrayList<>();
            CitiBikeReader<TripFlatEntity> reader = new CitiBikeReader<>("/tmp", str -> new FlatTripParser(str));
            reader.forEachCsvInZip(trips -> tripsData.add(trips.collect(Collectors.toList())));

            BulkInsert<TripFlatEntity> bulkInsert = new LoadDataBulkInsert<>(TripFlatEntity.class);
            for (List<TripFlatEntity> trip : tripsData) {
                bulkInsert.insertAll(connection, trip);
            }
        }
    }
}
