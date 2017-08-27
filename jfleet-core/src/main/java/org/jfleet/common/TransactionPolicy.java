package org.jfleet.common;

import java.sql.Connection;
import java.sql.SQLException;

public interface TransactionPolicy {

    public static TransactionPolicy getTransactionPolicy(Connection connection, boolean longTransaction)
            throws SQLException {
        if (longTransaction) {
            return new LongTransaction();
        }
        return new BatchTransaction(connection);
    }

    public default void commit() throws SQLException {
    }

    public default void close() throws SQLException {

    }

    public static class LongTransaction implements TransactionPolicy {

    }

    public static class BatchTransaction implements TransactionPolicy {

        private final Connection connection;
        private final boolean autocommit;

        public BatchTransaction(Connection connection) throws SQLException {
            this.connection = connection;
            this.autocommit = connection.getAutoCommit();
            this.connection.setAutoCommit(false);
        }

        @Override
        public void commit() throws SQLException {
            this.connection.commit();
        }

        @Override
        public void close() throws SQLException {
            this.connection.setAutoCommit(autocommit);
        }

    }

}
