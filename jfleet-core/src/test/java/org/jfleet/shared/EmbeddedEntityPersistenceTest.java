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
package org.jfleet.shared;

import org.jfleet.BulkInsert;
import org.jfleet.entities.Address;
import org.jfleet.entities.Company;
import org.jfleet.entities.Person;
import org.jfleet.parameterized.TestDBs;
import org.jfleet.util.Database;
import org.jfleet.util.SqlUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EmbeddedEntityPersistenceTest {

    @TestDBs
    public void canPersistAnEntityWithEmbeddedValues(Database database) throws Exception {
        Person p1 = new Person();
        p1.setId(1L);
        p1.setName("Sherlock Holmes");
        Address address1 = new Address();
        address1.setCity("London");
        address1.setStreet("221b Baker St");
        p1.setAddress(address1);

        Person p2 = new Person();
        p2.setId(2L);
        p2.setName("Inspector Lestrade");
        Address address2 = new Address();
        address2.setCity("London");
        address2.setStreet("4 Whitehall Place");
        p2.setAddress(address2);
        List<Person> persons = Arrays.asList(p1, p2);

        BulkInsert<Person> insert = database.getBulkInsert(Person.class);
        try (Connection conn = database.getConnection()) {
            SqlUtil.createTableForEntity(conn, Person.class);
            insert.insertAll(conn, persons);

            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery("SELECT id, name, city, street FROM person ORDER BY id ASC")) {
                    for (Person p : persons) {
                        assertTrue(rs.next());
                        assertEquals(p.getId().longValue(), rs.getLong("id"));
                        assertEquals(p.getName(), rs.getString("name"));
                        assertEquals(p.getAddress().getCity(), rs.getString("city"));
                        assertEquals(p.getAddress().getStreet(), rs.getString("street"));
                    }
                }
            }
        }
    }

    @TestDBs
    public void canPersistAnEntityWithMultipleEmbeddedValues(Database database) throws Exception {
        Company c1 = new Company();
        c1.setId(1L);
        c1.setName("Apple Inc");
        Address fiscalAddress1 = new Address();
        fiscalAddress1.setCity("Cupertino");
        fiscalAddress1.setStreet("1 Infinite Loop");
        c1.setFiscalAddress(fiscalAddress1);
        Address postalAddress1 = new Address();
        postalAddress1.setCity("Palo Alto");
        postalAddress1.setStreet("340 University Avenue");
        c1.setPostalAddress(postalAddress1);

        Company c2 = new Company();
        c2.setId(2L);
        c2.setName("Google Inc");
        Address fiscalAddress2 = new Address();
        fiscalAddress2.setCity("Mountain View");
        fiscalAddress2.setStreet("1600 Amphitheatre Parkway");
        c2.setFiscalAddress(fiscalAddress2);
        Address postalAddress2 = new Address();
        postalAddress2.setCity("Madrid");
        postalAddress2.setStreet("Plaza Pablo Ruiz Picasso, I");
        c2.setPostalAddress(postalAddress2);


        List<Company> companies = Arrays.asList(c1, c2);

        BulkInsert<Company> insert = database.getBulkInsert(Company.class);
        try (Connection conn = database.getConnection()) {
            SqlUtil.createTableForEntity(conn, Company.class);
            insert.insertAll(conn, companies);

            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery("SELECT id, name, fiscal_city, fiscal_street, "
                        + "postal_city, postal_street FROM company ORDER BY id ASC")) {
                    for (Company c : companies) {
                        assertTrue(rs.next());
                        assertEquals(c.getId().longValue(), rs.getLong("id"));
                        assertEquals(c.getName(), rs.getString("name"));
                        assertEquals(c.getFiscalAddress().getCity(), rs.getString("fiscal_city"));
                        assertEquals(c.getFiscalAddress().getStreet(), rs.getString("fiscal_street"));
                        assertEquals(c.getPostalAddress().getCity(), rs.getString("postal_city"));
                        assertEquals(c.getPostalAddress().getStreet(), rs.getString("postal_street"));
                    }
                }
            }
        }
    }

    @TestDBs
    public void canPersistAnEntityWithEmbeddedNullValue(Database database) throws Exception {
        Person p = new Person();
        p.setId(1L);
        p.setName("Sherlock Holmes");
        p.setAddress(null);

        List<Person> persons = Arrays.asList(p);

        BulkInsert<Person> insert = database.getBulkInsert(Person.class);
        try (Connection conn = database.getConnection()) {
            SqlUtil.createTableForEntity(conn, Person.class);
            insert.insertAll(conn, persons);

            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery("SELECT id, name, city, street FROM person ORDER BY id ASC")) {
                    assertTrue(rs.next());
                    assertEquals(p.getId().longValue(), rs.getLong("id"));
                    assertEquals(p.getName(), rs.getString("name"));
                    rs.getString("city");
                    assertTrue(rs.wasNull());
                    rs.getString("street");
                    assertTrue(rs.wasNull());
                }
            }
        }
    }

    @TestDBs
    public void canPersistAnEntityWithOneEmbeddedNull(Database database) throws Exception {
        Company c = new Company();
        c.setId(1L);
        c.setName("Apple Inc");
        Address fiscalAddress = new Address();
        fiscalAddress.setCity("Cupertino");
        fiscalAddress.setStreet("1 Infinite Loop");
        c.setFiscalAddress(fiscalAddress);
        c.setPostalAddress(null);

        Stream<Company> stream = Stream.of(c);

        BulkInsert<Company> insert = database.getBulkInsert(Company.class);
        try (Connection conn = database.getConnection()) {
            SqlUtil.createTableForEntity(conn, Company.class);
            insert.insertAll(conn, stream);

            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery("SELECT id, name, fiscal_city, fiscal_street, "
                        + "postal_city, postal_street FROM company ORDER BY id ASC")) {
                    assertTrue(rs.next());
                    assertEquals(c.getId().longValue(), rs.getLong("id"));
                    assertEquals(c.getName(), rs.getString("name"));
                    assertEquals(c.getFiscalAddress().getCity(), rs.getString("fiscal_city"));
                    assertEquals(c.getFiscalAddress().getStreet(), rs.getString("fiscal_street"));
                    rs.getString("postal_city");
                    assertTrue(rs.wasNull());
                    rs.getString("postal_street");
                    assertTrue(rs.wasNull());
                }
            }
        }
    }
}
