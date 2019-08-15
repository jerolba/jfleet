package org.jfleet.mysql;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

import org.jfleet.JFleetException;

interface Statement extends AutoCloseable {

    static Statement createStatement(Connection conn) throws JFleetException{
        Connection unwrapped = null;
        try {
            Class<?> mysql5Class = Class.forName("com.mysql.jdbc.Connection");
            unwrapped = (Connection) conn.unwrap(mysql5Class);
            return new MySql5Statement(unwrapped);
        } catch (ClassNotFoundException | SQLException e) {
            try {
                Class<?> mysql8Class = Class.forName("com.mysql.cj.jdbc.JdbcConnection");
                unwrapped = (Connection) conn.unwrap(mysql8Class);
                return new MySql8Statement(unwrapped);
            } catch (ClassNotFoundException | SQLException e2) {
                throw new RuntimeException("Incorrect Connection type. Expected com.mysql.jdbc.Connection"
                        + " or com.mysql.cj.jdbc.JdbcConnection");
            }
        }
    }

    void setLocalInfileInputStream(InputStream is);

    @Override
    void close() throws SQLException;

    void execute(String mainSql) throws SQLException;

    long getUpdatedRows();

}
