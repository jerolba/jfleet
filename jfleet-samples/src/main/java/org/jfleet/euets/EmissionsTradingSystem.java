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
package org.jfleet.euets;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.jfleet.BulkInsert;
import org.jfleet.JFleetException;
import org.jfleet.mysql.LoadDataBulkInsert;
import org.jfleet.mysql.LoadDataBulkInsert.Configuration;
import org.jfleet.util.CsvSplit;
import org.jfleet.util.MySqlTestConnectionProvider;

/*
 * This example shows how to configure the used batch size to a lower number (1MB) and disable the autocommit,
 * forcing to an atomic batch operation.
 * Because the dataset is too small, read it 10 times.
 *
 * Dataset source: https://datahub.io/core/eu-emissions-trading-system/r/eu-ets.csv
 */
public class EmissionsTradingSystem {

    public static void main(String[] args) throws JFleetException, IOException, SQLException {
        MySqlTestConnectionProvider connectionSuplier = new MySqlTestConnectionProvider();
        try (Connection connection = connectionSuplier.get()) {
            createTable(connection);
            List<Stream<EmissionTrade>> emissions = new ArrayList<>();
            Path path = Paths.get("/tmp/eu-ets.csv");
            for (int i = 0; i < 10; i++) {
                emissions.add(Files.lines(path).skip(1).map(EmissionsTradingSystem::parse));
            }

            LoadDataBulkInsert.Configuration<EmissionTrade> config = new Configuration<>(EmissionTrade.class)
                    .batchSize(1024*1024)
                    .autocommit(false);

            BulkInsert<EmissionTrade> bulkInsert = new LoadDataBulkInsert<>(config);
            bulkInsert.insertAll(connection, emissions.stream().flatMap(i -> i));
        }
    }

    private static void createTable(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS emmision_trade");
            stmt.execute(
                    "CREATE TABLE emmision_trade (id INT NOT NULL AUTO_INCREMENT, country_code VARCHAR(3) NOT NULL, "
                            + "country VARCHAR(40) NOT NULL, activity_sector VARCHAR(40) NOT NULL, ets_information VARCHAR(128) NOT NULL, "
                            + "year INT NOT NULL, value DECIMAL(10,2), period VARCHAR(40) NOT NULL, unit VARCHAR(64) NOT NULL, "
                            + "PRIMARY KEY (id))");
        }
    }

    private static EmissionTrade parse(String line) {
        String[] columns = CsvSplit.split(line, 7);
        EmissionTrade et = new EmissionTrade();
        et.setCountry(columns[0]);
        et.setCountryCode(columns[1]);
        et.setActivitySector(columns[2]);
        et.setInformation(columns[3]);
        if (columns[4].startsWith("20")) {
            et.setYear(Integer.parseInt(columns[4]));
        } else {
            et.setPeriod(columns[4]);
        }
        et.setValue(new BigDecimal(columns[5]));
        et.setUnit(columns[6]);
        return et;
    }

}
