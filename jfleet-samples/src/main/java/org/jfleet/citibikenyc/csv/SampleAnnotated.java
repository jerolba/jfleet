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
package org.jfleet.citibikenyc.csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jfleet.citibikenyc.CSVParser;
import org.jfleet.citibikenyc.CitiBikeReader;
import org.jfleet.csv.CsvConfiguration;
import org.jfleet.csv.CsvWriter;

/*
 * This example shows how to write a CSV file from objects of a class with JPA annotations
 *
 */
public class SampleAnnotated {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        CitiBikeReader<TripAnnotated> reader = new CitiBikeReader<>("/tmp", str -> new TripAnnotatedParser(str));

        CsvConfiguration<TripAnnotated> config = new CsvConfiguration.Builder<>(TripAnnotated.class).build();

        CsvWriter<TripAnnotated> csvWriter = new CsvWriter<>(config);
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

    public static class TripAnnotatedParser extends CSVParser<TripAnnotated> {

        public TripAnnotatedParser(String line) {
            super(line, 15);
        }

        @Override
        public TripAnnotated parse() {
            TripAnnotated trip = new TripAnnotated();
            trip.setTripduration(nextInteger());
            trip.setStarttime(nextDate());
            trip.setStoptime(nextDate());
            trip.setStartStationId(nextInteger());
            trip.setStartStationName(nextString());
            trip.setStartStationLatitude(nextDouble());
            trip.setStartStationLongitude(nextDouble());
            trip.setEndStationId(nextInteger());
            trip.setEndStationName(nextString());
            trip.setEndStationLatitude(nextDouble());
            trip.setEndStationLongitude(nextDouble());
            trip.setBikeId(nextLong());
            trip.setUserType(nextString());
            trip.setBirthYear(nextInteger());
            trip.setGender(nextChar());
            return trip;
        }

    }
}
