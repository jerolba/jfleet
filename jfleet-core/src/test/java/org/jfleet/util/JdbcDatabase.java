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
import org.jfleet.jdbc.JdbcBulkInsert;
import org.jfleet.jdbc.JdbcConfiguration;

public abstract class JdbcDatabase extends Database {

    public JdbcDatabase(String properties) {
        super(properties);
    }

    @Override
    public <T> BulkInsert<T> getBulkInsert(Class<T> clazz) {
        return new JdbcBulkInsert<>(clazz);
    }

    public <T> BulkInsert<T> getBulkInsert(JdbcConfiguration config) {
        return new JdbcBulkInsert<>(config);
    }

}
