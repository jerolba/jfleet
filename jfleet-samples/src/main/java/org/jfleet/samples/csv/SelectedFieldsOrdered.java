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
package org.jfleet.samples.csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jfleet.EntityInfoBuilder;
import org.jfleet.csv.CsvConfiguration;
import org.jfleet.csv.CsvConfiguration.Builder;
import org.jfleet.samples.shared.CitiBikeReader;
import org.jfleet.csv.CsvWriter;

/*
 * This example shows how to write into CSV file selecting which fields and in which order appear
 *
 */
public class SelectedFieldsOrdered {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        CitiBikeReader<Trip> reader = new CitiBikeReader<>("/tmp", str -> new TripParser(str));

        EntityInfoBuilder<Trip> entityInfo = new EntityInfoBuilder<>(Trip.class);
        entityInfo.addFields("bikeId", "tripduration", "starttime", "startStationId", "stoptime", "endStationId",
                "userType", "birthYear", "gender");
        CsvConfiguration<Trip> csvConfiguration = new Builder<Trip>(entityInfo.build()).build();
        CsvWriter<Trip> csvWriter = new CsvWriter<>(csvConfiguration);
        try (FileOutputStream fos = new FileOutputStream(new File("/tmp/trips.csv"))) {
            reader.forEachCsvInZip(trips -> {
                try {
                    csvWriter.writeAll(fos, trips);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

}
