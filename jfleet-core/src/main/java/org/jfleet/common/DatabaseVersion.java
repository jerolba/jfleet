package org.jfleet.common;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseVersion {

    public enum Database {
        MySql, Postgres, MsSql
    }
    
    public static Database getVersion(Connection conn) throws SQLException {
        try {
            Class<?> mySql = Class.forName("com.mysql.jdbc.Connection");
            conn.unwrap(mySql);
            return Database.MySql;
        } catch (SQLException | ClassNotFoundException e) {
            try {
                Class<?> mySql = Class.forName("com.mysql.cj.jdbc.JdbcConnection");
                conn.unwrap(mySql);
                return Database.MySql;
            } catch (SQLException | ClassNotFoundException e1) {
                try {
                    Class<?> postgres = Class.forName("org.postgresql.jdbc.PgConnection");
                    conn.unwrap(postgres);
                    return Database.Postgres;
                } catch (SQLException | ClassNotFoundException e2) {
                    try {
                        Class<?> mssql = Class.forName("com.microsoft.sqlserver.jdbc.ISQLServerConnection");
                        conn.unwrap(mssql);
                        return Database.MsSql;
                    } catch (SQLException | ClassNotFoundException e3) {
                        throw new SQLException("Unrecognized Database");
                    }
                }
            }
        }
    }

}
