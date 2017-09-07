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
package org.jfleet.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.function.Supplier;

import javax.sql.DataSource;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class MySqlTestDatasourceProvider implements Supplier<DataSource> {

    private Properties prop;

    public MySqlTestDatasourceProvider(String propertiesName) throws IOException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(propertiesName);
        Properties p = new Properties();
        p.load(is);
        prop = p;
    }

    public MySqlTestDatasourceProvider() throws IOException {
        this("mysql-test.properties");
    }

    @Override
    public DataSource get() {
        MysqlDataSource mysqlds = new MysqlDataSource();
        mysqlds.setUrl(prop.getProperty("urlConnection"));
        mysqlds.setUser(prop.getProperty("user"));
        mysqlds.setPassword(prop.getProperty("password"));
        return mysqlds;
    }

}
