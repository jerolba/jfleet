package com.mysql.cj.jdbc;

import java.sql.SQLException;

public class JdbcConnection implements AutoCloseable {

    public void setAllowLoadLocalInfile(boolean b) {
    }

    public java.sql.Statement createStatement() throws SQLException{
        throw new SQLException();
    }

    @Override
    public void close() {

    }

}
