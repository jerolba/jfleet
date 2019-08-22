package org.jfleet.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface ParameterSetter {
    
    void setParameter(PreparedStatement pstmt, int parameterIndex, Object parameterObj) throws SQLException;
    
}