/**
 * Copyright 2022 Jerónimo López Bezanilla
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

import org.jfleet.entities.City;
import org.jfleet.entities.Employee;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TransactionPolicyTestHelper {

    private static City city1 = new City(1, "Madrid");
    private static City city2 = new City(2, "Barcelona");
    private static City city3 = new City(3, "Bilbao");

    public static void setupDatabase(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS employee");
            stmt.execute("DROP TABLE IF EXISTS city");
            stmt.execute("CREATE TABLE city (id INT NOT NULL, "
                    + "name VARCHAR(64) NOT NULL, PRIMARY KEY (id))");

            stmt.executeUpdate("INSERT INTO city VALUES (1,'Madrid')");
            stmt.executeUpdate("INSERT INTO city VALUES (2,'Barcelona')");

            stmt.execute("CREATE TABLE employee (id INT NOT NULL, "
                    + "name VARCHAR(255) NOT NULL, city INT,  PRIMARY KEY (id),"
                    + "FOREIGN KEY (city) REFERENCES city(id))");
        }
    }

    public static Stream<Employee> employeesWithOutErrors() {
        return Stream.of(
                new Employee(1, "John", city1),
                new Employee(2, "Charles", city2),
                new Employee(3, "Peter", city1),
                new Employee(4, "John", city2),
                new Employee(5, "Donald", city1),
                new Employee(6, "Alex", city1),
                new Employee(7, "David", city1));
    }

    public static Stream<Employee> employeesWithConstraintError() {
        return employeesWithForeignKeyError();
    }

    public static Stream<Employee> employeesWithForeignKeyError() {
        return Stream.of(
                new Employee(1, "John", city1),
                new Employee(2, "Charles", city2),
                new Employee(3, "Peter", city1),
                new Employee(4, "John", city3),    //<-- no FK city in DB
                new Employee(5, "Donald", city1),
                new Employee(6, "Alex", city2),
                new Employee(7, "David", city1));
    }

    public static Stream<Employee> employeesWithUniqueError() {
        return Stream.of(
                new Employee(1, "John", city1),
                new Employee(2, "Charles", city2),
                new Employee(3, "Peter", city1),
                new Employee(4, "John", city2),
                new Employee(1, "Donald", city1));   //<-- Duplicated PK in DB
    }

    public static Stream<Employee> employeesWithMultipleConstraintsErrors() {
        return Stream.of(
            new Employee(1, "John", city1),
            new Employee(2, "Charles", city2),
            new Employee(3, "Peter", city1),
            new Employee(4, "John", city3),    //<-- no FK city in DB
            new Employee(5, "Donald", city1),
            new Employee(6, "Alex", city1),
            new Employee(7, "David", city1),
            new Employee(1, "Albert", city2)); //<-- Duplicated PK in DB
    }

    public static long numberOfRowsInEmployeeTable(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            try (ResultSet rs = stmt.executeQuery("SELECT count(*) FROM employee")) {
                assertTrue(rs.next());
                return rs.getLong(1);
            }
        }
    }

}
