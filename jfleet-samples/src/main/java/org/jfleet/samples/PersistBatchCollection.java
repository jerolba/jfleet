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

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Supplier;

import org.jfleet.BulkInsert;
import org.jfleet.mysql.LoadDataBulkInsert;
import org.jfleet.samples.entities.TripFlatEntity;
import org.jfleet.samples.shared.CitiBikeReader;
import org.jfleet.samples.shared.FlatTripParser;
import org.jfleet.samples.shared.TableHelper;
import org.jfleet.util.MySqlTestConnectionProvider;

/*
 * In this example, read and parse process are separated from insert process. Firstly reads all content
 * and creates entities in memory and secondly writes it.
 * This requires much more memory than streaming it, but allows to meter % time required for each part.
 *
 */
public class PersistBatchCollection {

    public static void main(String[] args) throws IOException, SQLException {
        Supplier<Connection> connectionSuplier = new MySqlTestConnectionProvider();
        try (Connection connection = connectionSuplier.get()) {
            TableHelper.createTable(connection);

            CitiBikeReader<TripFlatEntity> reader = new CitiBikeReader<>("/tmp", str -> new FlatTripParser(str));
            BulkInsert<TripFlatEntity> bulkInsert = new LoadDataBulkInsert<>(TripFlatEntity.class);
            reader.forEachCsvInZip(tripsStream -> {
                try {
                    List<TripFlatEntity> trips = tripsStream.collect(toList());
                    bulkInsert.insertAll(connection, trips);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
