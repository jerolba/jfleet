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
package org.jfleet.postgres;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.stream.Stream;

import org.jfleet.BulkInsert;
import org.jfleet.EntityInfo;
import org.jfleet.JFleetException;
import org.jfleet.JpaEntityInspector;
import org.jfleet.WrappedException;
import org.postgresql.copy.CopyManager;
import org.postgresql.jdbc.PgConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PgCopyBulkInsert<T> implements BulkInsert<T> {

    private static Logger logger = LoggerFactory.getLogger(PgCopyBulkInsert.class);

    private final EntityInfo entityInfo;
    private final String mainSql;
    private final long BATCH_SIZE = 50 * 1_024 * 1_024;

    public PgCopyBulkInsert(Class<T> clazz) {
        JpaEntityInspector inspector = new JpaEntityInspector(clazz);
        this.entityInfo = inspector.inspect();

        SqlBuilder sqlBuiler = new SqlBuilder(entityInfo);
        mainSql = sqlBuiler.build();
        logger.debug("SQL Insert for {}: {}", entityInfo.getEntityClass().getName(), mainSql);
        logger.debug("Batch size: {} bytes", BATCH_SIZE);
    }

    @Override
    public void insertAll(Connection conn, Stream<T> stream) throws JFleetException {
        StdInContentBuilder contentBuilder = new StdInContentBuilder(entityInfo);
        CopyManager copyMng = getCopyManager(conn);
        try {
            stream.forEach(element -> {
                contentBuilder.add(element);
                if (contentBuilder.getContentSize() > BATCH_SIZE) {
                    logger.debug("Writing content");
                    writeContent(copyMng, contentBuilder);
                }
            });
            logger.debug("Flushing content");
            writeContent(copyMng, contentBuilder);
        } catch (WrappedException e) {
            e.rethrow();
        }
    }

    private void writeContent(CopyManager copyManager, StdInContentBuilder contentBuilder) {
        if (contentBuilder.getContentSize() > 0) {
            try {
                long init = System.nanoTime();
                Reader reader = new StringBuilderReader(contentBuilder.getContent());
                copyManager.copyIn(mainSql, reader);
                logger.debug("{} ms writing {} bytes for {} records", (System.nanoTime() - init) / 1_000_000,
                        contentBuilder.getContentSize(), contentBuilder.getRecords());
                contentBuilder.reset();
            } catch (SQLException | IOException e) {
                throw new WrappedException(e);
            }
        }
    }

    private CopyManager getCopyManager(Connection conn) throws JFleetException {
        try {
            PgConnection unwrapped = conn.unwrap(PgConnection.class);
            return unwrapped.getCopyAPI();
        } catch (SQLException e) {
            throw new JFleetException(e);
        }
    }

}
