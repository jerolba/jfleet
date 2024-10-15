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
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.cj.jdbc.JdbcConnection;
import org.jfleet.EntityInfo;
import org.jfleet.inspection.JpaEntityInspector;
import org.jfleet.util.Dialect.DDLHelper;
import org.postgresql.jdbc.PgConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlUtil {

    private static Logger logger = LoggerFactory.getLogger(SqlUtil.class);

    public static void createTableForEntity(Connection conn, Class<?> clazz) throws SQLException {
        JpaEntityInspector inspector = new JpaEntityInspector(clazz);
        EntityInfo entityInfo = inspector.inspect();
        Dialect dialect = getDialect(conn);
        DDLHelper ddlHelper = dialect.getDDLHelper();
        try (Statement stmt = conn.createStatement()) {
            String dropTableSql = ddlHelper.dropTableSentence(entityInfo);
            logger.info("Droping table: " + dropTableSql);
            stmt.execute(dropTableSql);

            String createTableSql = ddlHelper.createTableSentence(entityInfo);
            logger.info("Creating table: " + createTableSql);
            stmt.execute(createTableSql);
        }
    }

    private static Dialect getDialect(Connection conn) {
        try {
            conn.unwrap(com.mysql.jdbc.Connection.class);
            return Dialect.Mysql;
        } catch (SQLException e) {
            try {
                conn.unwrap(JdbcConnection.class);
                return Dialect.Mysql;
            } catch (SQLException e1) {
                try {
                    conn.unwrap(PgConnection.class);
                    return Dialect.Postgres;
                } catch (SQLException e2) {
                    logger.error("No dialect found for connection");
                    return null;
                }
            }
        }
    }
}
