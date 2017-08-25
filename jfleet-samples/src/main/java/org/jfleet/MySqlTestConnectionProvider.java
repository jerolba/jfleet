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
package org.jfleet;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.function.Supplier;

public class MySqlTestConnectionProvider implements Supplier<Connection> {

	private Properties prop;

	public MySqlTestConnectionProvider() throws IOException {
		this("mysql-test.properties");
	}

	public MySqlTestConnectionProvider(String propertiesName) throws IOException {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(propertiesName);
		Properties p = new Properties();
		p.load(is);
		prop = p;
	}

	@Override
	public Connection get() {
		try {
			Class.forName(prop.getProperty("driver")).newInstance();
			Connection conn = DriverManager.getConnection(prop.getProperty("urlConnection"), prop.getProperty("user"), prop.getProperty("password"));
			return conn;
		} catch (ClassNotFoundException | IllegalAccessException | InstantiationException ex) {
			throw new RuntimeException("Can not instantiate a MySql driver", ex);
		} catch (SQLException ex) {
			throw new RuntimeException("Can not connect to database", ex);
		}
	}

}
