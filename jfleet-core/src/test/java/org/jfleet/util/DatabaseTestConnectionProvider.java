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
package org.jfleet.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.function.Supplier;

import org.testcontainers.containers.JdbcDatabaseContainer;

public class DatabaseTestConnectionProvider implements Supplier<Connection> {

    private final JdbcDatabaseContainer<?> container;

    public DatabaseTestConnectionProvider(JdbcDatabaseContainer<?> container) {
        this.container = container;
    }

    @Override
    public Connection get() {
        String driver = container.getDriverClassName();
        try {
            Class.forName(driver).newInstance();
            Connection conn = DriverManager.getConnection(container.getJdbcUrl(), container.getUsername(),
                    container.getPassword());
            return conn;
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException ex) {
            throw new RuntimeException("Can not instantiate driver " + driver, ex);
        } catch (SQLException ex) {
            throw new RuntimeException("Can not connect to database", ex);
        }
    }

}
