package org.jfleet.parameterized;

import org.jfleet.util.Database;
import org.jfleet.util.DatabaseContainers;
import org.jfleet.util.JdbcMysqlDatabase;
import org.jfleet.util.JdbcPostgresDatabase;
import org.jfleet.util.MySqlDatabase;
import org.jfleet.util.PostgresDatabase;
import org.testcontainers.containers.JdbcDatabaseContainer;

public class DatabaseProvider {

    public static Database getDatabase(Databases enumValue) {
        JdbcDatabaseContainer<?> container = DatabaseContainers.getContainer(enumValue);
        switch (enumValue) {
            case JdbcMySql:
                return new JdbcMysqlDatabase(container);
            case JdbcPosgres:
                return new JdbcPostgresDatabase(container);
            case MySql:
                return new MySqlDatabase(container);
            case Postgres:
                return new PostgresDatabase(container);
        }

        return null;
    }
}
