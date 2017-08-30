package org.jfleet.parameterized;

import org.jfleet.BulkInsert;
import org.jfleet.postgres.PgCopyBulkInsert;

public class PostgresDatabase extends Database {

    public PostgresDatabase(String properties) {
        super(properties);
    }

    public PostgresDatabase() {
        super("postgres-test.properties");
    }

    @Override
    public <T> BulkInsert<T> getBulkInsert(Class<T> clazz) {
        return new PgCopyBulkInsert<>(clazz);
    }

}
