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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.stream.Stream;

import org.jfleet.BulkInsert;
import org.jfleet.EntityInfo;
import org.jfleet.JFleetException;
import org.jfleet.common.ContentWriter;
import org.jfleet.common.LoopAndWrite;
import org.jfleet.mysql.LoadDataConfiguration.LoadDataConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysql.jdbc.Statement;

public class LoadDataBulkInsert<T> implements BulkInsert<T> {

    private static Logger logger = LoggerFactory.getLogger(LoadDataBulkInsert.class);

    private final LoadDataConfiguration cfg;
    private final String mainSql;

    public LoadDataBulkInsert(Class<?> clazz) {
        this(LoadDataConfigurationBuilder.from(clazz).build());
    }

    public LoadDataBulkInsert(EntityInfo entityInfo) {
        this(LoadDataConfigurationBuilder.from(entityInfo).build());
    }

    public LoadDataBulkInsert(LoadDataConfiguration loadDataConfiguration) {
        this.cfg = loadDataConfiguration;
        SqlBuilder sqlBuiler = new SqlBuilder(cfg.getEntityInfo());
        mainSql = sqlBuiler.build();
        logger.debug("SQL Insert for {}: {}", cfg.getEntityInfo().getEntityClass().getName(), mainSql);
        logger.debug("Batch size: {} bytes", cfg.getBatchSize());
    }

    @Override
    public void insertAll(Connection conn, Stream<T> stream) throws JFleetException, SQLException {
        LoadDataRowBuilder rowBuilder = new LoadDataRowBuilder(cfg.getEntityInfo());
        MySqlTransactionPolicy txPolicy = getTransactionPolicy(conn, cfg.isAutocommit(), cfg.isErrorOnMissingRow());
        try (Statement stmt = getStatementForLoadLocal(conn)) {
            ContentWriter contentWriter = new LoadDataContentWriter(stmt, txPolicy, mainSql, cfg.getEncoding());
            ContentWriter wrappedContentWriter = cfg.getWriterWrapper().apply(contentWriter);
            LoopAndWrite loopAndWrite = new LoopAndWrite(cfg, wrappedContentWriter, rowBuilder);
            loopAndWrite.go(stream);
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

}
