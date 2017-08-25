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

import java.io.InputStream;
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
import org.jfleet.JpaEntityInspector;

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
            EntityFieldAccessor accesor = factory.getAccesor(entityInfo.getEntityClass(), f);
            accessors.add(accesor);
        }
    }

    private String createInsertQuery(EntityInfo entityInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(entityInfo.getTableName()).append(" (");
        List<FieldInfo> fields = entityInfo.getFields();
        for (int i = 0; i < fields.size(); i++) {
            FieldInfo fieldInfo = fields.get(i);
            sb.append('`').append(fieldInfo.getColumnName()).append('`');
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
    public void insertAll(Connection conn, Stream<T> stream) throws SQLException {
        conn.setAutoCommit(false);
        try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            BatchInsert statementCount = new BatchInsert(conn, pstmt);
            stream.forEach(statementCount::add);
            statementCount.finish();
        } catch (WrappedSQLException e) {
            throw e.getSQLException();
        } finally {
            conn.setAutoCommit(true);
        }
    }

    private class BatchInsert {

        private final Connection connection;
        private final PreparedStatement pstmt;
        private int count = 0;

        BatchInsert(Connection connection, PreparedStatement pstmt) throws SQLException {
            this.connection = connection;
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
                throw new WrappedSQLException(e);
            }
        }

        public void finish() throws SQLException {
            if (count > 0) {
                flush();
            }
        }

        private void flush() throws SQLException {
            int[] result = pstmt.executeBatch();
            connection.commit();
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
            if (parameterObj instanceof Byte) {
                pstmt.setInt(parameterIndex, ((Byte) parameterObj).intValue());
            } else if (parameterObj instanceof String) {
                pstmt.setString(parameterIndex, (String) parameterObj);
            } else if (parameterObj instanceof Character) {
                pstmt.setString(parameterIndex, ((Character) parameterObj).toString());
            } else if (parameterObj instanceof BigDecimal) {
                pstmt.setBigDecimal(parameterIndex, (BigDecimal) parameterObj);
            } else if (parameterObj instanceof Short) {
                pstmt.setShort(parameterIndex, ((Short) parameterObj).shortValue());
            } else if (parameterObj instanceof Integer) {
                pstmt.setInt(parameterIndex, ((Integer) parameterObj).intValue());
            } else if (parameterObj instanceof Long) {
                pstmt.setLong(parameterIndex, ((Long) parameterObj).longValue());
            } else if (parameterObj instanceof Float) {
                pstmt.setFloat(parameterIndex, ((Float) parameterObj).floatValue());
            } else if (parameterObj instanceof Double) {
                pstmt.setDouble(parameterIndex, ((Double) parameterObj).doubleValue());
            } else if (parameterObj instanceof byte[]) {
                pstmt.setBytes(parameterIndex, (byte[]) parameterObj);
            } else if (parameterObj instanceof java.sql.Date) {
                pstmt.setDate(parameterIndex, (java.sql.Date) parameterObj);
            } else if (parameterObj instanceof Time) {
                pstmt.setTime(parameterIndex, (Time) parameterObj);
            } else if (parameterObj instanceof Timestamp) {
                pstmt.setTimestamp(parameterIndex, (Timestamp) parameterObj);
            } else if (parameterObj instanceof Boolean) {
                pstmt.setBoolean(parameterIndex, ((Boolean) parameterObj).booleanValue());
            } else if (parameterObj instanceof InputStream) {
                pstmt.setBinaryStream(parameterIndex, (InputStream) parameterObj, -1);
            } else if (parameterObj instanceof java.sql.Blob) {
                pstmt.setBlob(parameterIndex, (java.sql.Blob) parameterObj);
            } else if (parameterObj instanceof java.sql.Clob) {
                pstmt.setClob(parameterIndex, (java.sql.Clob) parameterObj);
            } else if (parameterObj instanceof java.util.Date) {
                pstmt.setTimestamp(parameterIndex, new Timestamp(((java.util.Date) parameterObj).getTime()));
            } else if (parameterObj instanceof BigInteger) {
                pstmt.setString(parameterIndex, parameterObj.toString());
            } else {
                throw new RuntimeException("No type mapper for " + parameterObj.getClass());
            }
        }
    }

    private static class WrappedSQLException extends RuntimeException {

        private static final long serialVersionUID = 3733123280122720289L;

        WrappedSQLException(SQLException e) {
            super(e);
        }

        public SQLException getSQLException() {
            return (SQLException) this.getCause();
        }
    }

}
