package org.jfleet.mysql;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

import com.mysql.jdbc.ResultsetInspector;

class MySql5Statement implements org.jfleet.mysql.Statement {

    private final com.mysql.jdbc.Statement statement;

    public MySql5Statement(Connection unwrapped) throws SQLException {
        com.mysql.jdbc.Connection connection = (com.mysql.jdbc.Connection) unwrapped;
        connection.setAllowLoadLocalInfile(true);
        this.statement = (com.mysql.jdbc.Statement) connection.createStatement();
    }

    @Override
    public void close() throws SQLException {
        statement.close();
    }

    @Override
    public void setLocalInfileInputStream(InputStream is) {
        statement.setLocalInfileInputStream(is);
    }

    @Override
    public void execute(String mainSql) throws SQLException {
        statement.execute(mainSql);
    }

    @Override
    public long getUpdatedRows() {
        return ResultsetInspector.getUpdatedRows(statement);
    }

}
