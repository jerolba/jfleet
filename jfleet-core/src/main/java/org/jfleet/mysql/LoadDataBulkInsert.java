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

import static org.jfleet.mysql.MySqlTransactionPolicy.getTransactionPolicy;

import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.stream.Stream;

import org.jfleet.BulkInsert;
import org.jfleet.EntityInfo;
import org.jfleet.JFleetException;
import org.jfleet.common.ContentWriter;
import org.jfleet.common.ParallelContentWriter;
import org.jfleet.inspection.JpaEntityInspector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysql.jdbc.Statement;

public class LoadDataBulkInsert<T> implements BulkInsert<T> {

    private static Logger logger = LoggerFactory.getLogger(LoadDataBulkInsert.class);
    private final static int DEFAULT_BATCH_SIZE = 10 * 1_024 * 1_024;

    private final EntityInfo entityInfo;
    private final String mainSql;
    private final int batchSize;
    private final boolean autocommit;
    private final boolean concurrent;
    private final boolean errorOnMissingRow;
    private final Charset encoding;

    public LoadDataBulkInsert(Class<T> clazz) {
        this(new Configuration<>(clazz));
    }

    public LoadDataBulkInsert(Configuration<T> config) {
        EntityInfo entityInfo = config.entityInfo;
        if (entityInfo == null) {
            JpaEntityInspector inspector = new JpaEntityInspector(config.clazz);
            entityInfo = inspector.inspect();
        }
        this.entityInfo = entityInfo;
        this.encoding = config.encoding;
        this.batchSize = config.batchSize;
        this.autocommit = config.autocommit;
        this.concurrent = config.concurrent;
        this.errorOnMissingRow = config.errorOnMissingRow;
        SqlBuilder sqlBuiler = new SqlBuilder(entityInfo);
        mainSql = sqlBuiler.build();
        logger.debug("SQL Insert for {}: {}", entityInfo.getEntityClass().getName(), mainSql);
        logger.debug("Batch size: {} bytes", batchSize);
    }

    @Override
    public void insertAll(Connection conn, Stream<T> stream) throws JFleetException, SQLException {
        FileContentBuilder contentBuilder = new FileContentBuilder(entityInfo, batchSize, true);
        MySqlTransactionPolicy txPolicy = getTransactionPolicy(conn, autocommit, errorOnMissingRow);
        try (Statement stmt = getStatementForLoadLocal(conn)) {
            ContentWriter contentWriter = new LoadDataContentWriter(stmt, txPolicy, mainSql, encoding);
            if (concurrent) {
                contentWriter = new ParallelContentWriter(contentWriter);
            }
            Iterator<T> iterator = stream.iterator();
            while (iterator.hasNext()) {
                contentBuilder.add(iterator.next());
                if (contentBuilder.isFilled()) {
                    logger.debug("Writing content");
                    contentWriter.writeContent(contentBuilder.getContent());
                    contentBuilder.reset();
                }
            }
            logger.debug("Flushing content");
            contentWriter.writeContent(contentBuilder.getContent());
            contentWriter.waitForWrite();
            contentBuilder.reset();
        } finally {
            txPolicy.close();
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

    public static class Configuration<T> {

        private Class<T> clazz;
        private EntityInfo entityInfo;
        private Charset encoding = Charset.forName("UTF-8");
        private int batchSize = DEFAULT_BATCH_SIZE;
        private boolean autocommit = true;
        private boolean concurrent = true;
        private boolean errorOnMissingRow = false;

        public Configuration(Class<T> clazz) {
            this.clazz = clazz;
        }

        public Configuration(EntityInfo entityInfo) {
            this.entityInfo = entityInfo;
        }

        public Configuration<T> encoding(Charset encoding) {
            this.encoding = encoding;
            return this;
        }

        public Configuration<T> batchSize(int batchSize) {
            this.batchSize = batchSize;
            return this;
        }

        public Configuration<T> autocommit(boolean autocommit) {
            this.autocommit = autocommit;
            return this;
        }

        public Configuration<T> concurrent(boolean concurrent) {
            this.concurrent = concurrent;
            return this;
        }

        public Configuration<T> errorOnMissingRow(boolean errorOnMissingRow) {
            this.errorOnMissingRow = errorOnMissingRow;
            return this;
        }

    }

}
