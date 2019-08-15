package org.jfleet.mysql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

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
    public void setLocalInfileInputStream(ReaderInputStream ris) {
        statement.setLocalInfileInputStream(ris);
    }

    @Override
    public void execute(String mainSql) throws SQLException {
        statement.execute(mainSql);
    }

    @Override
    public Optional<Long> getUpdatedRows() {
        return ResultsetInspector.getUpdatedRows(statement);
    }

}
