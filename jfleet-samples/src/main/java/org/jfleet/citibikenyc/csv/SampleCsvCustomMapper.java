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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import org.jfleet.EntityFieldType.FieldTypeEnum;
import org.jfleet.EntityInfo;
import org.jfleet.EntityInfoBuilder;
import org.jfleet.citibikenyc.CitiBikeReader;
import org.jfleet.csv.CsvConfiguration;
import org.jfleet.csv.CsvConfiguration.Builder;
import org.jfleet.csv.CsvWriter;

/*
 * This example shows how to write into CSV file customizing how some fields are serialized
 *
 */
public class SampleCsvCustomMapper {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        CitiBikeReader<Trip> reader = new CitiBikeReader<>("/tmp", str -> new TripParser(str));

        CsvConfiguration<Trip> csvConfiguration = new Builder<Trip>(configureEntityMapping()).build();
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

    private static EntityInfo configureEntityMapping() {
        EntityInfoBuilder<Trip> entityInfo = new EntityInfoBuilder<>(Trip.class);
        entityInfo.addField("bikeId");
        entityInfo.addField("tripduration");
        entityInfo.addColumn("startdate", FieldTypeEnum.STRING, trip -> toDate(trip.getStarttime()));
        entityInfo.addField("startStationId");
        entityInfo.addColumn("stopdate", FieldTypeEnum.STRING, trip -> toDate(trip.getStoptime()));
        entityInfo.addField("endStationId");
        entityInfo.addField("userType");
        entityInfo.addColumn("age", FieldTypeEnum.INT, trip -> getAge(trip.getBirthYear()));
        entityInfo.addColumn("gender", FieldTypeEnum.STRING, trip -> getGender(trip.getGender()));
        return entityInfo.build();
    }

    private static SimpleDateFormat TO_DAY = new SimpleDateFormat("yyyy-MM-dd");
    private static int presentYear = LocalDate.now().getYear();

    private static String toDate(Date day) {
        return TO_DAY.format(day);
    }

    private static Integer getAge(Integer birthYear) {
        return birthYear == null ? null : presentYear - birthYear;
    }

    private static String getGender(Character code) {
        switch (code) {
        case '1':
            return "Male";
        case '2':
            return "Female";
        default:
            return null;
        }
    }

}
