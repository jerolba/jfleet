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
