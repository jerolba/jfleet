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
