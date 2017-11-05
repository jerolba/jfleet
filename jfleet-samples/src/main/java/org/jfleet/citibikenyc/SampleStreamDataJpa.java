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
import java.util.Iterator;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.jfleet.JFleetException;
import org.jfleet.citibikenyc.entities.TripFlatEntity;
import org.jfleet.util.MySqlTestDatasourceProvider;
import org.jfleet.util.jpa.EntityManagerFactoryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * This example read data from stream but persist it using JPA (Hibernate implementation)
 * with batch operations, flushing periodically the entity manager.
 * For optimal performance, the property `hibernate.jdbc.batch_size` should
 * be the same value of the flush and clean size.
 *
 */
public class SampleStreamDataJpa {

    private static int BATCH_SIZE = 200;

    private static Logger LOGGER = LoggerFactory.getLogger(SampleStreamDataJpa.class);

    public static void main(String[] args) throws JFleetException, IOException, SQLException {
        DataSource dataSource = new MySqlTestDatasourceProvider().get();
        EntityManagerFactoryFactory factory = new EntityManagerFactoryFactory(dataSource, TripFlatEntity.class) {
            @Override
            public Properties properties() {
                Properties properties = super.properties();
                properties.put("hibernate.hbm2ddl.auto", "none");
                properties.put("hibernate.jdbc.batch_size", BATCH_SIZE);
                return properties;
            }
        };

        EntityManagerFactory entityManagerFactory = factory.newEntityManagerFactory();
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try (Connection conn = dataSource.getConnection()) {
            TableHelper.createTable(conn);
        }

        CitiBikeReader<TripFlatEntity> reader = new CitiBikeReader<>("/tmp", str -> new FlatTripParser(str));
        entityManager.getTransaction().begin();
        //Batch operations are not allowed with autoinsert id. We need to generate it manually.
        int [] idSeq = new int[] {1};
        reader.forEachCsvInZip(trips -> {
            int cont = 0;
            Iterator<TripFlatEntity> iterator = trips.iterator();
            while(iterator.hasNext()) {
                TripFlatEntity trip = iterator.next();
                trip.setId(idSeq[0]++);
                entityManager.persist(trip);
                cont++;
                if (cont % BATCH_SIZE ==0) {
                    LOGGER.info("Flushing. Total elements {}",cont);
                    entityManager.flush();
                    entityManager.clear();
                }
            }
            entityManager.flush();
            entityManager.clear();
        });
        entityManager.getTransaction().commit();
        entityManager.close();
    }
}
