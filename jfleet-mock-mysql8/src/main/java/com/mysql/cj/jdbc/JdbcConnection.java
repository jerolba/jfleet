package com.mysql.cj.jdbc;

import java.sql.SQLException;
import java.util.Properties;

public class JdbcConnection implements AutoCloseable {

    public java.sql.Statement createStatement() throws SQLException {
        throw new SQLException();
    }

    @Override
    public void close() {

    }

    public Properties getProperties() {
        return null;
    }

}
