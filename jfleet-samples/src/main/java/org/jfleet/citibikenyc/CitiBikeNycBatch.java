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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jfleet.BulkInsert;
import org.jfleet.JFleetException;
import org.jfleet.mysql.LoadDataBulkInsert;
import org.jfleet.util.MySqlTestConnectionProvider;

/*
 * This example works with the dataset provided by Citi Bike NYC about each trip with their bikes.
 * The dataset can be downloaded from: https://s3.amazonaws.com/tripdata/index.html
 *
 * Read and parse all files located in /tmp directory and each CSV file is inserted into the database.
 * The read and parse process are separated from insert one, which requires much more memory than streaming it.
 *
 */
public class CitiBikeNycBatch {

	public static void main(String[] args) throws JFleetException, IOException, SQLException {
		MySqlTestConnectionProvider connectionSuplier = new MySqlTestConnectionProvider();
		Connection connection = connectionSuplier.get();

		try (Statement stmt = connection.createStatement()) {
			stmt.execute("DROP TABLE IF EXISTS bike_trip");
			stmt.execute("CREATE TABLE bike_trip (id INT NOT NULL AUTO_INCREMENT, tripduration INT NOT NULL, starttime DATETIME, stoptime DATETIME, start_station_id INT NOT NULL, start_station_name VARCHAR(255), start_station_latitude DOUBLE NOT NULL, start_station_longitude DOUBLE NOT NULL, end_station_id INT NOT NULL, end_station_name VARCHAR(255), end_station_latitude DOUBLE NOT NULL, end_station_longitude DOUBLE NOT NULL, bike_id BIGINT NOT NULL, user_type VARCHAR(255), birth_year INT, gender CHAR, PRIMARY KEY (id))");
		}
		List<List<TripEntity>> tripsData = new ArrayList<>();
		CitiBikeReader reader = new CitiBikeReader("/tmp");
        reader.forEachCsvInZip(trips -> tripsData.add(trips.collect(Collectors.toList())));

        BulkInsert<TripEntity> bulkInsert = new LoadDataBulkInsert<>(TripEntity.class);
        for(List<TripEntity> trip: tripsData) {
            bulkInsert.insertAll(connection, trip);
        }
	}
}
