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

import org.jfleet.BulkInsert;
import org.jfleet.postgres.PgCopyBulkInsert;
import org.testcontainers.containers.JdbcDatabaseContainer;

public class PostgresDatabase extends Database {

    public PostgresDatabase(JdbcDatabaseContainer<?> postgresContainer) {
        super(postgresContainer);
    }

    @Override
    public <T> BulkInsert<T> getBulkInsert(Class<T> clazz) {
        return new PgCopyBulkInsert<>(clazz);
    }

}
