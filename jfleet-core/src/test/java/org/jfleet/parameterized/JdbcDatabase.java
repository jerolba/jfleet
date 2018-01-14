package org.jfleet.parameterized;

import org.jfleet.BulkInsert;
import org.jfleet.jdbc.JdbcBulkInsert;
import org.jfleet.jdbc.JdbcBulkInsert.Configuration;

public abstract class JdbcDatabase extends Database {

    public JdbcDatabase(String properties) {
        super(properties);
    }

    @Override
    public <T> BulkInsert<T> getBulkInsert(Class<T> clazz) {
        return new JdbcBulkInsert<>(clazz);
    }

    public <T> BulkInsert<T> getBulkInsert(Configuration<T> config) {
        return new JdbcBulkInsert<>(config);
    }

}
