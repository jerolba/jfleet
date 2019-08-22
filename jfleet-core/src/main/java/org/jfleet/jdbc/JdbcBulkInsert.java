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
package org.jfleet.jdbc;

import static org.jfleet.common.DatabaseVersion.getVersion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jfleet.BulkInsert;
import org.jfleet.ColumnInfo;
import org.jfleet.EntityInfo;
import org.jfleet.JFleetException;
import org.jfleet.common.DatabaseVersion.Database;
import org.jfleet.common.TransactionPolicy;
import org.jfleet.jdbc.JdbcConfiguration.JdbcConfigurationBuilder;

public class JdbcBulkInsert<T> implements BulkInsert<T> {

    private final JdbcConfiguration cfg;
    private final String insertSql;

    private final List<Function<Object, Object>> accessors = new ArrayList<>();
    private final List<Function<Object, Object>> preConvert = new ArrayList<>();

    private final List<ColumnInfo> columns;

    public JdbcBulkInsert(Class<?> clazz) {
        this(JdbcConfigurationBuilder.from(clazz).build());
    }

    public JdbcBulkInsert(EntityInfo entityInfo) {
        this(JdbcConfigurationBuilder.from(entityInfo).build());
    }

    public JdbcBulkInsert(JdbcConfiguration config) {
        this.cfg = config;
        this.columns = cfg.getEntityInfo().getNotIdentityColumns();
        this.insertSql = createInsertQuery(cfg.getEntityInfo().getTableName(), columns);
        for (ColumnInfo column : columns) {
            accessors.add(column.getAccessor());
        }
        FieldPreConvert fieldPreConvert = new FieldPreConvert();
        for (ColumnInfo column : columns) {
            preConvert.add(fieldPreConvert.preConvert(column.getFieldType()));
        }
    }

    private String createInsertQuery(String tableName, List<ColumnInfo> columns) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(tableName).append(" (");
        sb.append(columns.stream().map(ColumnInfo::getColumnName).collect(Collectors.joining(", ")));
        sb.append(") values (");
        sb.append(columns.stream().map(f -> "?").collect(Collectors.joining(", ")));
        sb.append(")");
        return sb.toString();
    }

    @Override
    public void insertAll(Connection conn, Stream<T> stream) throws JFleetException, SQLException {
        ParameterSetter parameterSetter = getParameterSetter(conn);
        TransactionPolicy txPolicy = TransactionPolicy.getTransactionPolicy(conn, cfg.isAutocommit());
        try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            BatchInsert batchInsert = new BatchInsert(txPolicy, pstmt, parameterSetter);
            Iterator<T> iterator = stream.iterator();
            while (iterator.hasNext()) {
                batchInsert.add(iterator.next());
            }
            batchInsert.finish();
        } finally {
            txPolicy.close();
        }
    }

    private class BatchInsert {

        private final TransactionPolicy txPolicy;
        private final PreparedStatement pstmt;
        private final ParameterSetter parameterSetter;
        private int count = 0;

        BatchInsert(TransactionPolicy txPolicy, PreparedStatement pstmt, ParameterSetter parameterSetter) {
            this.txPolicy = txPolicy;
            this.pstmt = pstmt;
            this.parameterSetter = parameterSetter;
        }

        public void add(T entity) throws SQLException {
            setObjectValues(pstmt, entity);
            pstmt.addBatch();
            count++;
            if (count == cfg.getBatchSize()) {
                flush();
                count = 0;
            }
        }

        public void finish() throws SQLException {
            if (count > 0) {
                flush();
            }
        }

        private void flush() throws SQLException {
            pstmt.executeBatch();
            txPolicy.commit();
        }

        private void setObjectValues(PreparedStatement pstmt, T entity) throws SQLException {
            for (int i = 0; i < columns.size(); i++) {
                Function<Object, Object> accessor = accessors.get(i);
                Object value = accessor.apply(entity);
                Function<Object, Object> f = preConvert.get(i);
                parameterSetter.setParameter(pstmt, i + 1, f.apply(value));
            }
        }
    }

    private ParameterSetter getParameterSetter(Connection connection) throws SQLException {
        if (cfg.getParameterSetter() != null) {
            return cfg.getParameterSetter();
        }
        return getVersion(connection).map(this::getSetter).orElseGet(() -> new DefaultParameterSetter());
    }

    private ParameterSetter getSetter(Database database) {
        if (database == Database.MsSql) {
            return new MsSqlParameterSetter();
        }
        return new DefaultParameterSetter();
    }

}
