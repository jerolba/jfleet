package org.jfleet.mysql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Properties;

import org.jfleet.JFleetException;

import com.mysql.cj.jdbc.ResultsetInspector;

class MySql8Statement implements org.jfleet.mysql.Statement {

    private final com.mysql.cj.jdbc.StatementImpl statement;

    public MySql8Statement(Connection unwrapped) throws SQLException, JFleetException {
        com.mysql.cj.jdbc.JdbcConnection connection = (com.mysql.cj.jdbc.JdbcConnection) unwrapped;
        Properties properties = connection.getProperties();
        String allow = properties.getProperty("allowLoadLocalInfile");
        if (allow==null || !allow.toLowerCase().equals("true")) {
            throw new JFleetException("MySql 8 connection url must be configured with \"allowLoadLocalInfile=true\"");
        }
        this.statement = (com.mysql.cj.jdbc.StatementImpl) connection.createStatement();
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
