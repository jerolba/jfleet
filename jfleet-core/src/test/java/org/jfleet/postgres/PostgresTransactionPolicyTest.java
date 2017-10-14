package org.jfleet.postgres;

import static org.jfleet.util.TransactionPolicyTestHelper.employeesWithConstraintError;
import static org.jfleet.util.TransactionPolicyTestHelper.employeesWithOutErrors;
import static org.jfleet.util.TransactionPolicyTestHelper.numberOfRowsInEmployeeTable;
import static org.jfleet.util.TransactionPolicyTestHelper.setupDatabase;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

import org.jfleet.BulkInsert;
import org.jfleet.JFleetException;
import org.jfleet.entities.Employee;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostgresTransactionPolicyTest {

    private static Logger logger = LoggerFactory.getLogger(PostgresTransactionPolicyTest.class);
    private static final long VERY_LOW_SIZE_TO_FREQUENT_LOAD_DATA = 10;

    @Test
    public void longTransactionExecuteMultipleLoadDataOperationsTransactionaly() throws IOException, SQLException, JFleetException {
        Supplier<Connection> provider = new PostgresTestConnectionProvider();
        try (Connection connection = provider.get()) {
            setupDatabase(connection);
            connection.setAutoCommit(false);

            BulkInsert<Employee> bulkInsert = new PgCopyBulkInsert<>(Employee.class,
                    VERY_LOW_SIZE_TO_FREQUENT_LOAD_DATA, true);

            bulkInsert.insertAll(connection, employeesWithOutErrors());

            //We don't know how many load data operations were executed, but with
            //low batch size, multiple load data are executed with few records.
            assertEquals(7, numberOfRowsInEmployeeTable(connection));
            connection.rollback();
            assertEquals(0, numberOfRowsInEmployeeTable(connection));
        }
    }

    @Test
    public void longTransactionWithConstraintExceptionIsRollbacked() throws IOException, SQLException {
        Supplier<Connection> provider = new PostgresTestConnectionProvider();
        try (Connection connection = provider.get()) {
            setupDatabase(connection);
            connection.setAutoCommit(false);

            BulkInsert<Employee> bulkInsert = new PgCopyBulkInsert<>(Employee.class,
                    VERY_LOW_SIZE_TO_FREQUENT_LOAD_DATA, true);

            try {
                bulkInsert.insertAll(connection, employeesWithConstraintError());
                connection.commit();
            } catch (JFleetException e) {
                logger.info("Expected error on missed FK");
                connection.close();
            }
            assertEquals(0, numberOfRowsInEmployeeTable(provider.get()));
        }
    }

    @Test
    public void multipleBatchOperationsExecuteMultipleLoadDataOperationsWithHisOwnTransaction() throws IOException, SQLException {
        Supplier<Connection> provider = new PostgresTestConnectionProvider();
        try (Connection connection = provider.get()) {
            setupDatabase(connection);

            BulkInsert<Employee> bulkInsert = new PgCopyBulkInsert<>(Employee.class,
                    VERY_LOW_SIZE_TO_FREQUENT_LOAD_DATA, false);

            try {
                bulkInsert.insertAll(connection, employeesWithConstraintError());
            } catch(JFleetException e) {
                logger.info("Expected error on missed FK");
            }
            assertTrue(numberOfRowsInEmployeeTable(connection)>0);
        }
    }

}
