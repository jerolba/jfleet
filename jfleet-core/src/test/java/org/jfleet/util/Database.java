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
package org.jfleet.util;

import java.io.IOException;
import java.sql.Connection;

import org.jfleet.BulkInsert;

public abstract class Database {

    private String properties;

    public Database(String properties) {
        this.properties = properties;
    }

    public String getProperties() {
        return properties;
    }

    public abstract <T> BulkInsert<T> getBulkInsert(Class<T> clazz);

    public Connection getConnection() throws IOException {
        return new DatabaseTestConnectionProvider(getProperties()).get();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName().replace("Database", "");
    }

}
