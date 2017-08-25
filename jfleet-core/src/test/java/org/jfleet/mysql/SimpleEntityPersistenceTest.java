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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.jfleet.mysql.LoadDataBulkInsert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleEntityPersistenceTest {

	private static Logger logger = LoggerFactory.getLogger(SimpleEntityPersistenceTest.class);
	
	@Entity
	@Table(name = "simple_table")
	public static class SimpleEntity {

		private String name;
		private boolean active;
		private int age;

		public SimpleEntity(String name, boolean active, int age) {
			this.name = name;
			this.active = active;
			this.age = age;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public boolean isActive() {
			return active;
		}

		public void setActive(boolean active) {
			this.active = active;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}

	}

	@Test
	public void canPersistCollectionOfEntities() throws SQLException, IOException {
		int times = 1000;

		LoadDataBulkInsert<SimpleEntity> insert = new LoadDataBulkInsert<>(SimpleEntity.class);
		List<SimpleEntity> list = IntStream.range(0, times).mapToObj(i -> new SimpleEntity("name_" + leftPad(i), i % 2 == 0, i))
				.collect(Collectors.toList());

		MySqlTestConnectionProvider connectionProvider = new MySqlTestConnectionProvider();
		try (Connection conn = connectionProvider.get()) {
			SqlUtil.createTableForEntity(conn, SimpleEntity.class);
			long before = System.nanoTime();
			insert.insertAll(conn, list);
			logger.info("Time "+((System.nanoTime()-before))/1_000_000+" ms");

			try (Statement stmt = conn.createStatement()) {
				try (ResultSet rs = stmt.executeQuery("SELECT name, active, age FROM simple_table ORDER BY name ASC")) {
					for (int i = 0; i < times; i++) {
						assertTrue(rs.next());
						assertEquals("name_"+leftPad(i),rs.getString(1));
						assertEquals((i+1) % 2,rs.getInt(2));
						assertEquals(i,rs.getInt(3));
					}
				}
			}
		}
	}
	
	private String leftPad(int i) {
		String str = Integer.toString(i);
		while(str.length()<6) {
			str="0"+str;
		}
		return str;
	}
}
