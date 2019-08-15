package com.mysql.cj.jdbc;

import java.io.InputStream;

public interface JdbcStatement extends AutoCloseable {

    @Override
    public void close();

    void execute(String string);

    void setLocalInfileInputStream(InputStream inputStream);

}
