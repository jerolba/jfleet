package org.jfleet.parameterized;

import org.jfleet.BulkInsert;
import org.jfleet.jdbc.JdbcBulkInsert;

public class JdbcPostgresDatabase extends Database {

    public JdbcPostgresDatabase(String properties) {
        super(properties);
    }

    public JdbcPostgresDatabase() {
        super("postgres-test.properties");
    }

    @Override
    public <T> BulkInsert<T> getBulkInsert(Class<T> clazz) {
        return new JdbcBulkInsert<>(clazz);
    }

}
