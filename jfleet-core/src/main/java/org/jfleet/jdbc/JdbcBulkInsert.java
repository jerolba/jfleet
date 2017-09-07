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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.jfleet.BulkInsert;
import org.jfleet.EntityFieldAccesorFactory;
import org.jfleet.EntityFieldAccessor;
import org.jfleet.EntityInfo;
import org.jfleet.FieldInfo;
import org.jfleet.JFleetException;
import org.jfleet.JpaEntityInspector;
import org.jfleet.WrappedException;
import org.jfleet.common.TransactionPolicy;

public class JdbcBulkInsert<T> implements BulkInsert<T> {

    private static final int BATCH_SIZE = 10_000;
    private final EntityInfo entityInfo;
    private final String insertSql;

    private final List<EntityFieldAccessor> accessors = new ArrayList<>();

    private final List<FieldInfo> fields;

    public JdbcBulkInsert(Class<T> clazz) {
        JpaEntityInspector inspector = new JpaEntityInspector(clazz);
        this.entityInfo = inspector.inspect();
        this.insertSql = createInsertQuery(entityInfo);

        this.fields = entityInfo.getFields();
        EntityFieldAccesorFactory factory = new EntityFieldAccesorFactory();
        for (FieldInfo f : fields) {
            EntityFieldAccessor accesor = factory.getAccessor(entityInfo.getEntityClass(), f);
            accessors.add(accesor);
        }
    }

    private String createInsertQuery(EntityInfo entityInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(entityInfo.getTableName()).append(" (");
        List<FieldInfo> fields = entityInfo.getFields();
        for (int i = 0; i < fields.size(); i++) {
            FieldInfo fieldInfo = fields.get(i);
            sb.append(fieldInfo.getColumnName());
            if (i < fields.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(") values (");
        for (int i = 0; i < fields.size(); i++) {
            sb.append('?');
            if (i < fields.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public void insertAll(Connection conn, Stream<T> stream) throws JFleetException {
        try {
            TransactionPolicy txPolicy = TransactionPolicy.getTransactionPolicy(conn, false);
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                BatchInsert statementCount = new BatchInsert(conn, txPolicy, pstmt);
                stream.forEach(statementCount::add);
                statementCount.finish();
            } finally {
                txPolicy.close();
            }
        } catch (SQLException e) {
            throw new JFleetException(e);
        } catch (WrappedException e) {
            e.rethrow();
        }
    }

    private class BatchInsert {

        private final TransactionPolicy txPolicy;
        private final PreparedStatement pstmt;
        private int count = 0;

        BatchInsert(Connection connection, TransactionPolicy txPolicy, PreparedStatement pstmt) throws SQLException {
            this.txPolicy = txPolicy;
            this.pstmt = pstmt;
        }

        public void add(T entity) {
            try {
                setObjectValues(pstmt, entity);
                pstmt.addBatch();
                count++;
                if (count == BATCH_SIZE) {
                    flush();
                    count = 0;
                }
            } catch (SQLException e) {
                throw new WrappedException(e);
            }
        }

        public void finish() throws SQLException {
            if (count > 0) {
                flush();
            }
        }

        private void flush() throws SQLException {
            int[] result = pstmt.executeBatch();
            txPolicy.commit();
        }

    }

    public void setObjectValues(PreparedStatement pstmt, T entity) throws SQLException {
        for (int i = 0; i < fields.size(); i++) {
            EntityFieldAccessor accessor = accessors.get(i);
            Object value = accessor.getValue(entity);
            setParameter(pstmt, i + 1, value);
        }
    }

    public void setParameter(PreparedStatement pstmt, int parameterIndex, Object parameterObj) throws SQLException {
        if (parameterObj == null) {
            pstmt.setNull(parameterIndex, java.sql.Types.OTHER);
        } else {
            if (parameterObj instanceof Integer) {
                pstmt.setInt(parameterIndex, ((Integer) parameterObj).intValue());
            } else if (parameterObj instanceof String) {
                pstmt.setString(parameterIndex, (String) parameterObj);
            } else if (parameterObj instanceof Long) {
                pstmt.setLong(parameterIndex, ((Long) parameterObj).longValue());
            } else if (parameterObj instanceof Boolean) {
                pstmt.setBoolean(parameterIndex, ((Boolean) parameterObj).booleanValue());
            } else if (parameterObj instanceof java.util.Date) {
                pstmt.setTimestamp(parameterIndex, new Timestamp(((java.util.Date) parameterObj).getTime()));
            } else if (parameterObj instanceof BigDecimal) {
                pstmt.setBigDecimal(parameterIndex, (BigDecimal) parameterObj);
            } else if (parameterObj instanceof Byte) {
                pstmt.setInt(parameterIndex, ((Byte) parameterObj).intValue());
            } else if (parameterObj instanceof Character) {
                pstmt.setString(parameterIndex, ((Character) parameterObj).toString());
            } else if (parameterObj instanceof Short) {
                pstmt.setShort(parameterIndex, ((Short) parameterObj).shortValue());
            } else if (parameterObj instanceof Float) {
                pstmt.setFloat(parameterIndex, ((Float) parameterObj).floatValue());
            } else if (parameterObj instanceof Double) {
                pstmt.setDouble(parameterIndex, ((Double) parameterObj).doubleValue());
            } else if (parameterObj instanceof java.sql.Date) {
                pstmt.setDate(parameterIndex, (java.sql.Date) parameterObj);
            } else if (parameterObj instanceof Time) {
                pstmt.setTime(parameterIndex, (Time) parameterObj);
            } else if (parameterObj instanceof Timestamp) {
                pstmt.setTimestamp(parameterIndex, (Timestamp) parameterObj);
            } else if (parameterObj instanceof BigInteger) {
                pstmt.setObject(parameterIndex, parameterObj);
            } else {
                throw new RuntimeException("No type mapper for " + parameterObj.getClass());
            }
        }
    }

}
