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
package org.jfleet.shared;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Stream;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.jfleet.BulkInsert;
import org.jfleet.JFleetException;
import org.jfleet.util.SqlUtil;
import org.junit.Test;

public class SpecialCharsPersistenceTest extends AllDatabasesBaseTest {

	@Entity
	@Table(name ="table_with_strings")
	class EnityWithStrings {

		private String foo;
		private String bar;

		public String getFoo() {
			return foo;
		}
		public void setFoo(String foo) {
			this.foo = foo;
		}
		public String getBar() {
			return bar;
		}
		public void setBar(String bar) {
			this.bar = bar;
		}

	}

	public void testWithString(String text) throws JFleetException, SQLException, IOException {
		EnityWithStrings entity = new EnityWithStrings();
		entity.setFoo("Some text");
		entity.setBar(text);

		BulkInsert<EnityWithStrings> insert = database.getBulkInsert(EnityWithStrings.class);

		try (Connection conn = database.getConnection()) {
			SqlUtil.createTableForEntity(conn, EnityWithStrings.class);
			insert.insertAll(conn, Stream.of(entity));

			try (Statement stmt = conn.createStatement()) {
				try (ResultSet rs = stmt.executeQuery("SELECT foo, bar FROM table_with_strings")) {
					assertTrue(rs.next());
					assertEquals("Some text", rs.getString("foo"));
					assertEquals(text, rs.getString("bar"));
				}
			}
		}
	}

	@Test
	public void persistTab() throws JFleetException, SQLException, IOException {
		testWithString("A text with a tab \t char inside \t");
	}

	@Test
	public void persistReturn() throws JFleetException, SQLException, IOException {
		testWithString("A text with a return \n char \n");
		testWithString("A text with a return \n char \n\r");
	}

	@Test
	public void persistWithEscapeChar() throws JFleetException, SQLException, IOException {
		testWithString("A text with \\ a escape \\");
	}

	@Test
	public void persistMixChars() throws JFleetException, SQLException, IOException {
		testWithString("A text all \\ types \t of \n escape chars\n\r");
	}
}
