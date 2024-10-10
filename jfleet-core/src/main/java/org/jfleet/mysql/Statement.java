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

import org.jfleet.JFleetException;

interface Statement extends AutoCloseable {

    String MY_SQL5 = "com.mysql.jdbc.Connection";
    String MY_SQL8 = "com.mysql.cj.jdbc.JdbcConnection";

    static Statement createStatement(Connection conn) throws JFleetException {
        Connection unwrapped = null;
        try {
            Class<?> mysql5Class = Class.forName(MY_SQL5);
            unwrapped = (Connection) conn.unwrap(mysql5Class);
            return new MySql5Statement(unwrapped);
        } catch (ClassNotFoundException | SQLException e) {
            try {
                Class<?> mysql8Class = Class.forName(MY_SQL8);
                unwrapped = (Connection) conn.unwrap(mysql8Class);
                return new MySql8Statement(unwrapped);
            } catch (ClassNotFoundException | SQLException e2) {
                throw new RuntimeException("Incorrect Connection type. Expected " + MY_SQL5 + " or " + MY_SQL8);
            }
        }
    }

    void setLocalInfileInputStream(InputStream is);

    @Override
    void close() throws SQLException;

    void execute(String mainSql) throws SQLException;

    long getUpdatedRows();

}
