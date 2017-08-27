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
package org.jfleet.postgres;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.jfleet.BulkInsert;
import org.jfleet.JFleetException;
import org.jfleet.common.SimpleEntity;
import org.jfleet.util.SqlUtil;
import org.junit.Test;

public class SimpleEntityPersistenceTest {

	@Test
	public void canPersistCollectionOfEntities() throws JFleetException, SQLException, IOException {
        int times = 1000;

		BulkInsert<SimpleEntity> insert = new PgCopyBulkInsert<>(SimpleEntity.class);
		Stream<SimpleEntity> stream = IntStream.range(0, times)
                .mapToObj(i -> new SimpleEntity("name_" + i, i % 2 == 0, i));

		Supplier<Connection> connectionProvider = new PostgresTestConnectionProvider();
		try (Connection conn = connectionProvider.get()) {
			SqlUtil.createTableForEntity(conn, SimpleEntity.class);
			insert.insertAll(conn, stream);

			try (Statement stmt = conn.createStatement()) {
				try (ResultSet rs = stmt.executeQuery("SELECT name, active, age FROM simple_table ORDER BY age ASC")) {
					for (int i = 0; i < times; i++) {
						assertTrue(rs.next());
						assertEquals("name_"+i,rs.getString(1));
						assertEquals((i+1) % 2 == 1,rs.getBoolean(2));
						assertEquals(i,rs.getInt(3));
					}
				}
			}
		}
	}

}
