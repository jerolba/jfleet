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
