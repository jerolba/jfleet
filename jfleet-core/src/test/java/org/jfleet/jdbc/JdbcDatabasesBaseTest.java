/**
 * Copyright 2017 Jerónimo López Bezanilla
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
package org.jfleet.jdbc;

import java.util.Arrays;
import java.util.Collection;

import org.jfleet.parameterized.JdbcDatabase;
import org.jfleet.parameterized.JdbcMysqlDatabase;
import org.jfleet.parameterized.JdbcPostgresDatabase;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public abstract class JdbcDatabasesBaseTest {

    @Parameters(name = "Database {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            {"JdbcPosgres", new JdbcPostgresDatabase()},
            {"JdbcMySql", new JdbcMysqlDatabase()}
        });
    }

    @Parameter(0)
    public String databaseName;

    @Parameter(1)
    public JdbcDatabase database;

}
