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
import java.util.stream.Stream;

import org.jfleet.BulkInsert;
import org.jfleet.JFleetException;
import org.jfleet.mysql.LoadDataBulkInsert;
import org.jfleet.samples.entities.TripFlatEntity;
import org.jfleet.samples.shared.CitiBikeReader;
import org.jfleet.samples.shared.FlatTripParser;
import org.jfleet.samples.shared.TableHelper;
import org.jfleet.util.MySqlTestConnectionProvider;

/*
 * This example inserts an stream of entities. This allows us to persist all information without keeping
 * all entities in memory and generate it lazily.
 *
 */
public class PersistStream {

    public static void main(String[] args) throws IOException, SQLException {
        Supplier<Connection> connectionSuplier = new MySqlTestConnectionProvider();
        try (Connection connection = connectionSuplier.get()) {
            TableHelper.createTable(connection);

            CitiBikeReader<TripFlatEntity> reader = new CitiBikeReader<>("/tmp", str -> new FlatTripParser(str));
            BulkInsert<TripFlatEntity> bulkInsert = new LoadDataBulkInsert<>(TripFlatEntity.class);
            reader.forEachCsvInZip((Stream<TripFlatEntity> trips) -> {
                try {
                    bulkInsert.insertAll(connection, trips);
                } catch (JFleetException | SQLException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
