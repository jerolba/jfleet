package com.mysql.cj.jdbc;

import java.io.InputStream;

import com.mysql.cj.jdbc.result.ResultSetInternalMethods;

public class StatementImpl implements JdbcStatement {

    @Override
    public void close() {
    }

    @Override
    public void setLocalInfileInputStream(InputStream is) {
    }

    @Override
    public void execute(String sql) {
    }

    public ResultSetInternalMethods getResultSetInternal() {
        return null;
    }

}
