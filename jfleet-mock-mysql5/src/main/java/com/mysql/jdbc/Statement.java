package com.mysql.jdbc;

import java.io.InputStream;
import java.sql.ResultSet;

public interface Statement extends AutoCloseable {

    @Override
    public void close();

    void execute(String string);

    void setLocalInfileInputStream(InputStream inputStream);

    public ResultSet executeQuery(String string);

}
