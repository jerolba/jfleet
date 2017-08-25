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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class CitiBikeReader {

    private final File datasetDir;
    private final Predicate<File> isCitibikeFile = f -> f.getName().contains("citibike-tripdata");

    public CitiBikeReader(String path) {
        datasetDir = new File(path);
    }

    public void forEachCsvInZip(Consumer<Stream<TripEntity>> consumer) {
        Stream.of(datasetDir.listFiles()).filter(f -> f.getName().endsWith(".zip")).filter(isCitibikeFile)
                .forEach(f -> {
                    try (ZipInputStream zis = new ZipInputStream(new FileInputStream(f))) {
                        ZipEntry ze = zis.getNextEntry();
                        while (ze != null) {
                            StreamCounter<TripEntity> counter = new StreamCounter<>();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(zis));
                            Stream<TripEntity> trips = reader.lines().skip(1).map(TripParser::new)
                                    .map(TripParser::parse).map(counter::count);

                            consumer.accept(trips);

                            counter.close();
                            ze = zis.getNextEntry();
                        }
                        zis.closeEntry();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    public void forEachCsv(Consumer<Stream<TripEntity>> consumer) {
        Stream.of(datasetDir.listFiles()).filter(f -> f.getName().endsWith(".csv")).filter(isCitibikeFile)
                .forEach(f -> {
                    try (StreamCounter<TripEntity> counter = new StreamCounter<>()) {
                        Stream<TripEntity> trips = Files.lines(Paths.get(f.getAbsolutePath())).skip(1)
                                .map(TripParser::new).map(TripParser::parse).map(counter::count);
                        consumer.accept(trips);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }
}
