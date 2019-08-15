package com.mysql.jdbc;

import java.io.InputStream;
import java.sql.ResultSet;

public class StatementImpl implements Statement {

    public ResultSetInternalMethods getResultSetInternal() {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public void execute(String sql) {

    }

    @Override
    public void setLocalInfileInputStream(InputStream inputStream) {

    }

    @Override
    public ResultSet executeQuery(String sql) {
        return null;
    }

}
