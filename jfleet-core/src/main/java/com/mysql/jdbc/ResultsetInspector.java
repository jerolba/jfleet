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
package com.mysql.jdbc;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResultsetInspector {

    private static Logger logger = LoggerFactory.getLogger(ResultsetInspector.class);

    public static Optional<Long> getUpdatedRows(Statement statement) {
        if (statement instanceof com.mysql.jdbc.StatementImpl) {
            StatementImpl impl = (StatementImpl) statement;
            ResultSetInternalMethods resultSetInternal = impl.getResultSetInternal();
            return Optional.of(resultSetInternal.getUpdateCount());
        }
        logger.warn("Mysql connection does not create com.mysql.jdbc.StatementImpl statements, "
                + "needed to fetch inserted rows. JFleet can not check if all rows are inserted.");
        return Optional.empty();
    }

}
