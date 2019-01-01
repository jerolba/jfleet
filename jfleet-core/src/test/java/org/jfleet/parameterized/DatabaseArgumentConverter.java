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

import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;

public class DatabaseArgumentConverter implements ArgumentConverter {

    @Override
    public Object convert(Object source, ParameterContext context) throws ArgumentConversionException {
        Databases enumValue = null;
        if (source instanceof Databases) {
            enumValue = (Databases) source;
        } else {
            enumValue = Databases.valueOf((String) source);
        }
        switch (enumValue) {
        case JdbcMySql:
            return new JdbcMysqlDatabase();
        case JdbcPosgres:
            return new JdbcPostgresDatabase();
        case MySql:
            return new MySqlDatabase();
        case Postgres:
            return new PostgresDatabase();
        }
        return null;
    }

}
