/**
 * Copyright 2022 Jerónimo López Bezanilla
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jfleet.parameterized;

import static org.jfleet.parameterized.Databases.JdbcMySql;
import static org.jfleet.parameterized.Databases.JdbcPosgres;
import static org.jfleet.parameterized.Databases.MySql;
import static org.jfleet.parameterized.Databases.Postgres;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.jfleet.util.Database;
import org.jfleet.util.JdbcMysqlDatabase;
import org.jfleet.util.JdbcPostgresDatabase;
import org.jfleet.util.MySqlDatabase;
import org.jfleet.util.PostgresDatabase;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;

public class DatabaseArgumentProvider implements ArgumentsProvider {

    private static final PostgreSQLContainer<?> postgreSqlContainer = new PostgreSQLContainer<>("postgres:12-alpine")
            .withUsername("test")
            .withPassword("test")
            .withPassword("test")
            .withDatabaseName("postgresdb")
            .withUrlParam("reWriteBatchedInserts", "true");

    private static final MySQLContainer<?> mySqlContainer = new MySQLContainer<>("mysql:5.7.34")
            .withUsername("test")
            .withPassword("test")
            .withDatabaseName("testdb")
            .withUrlParam("useSSL","false")
            .withUrlParam("allowPublicKeyRetrieval", "true")
            .withUrlParam("useUnicode","true")
            .withUrlParam("characterEncoding", "utf-8")
            .withUrlParam("allowLoadLocalInfile", "true");

    private static final Map<Databases, GenericContainer<?>> map = new HashMap<>();

    static {
        map.put(Postgres, postgreSqlContainer);
        map.put(JdbcPosgres, postgreSqlContainer);
        map.put(MySql, mySqlContainer);
        map.put(JdbcMySql, mySqlContainer);
        map.values().parallelStream().forEach(GenericContainer::start);
    }

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context)  {
        Method testMethod = context.getTestMethod().get();
        DBs dbs = testMethod.getAnnotation(DBs.class);
        if (dbs != null) {
            Databases[] value = dbs.value();
            if (value != null && value.length > 0) {
                return getDatabases(value);
            }
        }
        return getDatabases(Databases.values());
    }

    private Stream<? extends Arguments> getDatabases(Databases[] dbs) {
        return Stream.of(dbs).map(DatabaseArgumentProvider::getDatabaseContainer).map(Arguments::of);
    }

    public static Database getDatabaseContainer(Databases enumValue) {
        switch (enumValue) {
            case JdbcMySql:
                return new JdbcMysqlDatabase(mySqlContainer);
            case JdbcPosgres:
                return new JdbcPostgresDatabase(postgreSqlContainer);
            case MySql:
                return new MySqlDatabase(mySqlContainer);
            case Postgres:
                return new PostgresDatabase(postgreSqlContainer);
        }

        return null;
    }

}
