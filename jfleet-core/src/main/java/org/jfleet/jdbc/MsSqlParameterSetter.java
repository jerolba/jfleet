package org.jfleet.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MsSqlParameterSetter implements ParameterSetter {

    @Override
    public void setParameter(PreparedStatement pstmt, int parameterIndex, Object parameterObj) throws SQLException {
        if (parameterObj == null) {
            pstmt.setObject(parameterIndex, null);
        } else {
            if (parameterObj instanceof Character) {
                pstmt.setString(parameterIndex, String.valueOf((Character) parameterObj));
            } else {
                pstmt.setObject(parameterIndex, parameterObj);
            }
        }

    }

}
