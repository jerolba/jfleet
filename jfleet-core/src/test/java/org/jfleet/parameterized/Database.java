package org.jfleet.parameterized;

import java.io.IOException;
import java.sql.Connection;

import org.jfleet.BulkInsert;
import org.jfleet.util.DataBaseTestConnectionProvider;

public abstract class Database {

    private String properties;

    public Database(String properties) {
        this.properties = properties;
    }

    public String getProperties() {
        return properties;
    }

    public abstract <T> BulkInsert<T> getBulkInsert(Class<T> clazz);

    public Connection getConnection() throws IOException {
        return new DataBaseTestConnectionProvider(getProperties()).get();
    }

}
