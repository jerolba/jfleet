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

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.jfleet.BulkInsert;
import org.jfleet.parameterized.TestDBs;
import org.jfleet.util.Database;
import org.jfleet.util.SqlUtil;

public class ManyToOnePersistenceTest {

    @Entity
    public class Product {

        @Id
        private Long id;
        private String name;

        public Product(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

    }

    @Entity
    @Table(name = "tesla")
    public class Foo {

        @Id
        private Long id;
        private String reference;
        @ManyToOne
        private Product product;

        public Foo(Long id, String reference, Product product) {
            this.id = id;
            this.reference = reference;
            this.product = product;
        }

        public Long getId() {
            return id;
        }

        public String getReference() {
            return reference;
        }

        public Product getProduct() {
            return product;
        }

    }

    @Entity
    @Table(name = "textil")
    public class Bar {

        @Id
        private Long id;
        private double price;
        @ManyToOne
        @JoinColumn(name = "alternate_id")
        private Product product;

        public Bar(Long id, double price, Product product) {
            this.id = id;
            this.price = price;
            this.product = product;
        }

        public Long getId() {
            return id;
        }

        public double getPrice() {
            return price;
        }

        public Product getProduct() {
            return product;
        }

    }

    @TestDBs
    public void canPersistAnEntityWithManyToOne(Database database) throws Exception {
        Product p1 = new Product(1L, "Tesla X");
        Foo f1 = new Foo(1L, "85H", p1);
        Foo f2 = new Foo(2L, "100H", p1);
        Product p2 = new Product(2L, "Tesla S");
        Foo f3 = new Foo(3L, "120H", p2);

        List<Foo> foos = asList(f1, f2, f3);

        BulkInsert<Foo> insert = database.getBulkInsert(Foo.class);
        try (Connection conn = database.getConnection()) {
            SqlUtil.createTableForEntity(conn, Foo.class);
            insert.insertAll(conn, foos);

            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery("SELECT id, reference, product_id FROM tesla ORDER BY id ASC")) {
                    for (Foo f : foos) {
                        assertTrue(rs.next());
                        assertEquals(f.getId().longValue(), rs.getLong("id"));
                        assertEquals(f.getReference(), rs.getString("reference"));
                        assertEquals(f.getProduct().getId().longValue(), rs.getLong("product_id"));
                    }
                }
            }
        }
    }

    @TestDBs
    public void canPersistAnEntityWithJoinColumn(Database database) throws Exception {
        Product p1 = new Product(1L, "Gocco");
        Bar f1 = new Bar(1L, 10.95, p1);
        Bar f2 = new Bar(2L, 14.95, p1);
        Product p2 = new Product(2L, "Amichi");
        Bar f3 = new Bar(3L, 4.95, p2);

        List<Bar> bars = asList(f1, f2, f3);

        BulkInsert<Bar> insert = database.getBulkInsert(Bar.class);
        try (Connection conn = database.getConnection()) {
            SqlUtil.createTableForEntity(conn, Bar.class);
            insert.insertAll(conn, bars);

            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery("SELECT id, price, alternate_id FROM textil ORDER BY id ASC")) {
                    for (Bar f : bars) {
                        assertTrue(rs.next());
                        assertEquals(f.getId().longValue(), rs.getLong("id"));
                        assertEquals(f.getPrice(), rs.getDouble("price"), 0.0001);
                        assertEquals(f.getProduct().getId().longValue(), rs.getLong("alternate_id"));
                    }
                }
            }
        }
    }

    /*
     * If referenced entity has no id assigned, JFleet doesn't persist the entity (as JPA does) and doesn't assign
     * the Id. The main entity then is persisted without the id of the referenced entity.
     * JFleet user must persist or load any entity referenced, or assign manually an id.
     */
    @TestDBs
    public void canPersistAnEntityWithManyToOneNullId(Database database) throws Exception {
        Product p1 = new Product(1L, "Gocco");
        Bar f1 = new Bar(1L, 10.95, p1);
        Product p2 = new Product(null, "Amichi");
        Bar f2 = new Bar(2L, 14.95, p2);

        BulkInsert<Bar> insert = database.getBulkInsert(Bar.class);
        try (Connection conn = database.getConnection()) {
            SqlUtil.createTableForEntity(conn, Bar.class);
            insert.insertAll(conn, asList(f1, f2));

            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery("SELECT id, price, alternate_id FROM textil ORDER BY id ASC")) {
                    assertTrue(rs.next());
                    assertEquals(f1.getId().longValue(), rs.getLong("id"));
                    assertEquals(f1.getPrice(), rs.getDouble("price"), 0.0001);
                    assertEquals(f1.getProduct().getId().longValue(), rs.getLong("alternate_id"));

                    assertTrue(rs.next());
                    assertEquals(f2.getId().longValue(), rs.getLong("id"));
                    assertEquals(f2.getPrice(), rs.getDouble("price"), 0.0001);
                    rs.getLong("alternate_id");
                    assertTrue(rs.wasNull());
                }
            }
        }
    }

    @TestDBs
    public void canPersistAnEntityWithManyToOneNullReference(Database database) throws Exception {
        Product p1 = new Product(1L, "Gocco");
        Bar f1 = new Bar(1L, 10.95, p1);
        Bar f2 = new Bar(2L, 14.95, null);

        BulkInsert<Bar> insert = database.getBulkInsert(Bar.class);
        try (Connection conn = database.getConnection()) {
            SqlUtil.createTableForEntity(conn, Bar.class);
            insert.insertAll(conn, asList(f1, f2));

            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery("SELECT id, price, alternate_id FROM textil ORDER BY id ASC")) {
                    assertTrue(rs.next());
                    assertEquals(f1.getId().longValue(), rs.getLong("id"));
                    assertEquals(f1.getPrice(), rs.getDouble("price"), 0.0001);
                    assertEquals(f1.getProduct().getId().longValue(), rs.getLong("alternate_id"));

                    assertTrue(rs.next());
                    assertEquals(f2.getId().longValue(), rs.getLong("id"));
                    assertEquals(f2.getPrice(), rs.getDouble("price"), 0.0001);
                    rs.getLong("alternate_id");
                    assertTrue(rs.wasNull());
                }
            }
        }
    }

}
