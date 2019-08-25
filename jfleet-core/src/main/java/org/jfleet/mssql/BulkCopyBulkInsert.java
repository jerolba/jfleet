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
package org.jfleet.mssql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.stream.Stream;

import org.jfleet.BulkInsert;
import org.jfleet.EntityInfo;
import org.jfleet.JFleetException;
import org.jfleet.mssql.BulkInsertConfiguration.BulkInsertConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.sqlserver.jdbc.SQLServerBulkCopy;
import com.microsoft.sqlserver.jdbc.SQLServerBulkCopyOptions;

public class BulkCopyBulkInsert<T> implements BulkInsert<T> {

    private static Logger logger = LoggerFactory.getLogger(BulkCopyBulkInsert.class);

    private final BulkInsertConfiguration cfg;

    public BulkCopyBulkInsert(Class<?> clazz) {
        this(BulkInsertConfigurationBuilder.from(clazz).build());
    }

    public BulkCopyBulkInsert(EntityInfo entityInfo) {
        this(BulkInsertConfigurationBuilder.from(entityInfo).build());
    }

    public BulkCopyBulkInsert(BulkInsertConfiguration bulkInsertConfiguration) {
        this.cfg = bulkInsertConfiguration;
        logger.debug("Batch size: {} rows", cfg.getBatchSize());
    }

    @Override
    public void insertAll(Connection conn, Stream<T> stream) throws JFleetException, SQLException {
        JFleetSQLServerBulkRecord fileRecord = new JFleetSQLServerBulkRecord(cfg.getEntityInfo(), stream);

        SQLServerBulkCopyOptions opt = new SQLServerBulkCopyOptions();
        opt.setBatchSize(cfg.getBatchSize());
        try (SQLServerBulkCopy copy = new SQLServerBulkCopy(conn)) {
            copy.setBulkCopyOptions(opt);
            copy.setDestinationTableName(cfg.getEntityInfo().getTableName());
            copy.writeToServer(fileRecord);
        }

    }

}
