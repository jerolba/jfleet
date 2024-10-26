package org.jfleet.util;

import static org.jfleet.parameterized.Databases.JdbcMySql;
import static org.jfleet.parameterized.Databases.JdbcPosgres;
import static org.jfleet.parameterized.Databases.MySql;
import static org.jfleet.parameterized.Databases.Postgres;
import static org.jfleet.parameterized.IsMySql5Condition.isMySql5Present;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfleet.parameterized.Databases;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;

public class DatabaseContainers {

    private static final String MYSQL_5_VERSION = "mysql:5.7.34";
    private static final String MYSQL_8_VERSION = "mysql:8.0.39";
    private static final String POSTGRES_12_VERSION = "postgres:12-alpine";

    private static final Map<Databases, JdbcDatabaseContainer<?>> map = new HashMap<>();
    static {
        PostgreSQLContainer<?> postgresContainer = createPostgresContainer();
        MySQLContainer<?> mysqlContainer = createMySqlContainer();
        map.put(Postgres, postgresContainer);
        map.put(JdbcPosgres, postgresContainer);
        map.put(MySql, mysqlContainer);
        map.put(JdbcMySql, mysqlContainer);

        List.of(postgresContainer, mysqlContainer).parallelStream().forEach(GenericContainer::start);
    }

    public static JdbcDatabaseContainer<?> getContainer(Databases database) {
        return map.get(database);
    }

    private static PostgreSQLContainer<?> createPostgresContainer() {
        return new PostgreSQLContainer<>(POSTGRES_12_VERSION)
                .withUsername("test")
                .withPassword("test")
                .withPassword("test")
                .withDatabaseName("postgresdb")
                .withUrlParam("reWriteBatchedInserts", "true");
    }

    private static MySQLContainer<?> createMySqlContainer() {
        return new MySQLContainer<>(getMysqlVersion())
                .withConfigurationOverride("my-sql-config")
                .withUsername("test")
                .withPassword("test")
                .withDatabaseName("testdb")
                .withUrlParam("useSSL", "false")
                .withUrlParam("allowPublicKeyRetrieval", "true")
                .withUrlParam("useUnicode", "true")
                .withUrlParam("characterEncoding", "utf-8")
                .withUrlParam("allowLoadLocalInfile", "true");
    }

    private static String getMysqlVersion() {
        return isMySql5Present() ? MYSQL_5_VERSION: MYSQL_8_VERSION;
    }

}
