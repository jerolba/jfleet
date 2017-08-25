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

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.stream.Stream;

import org.jfleet.BulkInsert;
import org.jfleet.EntityInfo;
import org.jfleet.JpaEntityInspector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysql.jdbc.Statement;

public class LoadDataBulkInsert<T> implements BulkInsert<T> {

    private static Logger logger = LoggerFactory.getLogger(LoadDataBulkInsert.class);

    private final EntityInfo entityInfo;
    private final String mainSql;
    private final Charset encoding = Charset.forName("UTF-8");
    private final long BATCH_SIZE = 50 * 1_024 * 1_024;

    public LoadDataBulkInsert(Class<T> clazz) {
        JpaEntityInspector inspector = new JpaEntityInspector(clazz);
        this.entityInfo = inspector.inspect();

        SqlBuilder sqlBuiler = new SqlBuilder(entityInfo);
        mainSql = sqlBuiler.build();
        logger.debug("SQL Insert for {}: {}", entityInfo.getEntityClass().getName(), mainSql);
        logger.debug("Batch size: {} bytes", BATCH_SIZE);
    }

    @Override
    public void insertAll(Connection conn, Stream<T> stream) throws SQLException {
        FileContentBuilder contentBuilder = new FileContentBuilder(entityInfo);
        try (Statement stmt = getStatementForLoadLocal(conn)) {
            stream.forEach(element -> {
                contentBuilder.add(element);
                if (contentBuilder.getContentSize() > BATCH_SIZE) {
                    logger.debug("Writing content");
                    writeContent(stmt, contentBuilder);
                }
            });
            logger.debug("Flushing content");
            writeContent(stmt, contentBuilder);
        } catch (WrappedSQLException e) {
            throw e.getSQLException();
        }
    }

    private void writeContent(Statement stmt, FileContentBuilder contentBuilder) {
        if (contentBuilder.getContentSize() > 0) {
            try {
                long init = System.nanoTime();
                String content = contentBuilder.getContent();
                stmt.setLocalInfileInputStream(new ByteArrayInputStream(content.getBytes(encoding)));
                stmt.execute(mainSql);
                logger.debug("{} ms writing {} bytes for {} records", (System.nanoTime() - init) / 1_000_000,
                        contentBuilder.getContentSize(), contentBuilder.getRecords());
                contentBuilder.reset();
            } catch (SQLException e) {
                throw new WrappedSQLException(e);
            }
        }
    }

    private Statement getStatementForLoadLocal(Connection conn) throws SQLException {
        com.mysql.jdbc.Connection unwrapped = null;
        try {
            unwrapped = conn.unwrap(com.mysql.jdbc.Connection.class);
            unwrapped.setAllowLoadLocalInfile(true);
        } catch (SQLException e) {
            throw new RuntimeException("Incorrect Connection type. Expected com.mysql.jdbc.Connection");
        }
        return (Statement) unwrapped.createStatement();
    }

    private static class WrappedSQLException extends RuntimeException {

        private static final long serialVersionUID = 5687915465143634780L;

        WrappedSQLException(SQLException e) {
            super(e);
        }

        public SQLException getSQLException() {
            return (SQLException) this.getCause();
        }
    }

}
