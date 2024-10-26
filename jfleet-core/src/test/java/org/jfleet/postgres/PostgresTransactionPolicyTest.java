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
package org.jfleet.postgres;

import static org.jfleet.parameterized.Databases.Postgres;
import static org.jfleet.postgres.PgCopyConfiguration.PgCopyConfigurationBuilder.from;
import static org.jfleet.util.TransactionPolicyTestHelper.employeesWithConstraintError;
import static org.jfleet.util.TransactionPolicyTestHelper.employeesWithOutErrors;
import static org.jfleet.util.TransactionPolicyTestHelper.numberOfRowsInEmployeeTable;
import static org.jfleet.util.TransactionPolicyTestHelper.setupDatabase;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.jfleet.BulkInsert;
import org.jfleet.entities.Employee;
import org.jfleet.parameterized.DatabaseProvider;
import org.jfleet.util.Database;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostgresTransactionPolicyTest {

    private static final Logger logger = LoggerFactory.getLogger(PostgresTransactionPolicyTest.class);
    private static final int VERY_LOW_SIZE_TO_FREQUENT_LOAD_DATA = 10;

    private final Database database = DatabaseProvider.getDatabase(Postgres);

    @BeforeEach
    public void setup() throws SQLException, IOException {
        try (Connection connection = database.getConnection()) {
            setupDatabase(connection);
        }
    }

    @Test
    public void longTransactionExecuteMultipleLoadDataOperationsTransactionaly() throws Exception {
        try (Connection connection = database.getConnection()) {
            connection.setAutoCommit(false);

            PgCopyConfiguration config = from(Employee.class)
                    .batchSize(VERY_LOW_SIZE_TO_FREQUENT_LOAD_DATA)
                    .autocommit(false)
                    .build();
            BulkInsert<Employee> bulkInsert = new PgCopyBulkInsert<>(config);

            bulkInsert.insertAll(connection, employeesWithOutErrors());

            // We don't know how many load data operations were executed, but with
            // low batch size, multiple load data are executed with few records.
            assertEquals(7, numberOfRowsInEmployeeTable(connection));
            connection.rollback();
            assertEquals(0, numberOfRowsInEmployeeTable(connection));
        }
    }

    @Test
    public void longTransactionWithConstraintExceptionIsRollbacked() throws Exception {
        try (Connection connection = database.getConnection()) {
            connection.setAutoCommit(false);

            PgCopyConfiguration config = from(Employee.class)
                    .batchSize(VERY_LOW_SIZE_TO_FREQUENT_LOAD_DATA)
                    .autocommit(false)
                    .build();
            BulkInsert<Employee> bulkInsert = new PgCopyBulkInsert<>(config);

            try {
                bulkInsert.insertAll(connection, employeesWithConstraintError());
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                logger.info("Expected error on missed FK");
                assertEquals(0, numberOfRowsInEmployeeTable(connection));
                return;
            }
            assertTrue(false, "Expected JFleetException exception");
        }
    }

    @Test
    public void multipleBatchOperationsExecuteMultipleLoadDataOperationsWithHisOwnTransaction() throws Exception {
        try (Connection connection = database.getConnection()) {
            PgCopyConfiguration config = from(Employee.class)
                    .batchSize(VERY_LOW_SIZE_TO_FREQUENT_LOAD_DATA)
                    .autocommit(true)
                    .build();
            BulkInsert<Employee> bulkInsert = new PgCopyBulkInsert<>(config);

            try {
                bulkInsert.insertAll(connection, employeesWithConstraintError());
            } catch (SQLException e) {
                logger.info("Expected error on missed FK");
                assertTrue(numberOfRowsInEmployeeTable(connection) > 0);
                return;
            }
            assertTrue(false, "Expected JFleetException exception");
        }
    }

}
