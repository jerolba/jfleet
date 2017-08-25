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
package org.jfleet.mysql.manual;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

import org.jfleet.mysql.MySqlTestConnectionProvider;
import org.junit.Test;

public class ConnectionTest {

	@Test
	public void canConnectToTestDB() throws SQLException, IOException {
		MySqlTestConnectionProvider connectionProvider = new MySqlTestConnectionProvider();
		try (java.sql.Connection conn = connectionProvider.get()){
			assertNotNull(conn);
		} 
	}

	@Test
	public void canExecuteLoadData() throws SQLException, IOException {
		int someValue = 12345;
		String otherValue = "foobar"; 
		
		MySqlTestConnectionProvider connectionProvider = new MySqlTestConnectionProvider();
		try (Connection conn = (Connection) connectionProvider.get()){
			conn.setAllowLoadLocalInfile(true);
			try(Statement stmt = (Statement) conn.createStatement()) {
				stmt.execute("CREATE TEMPORARY TABLE simple_table (some_column INTEGER, other_column VARCHAR(255))");
				
				String someData = someValue+"\t"+otherValue+"\t\n"; 
				stmt.setLocalInfileInputStream(new ByteArrayInputStream(someData.getBytes(Charset.forName("UTF-8"))));
				String sql ="LOAD DATA LOCAL INFILE '' INTO TABLE simple_table "
						+ "FIELDS TERMINATED BY '\t' ENCLOSED BY '' ESCAPED BY '\\\\' LINES TERMINATED BY '\n' STARTING BY ''"
						+ "(some_column, other_column)";
				stmt.execute(sql);
				
				try(ResultSet rs = stmt.executeQuery("SELECT some_column, other_column FROM simple_table")) {
	                assertTrue(rs.next());
	                assertEquals(rs.getInt(1), someValue);                 
	                assertEquals(rs.getString(2), otherValue);
	            }
			}
		}
	}
	
	@Test
	public void canExecuteLoadDataWithNullValues() throws SQLException, IOException {
		int someValue = 12345;
		String otherValue = "foobar"; 
		
		MySqlTestConnectionProvider connectionProvider = new MySqlTestConnectionProvider();
		try (Connection conn = (Connection) connectionProvider.get()){
			conn.setAllowLoadLocalInfile(true);
			try(Statement stmt = (Statement) conn.createStatement()) {
				stmt.execute("CREATE TEMPORARY TABLE simple_table (some_column INTEGER, other_column VARCHAR(255))");
				
				String row1 = someValue+"\t\\N\t\n";
				String row2 = "\\N\t"+otherValue+"\t\n"; 
				String someData = row1 + row2;
				stmt.setLocalInfileInputStream(new ByteArrayInputStream(someData.getBytes(Charset.forName("UTF-8"))));
				String sql ="LOAD DATA LOCAL INFILE '' INTO TABLE simple_table "
						+ "FIELDS TERMINATED BY '\t' ENCLOSED BY '' ESCAPED BY '\\\\' LINES TERMINATED BY '\n' STARTING BY ''"
						+ "(some_column, other_column)";
				stmt.execute(sql);
				
				try(ResultSet rs = stmt.executeQuery("SELECT some_column, other_column FROM simple_table")) {
	                assertTrue(rs.next());
	                assertEquals(someValue, rs.getInt(1));
	                assertEquals(null, rs.getString(2));
	                assertTrue(rs.next());
	                assertEquals(0, rs.getInt(1));
	                assertTrue(rs.wasNull());
	                assertEquals(otherValue, rs.getString(2));
	            }
			}
		}
	}
}
