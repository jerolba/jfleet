package com.mysql.jdbc;

import java.sql.SQLException;

public class Connection implements AutoCloseable {

    public void setAllowLoadLocalInfile(boolean b) {
    }

    public java.sql.Statement createStatement() throws SQLException{
        throw new SQLException();
    }

    @Override
    public void close() {

    }

}
