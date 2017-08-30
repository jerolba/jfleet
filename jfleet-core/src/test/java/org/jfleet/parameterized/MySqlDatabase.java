package org.jfleet.parameterized;

import org.jfleet.BulkInsert;
import org.jfleet.mysql.LoadDataBulkInsert;

public class MySqlDatabase extends Database {

    public MySqlDatabase(String properties) {
        super(properties);
    }

    public MySqlDatabase() {
        super("mysql-test.properties");
    }

    @Override
    public <T> BulkInsert<T> getBulkInsert(Class<T> clazz) {
        return new LoadDataBulkInsert<>(clazz);
    }

}
