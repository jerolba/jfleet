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
package org.jfleet.mysql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.jfleet.EntityInfo;
import org.jfleet.JpaEntityInspector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlUtil {
	
	private static Logger logger = LoggerFactory.getLogger(SqlUtil.class);

	public static void createTableForEntity(Connection conn, Class<?> clazz) throws SQLException {
		JpaEntityInspector inspector = new JpaEntityInspector(clazz);
		EntityInfo entityInfo = inspector.inspect();
		CreateTableBuilder tableBuilder = new CreateTableBuilder();
		String sql = tableBuilder.createTableSentence(entityInfo, true);
		try (Statement stmt = conn.createStatement()) {
			logger.info("Creating table: "+sql);
			stmt.execute(sql);
		}

	}
}
