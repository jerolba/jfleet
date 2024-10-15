/**
 * Copyright 2022 Jerónimo López Bezanilla
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
package org.jfleet.mysql.error;

import org.jfleet.BulkInsert;
import org.jfleet.JFleetException;
import org.jfleet.entities.City;
import org.jfleet.entities.Employee;
import org.jfleet.mysql.LoadDataBulkInsert;
import org.jfleet.mysql.LoadDataConfiguration;
import org.jfleet.parameterized.DatabaseArgumentProvider;
import org.jfleet.util.MySqlDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.stream.Stream;

import static org.jfleet.mysql.LoadDataConfiguration.LoadDataConfigurationBuilder.from;
import static org.jfleet.parameterized.Databases.MySql;
import static org.jfleet.util.TransactionPolicyTestHelper.numberOfRowsInEmployeeTable;
import static org.jfleet.util.TransactionPolicyTestHelper.setupDatabase;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LockRetryTest {

    private static Logger logger = LoggerFactory.getLogger(LockRetryTest.class);

    private final MySqlDatabase database = (MySqlDatabase) DatabaseArgumentProvider.getDatabaseContainer(MySql);

    private static City city1 = new City(1, "Madrid");
    private static City city2 = new City(2, "Barcelona");

    @BeforeEach
    public void setup() throws SQLException, IOException {
        try (Connection connection = database.getConnection()) {
            setupDatabase(connection);
        }
    }

    @Test
    public void lockTableRetryUntilUnlock() throws Exception {
        try (Connection connectionLocker = database.getConnection()) {
            lockTableForInsert(connectionLocker, 5000);
            try (Connection connection = database.getConnection()) {
                connection.setAutoCommit(false);
                connection.createStatement().execute("SET innodb_lock_wait_timeout = 2");
                LoadDataConfiguration config = from(Employee.class)
                        .writerWrapper(writer -> new LockTimeoutErrorManager(writer, 3)).build();

                BulkInsert<Employee> bulkInsert = new LoadDataBulkInsert<>(config);
                long init = System.nanoTime();
                bulkInsert.insertAll(connection, moreEmployees());
                long elapsedTime = (System.nanoTime() - init) / 1_000_000;
                assertTrue(elapsedTime > 4000);
                assertTrue(elapsedTime < 5000);
                assertEquals(14, numberOfRowsInEmployeeTable(connection));
            }
        }
    }

    @Test
    public void lockTableRetryFinishInIteratoins() throws Exception {
        int lockMilisecond = 5000;
        try (Connection connectionLocker = database.getConnection()) {
            lockTableForInsert(connectionLocker, lockMilisecond);
            try (Connection connection = database.getConnection()) {
                connection.setAutoCommit(false);
                connection.createStatement().execute("SET innodb_lock_wait_timeout = 1");
                LoadDataConfiguration config = from(Employee.class)
                        .writerWrapper(writer -> new LockTimeoutErrorManager(writer, 2)).build();

                BulkInsert<Employee> bulkInsert = new LoadDataBulkInsert<>(config);
                long init = System.nanoTime();
                bulkInsert.insertAll(connection, moreEmployees());
                long elapsedTime = (System.nanoTime() - init) / 1_000_000;
                assertTrue(elapsedTime < lockMilisecond);
                assertEquals(7, numberOfRowsInEmployeeTable(connection));
                Thread.sleep(lockMilisecond - elapsedTime);
            }
        }
    }

    private void lockTableForInsert(Connection connection, int lockMilisecond) {
        new Thread(() -> {
            try {
                connection.setAutoCommit(false);
                LoadDataConfiguration config = from(Employee.class).build();
                BulkInsert<Employee> bulkInsert = new LoadDataBulkInsert<>(config);
                bulkInsert.insertAll(connection, someEmployees());
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM employee FOR UPDATE");
                ps.execute();
                logger.debug("Table locked for {} miliseconds", lockMilisecond);
                Thread.sleep(lockMilisecond);
                connection.commit();
                logger.debug("Table unlocked");
            } catch (SQLException | JFleetException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
        // Avoid that main thread start before this
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static Stream<Employee> someEmployees() {
        return Stream.of(
                new Employee(1, "John", city1),
                new Employee(2, "Charles", city2),
                new Employee(3, "Peter", city1),
                new Employee(4, "John", city2),
                new Employee(5, "Donald", city1),
                new Employee(6, "Alex", city1),
                new Employee(7, "David", city1));
    }

    private static Stream<Employee> moreEmployees() {
        return Stream.of(
                new Employee(8, "Albert", city1),
                new Employee(9, "Bernard", city2),
                new Employee(10, "Caroline", city1),
                new Employee(11, "David", city2),
                new Employee(12, "Eva", city1),
                new Employee(13, "Frank", city2),
                new Employee(14, "Glenn", city1));
    }
}
