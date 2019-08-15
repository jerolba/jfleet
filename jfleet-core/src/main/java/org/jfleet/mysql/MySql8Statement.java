package org.jfleet.mysql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.RuntimeProperty;
import com.mysql.cj.jdbc.JdbcPropertySet;
import com.mysql.cj.jdbc.ResultsetInspector;

class MySql8Statement implements org.jfleet.mysql.Statement {

    private final com.mysql.cj.jdbc.StatementImpl statement;

    public MySql8Statement(Connection unwrapped) throws SQLException {
        com.mysql.cj.jdbc.JdbcConnection connection = (com.mysql.cj.jdbc.JdbcConnection) unwrapped;
        JdbcPropertySet propertySet = connection.getPropertySet();
        RuntimeProperty<Boolean> allow = propertySet.getProperty(PropertyKey.allowLoadLocalInfile);
        Boolean value = allow.getValue();
        if (value==false) {
            throw new SQLException("MySql 8 connection url must be configured with \"allowLoadLocalInfile=true\"");
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
