package org.jfleet.shared;

import java.util.Arrays;
import java.util.Collection;

import org.jfleet.parameterized.Database;
import org.jfleet.parameterized.JdbcPostgresDatabase;
import org.jfleet.parameterized.MySqlDatabase;
import org.jfleet.parameterized.PostgresDatabase;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public abstract class AllDatabasesBaseTest {

    @Parameters(name = "Database {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
               {"MySql", new MySqlDatabase()},
               {"Postgres", new PostgresDatabase()},
               {"Jdbc", new JdbcPostgresDatabase()}
               });
    }

    @Parameter(0)
    public String databaseName;

    @Parameter(1)
    public Database database;

}
