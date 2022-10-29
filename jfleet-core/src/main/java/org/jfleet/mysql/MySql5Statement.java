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
package org.jfleet.mysql;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

import com.mysql.jdbc.ResultsetInspector;

class MySql5Statement implements org.jfleet.mysql.Statement {

    private final com.mysql.jdbc.Statement statement;

    MySql5Statement(Connection unwrapped) throws SQLException {
        com.mysql.jdbc.Connection connection = (com.mysql.jdbc.Connection) unwrapped;
        connection.setAllowLoadLocalInfile(true);
        this.statement = (com.mysql.jdbc.Statement) connection.createStatement();
    }

    @Override
    public void close() throws SQLException {
        statement.close();
    }

    @Override
    public void setLocalInfileInputStream(InputStream is) {
        statement.setLocalInfileInputStream(is);
    }

    @Override
    public void execute(String mainSql) throws SQLException {
        statement.execute(mainSql);
    }

    @Override
    public long getUpdatedRows() {
        return ResultsetInspector.getUpdatedRows(statement);
    }

}
