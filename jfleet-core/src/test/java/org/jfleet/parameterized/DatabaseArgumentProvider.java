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

import org.jfleet.util.*;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.stream.Stream;

import static org.jfleet.parameterized.Databases.*;

public class DatabaseArgumentProvider implements ArgumentsProvider {

    //TODO move this
    private static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:12-alpine")
            .withUsername("test")
                .withPassword("test")
                .withDatabaseName("postgresdb")
                .withUrlParam("reWriteBatchedInserts", "true");
    private static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:5.7.34")
            .withUsername("test")
                .withPassword("test")
                .withDatabaseName("testdb")
                .withUrlParam("useSSL","false")
                .withUrlParam("allowPublicKeyRetrieval", "true")
                .withUrlParam("useUnicode","true")
                .withUrlParam("characterEncoding", "utf-8")
                .withUrlParam("allowLoadLocalInfile", "true");

    private static final Map<Databases, GenericContainer<?>> map = Map.of(
        Postgres, postgreSQLContainer,
        JdbcPosgres, postgreSQLContainer,
        MySql, mySQLContainer,
        JdbcMySql, mySQLContainer
    );

    static {
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
        return switch (enumValue) {
            case JdbcMySql -> new JdbcMysqlDatabase(mySQLContainer);
            case JdbcPosgres -> new JdbcPostgresDatabase(postgreSQLContainer);
            case MySql -> new MySqlDatabase(mySQLContainer);
            case Postgres -> new PostgresDatabase(postgreSQLContainer);
        };
    }

}
