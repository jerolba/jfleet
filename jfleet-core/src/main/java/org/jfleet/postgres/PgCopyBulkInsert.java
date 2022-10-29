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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.stream.Stream;

import org.jfleet.BulkInsert;
import org.jfleet.EntityInfo;
import org.jfleet.JFleetException;
import org.jfleet.WrappedException;
import org.jfleet.common.ContentWriter;
import org.jfleet.common.LoopAndWrite;
import org.jfleet.common.TransactionPolicy;
import org.jfleet.postgres.PgCopyConfiguration.PgCopyConfigurationBuilder;
import org.postgresql.copy.CopyManager;
import org.postgresql.jdbc.PgConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PgCopyBulkInsert<T> implements BulkInsert<T> {

    private static Logger logger = LoggerFactory.getLogger(PgCopyBulkInsert.class);

    private final PgCopyConfiguration cfg;
    private final String mainSql;

    public PgCopyBulkInsert(Class<T> clazz) {
        this(PgCopyConfigurationBuilder.from(clazz).build());
    }

    public PgCopyBulkInsert(EntityInfo entityInfo) {
        this(PgCopyConfigurationBuilder.from(entityInfo).build());
    }

    public PgCopyBulkInsert(PgCopyConfiguration config) {
        this.cfg = config;
        this.mainSql = new SqlBuilder(config.getEntityInfo()).build();
        logger.debug("SQL Insert for {}: {}", config.getEntityInfo().getEntityClass().getName(), mainSql);
        logger.debug("Batch size: {} bytes", config.getBatchSize());
    }

    @Override
    public void insertAll(Connection conn, Stream<T> stream) throws JFleetException, SQLException {
        PgCopyRowBuilder rowBuilder = new PgCopyRowBuilder(cfg.getEntityInfo());
        CopyManager copyMng = getCopyManager(conn);
        try {
            TransactionPolicy txPolicy = TransactionPolicy.getTransactionPolicy(conn, cfg.isAutocommit());
            ContentWriter contentWriter = new PgCopyContentWriter(txPolicy, copyMng, mainSql);
            try {
                LoopAndWrite loopAndWrite = new LoopAndWrite(cfg, contentWriter, rowBuilder);
                loopAndWrite.go(stream);
            } finally {
                txPolicy.close();
            }
        } catch (WrappedException e) {
            e.rethrow();
        }
    }

    private CopyManager getCopyManager(Connection conn) throws SQLException {
        PgConnection unwrapped = conn.unwrap(PgConnection.class);
        return unwrapped.getCopyAPI();
    }

}
