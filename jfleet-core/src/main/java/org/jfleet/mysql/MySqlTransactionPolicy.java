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
package org.jfleet.mysql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import org.jfleet.JFleetException;

public interface MySqlTransactionPolicy {

    static MySqlTransactionPolicy getTransactionPolicy(Connection connection, boolean longTransaction,
            boolean errorOnMissingRow) throws SQLException {
        if (longTransaction) {
            return new LongTransaction(errorOnMissingRow);
        }
        return new BatchTransaction(connection, errorOnMissingRow);
    }

    void commit(int processed, Optional<Long> updatedInDB) throws SQLException, JFleetException;

    void close() throws SQLException;

    class LongTransaction implements MySqlTransactionPolicy {

        private boolean errorOnMissingRow;

        public LongTransaction(boolean errorOnMissingRow) {
            this.errorOnMissingRow = errorOnMissingRow;
        }

        @Override
        public void commit(int processed, Optional<Long> updatedInDB) throws SQLException, JFleetException {
            if (errorOnMissingRow && updatedInDB.isPresent()) {
                if (processed != updatedInDB.get()) {
                    throw new JFleetException(
                            "Missed row, processed: " + processed + ", expected: " + updatedInDB.get());
                }
            }
        }

        @Override
        public void close() throws SQLException {

        }

    }

    class BatchTransaction implements MySqlTransactionPolicy {

        private final Connection connection;
        private final boolean autocommit;
        private boolean errorOnMissingRow;

        public BatchTransaction(Connection connection, boolean errorOnMissingRow) throws SQLException {
            this.connection = connection;
            this.errorOnMissingRow = errorOnMissingRow;
            this.autocommit = connection.getAutoCommit();
            this.connection.setAutoCommit(false);
        }

        @Override
        public void commit(int processed, Optional<Long> updatedInDB) throws SQLException, JFleetException {
            if (errorOnMissingRow && updatedInDB.isPresent()) {
                if (processed != updatedInDB.get()) {
                    this.connection.rollback();
                    throw new JFleetException(
                            "Missed row, processed: " + processed + ", expected: " + updatedInDB.get());
                }
            }
            this.connection.commit();
        }

        @Override
        public void close() throws SQLException {
            this.connection.setAutoCommit(autocommit);
        }

    }

}
