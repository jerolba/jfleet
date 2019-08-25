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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DefaultParameterSetter implements ParameterSetter {

    /*
     * Each JDBC driver implements code like this in their setObject(idx, object)
     * method. If following conversions are not supported by your driver (like
     * LocalDate, LocalTime, and LocalDateTime), extend and overwrite it with the
     * correct one.
     */

    @Override
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
                pstmt.setByte(parameterIndex, ((Byte) parameterObj).byteValue());
            } else if (parameterObj instanceof Character) {
                pstmt.setString(parameterIndex, String.valueOf((Character) parameterObj));
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
            } else if (parameterObj instanceof LocalDate) {
                pstmt.setObject(parameterIndex, parameterObj);
            } else if (parameterObj instanceof LocalTime) {
                pstmt.setObject(parameterIndex, parameterObj);
            } else if (parameterObj instanceof LocalDateTime) {
                pstmt.setObject(parameterIndex, parameterObj);
            } else {
                throw new RuntimeException("No type mapper for " + parameterObj.getClass());
            }
        }
    }

}
