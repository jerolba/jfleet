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
package org.jfleet.csv;

import static org.jfleet.csv.CsvTestHelper.createBuilderForSomeEntity;
import static org.jfleet.csv.CsvTestHelper.writeCsvToString;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import java.io.IOException;
import java.util.Collections;

import org.jfleet.EntityFieldType.FieldTypeEnum;
import org.jfleet.EntityInfoBuilder;
import org.jfleet.csv.CsvConfiguration.Builder;
import org.junit.jupiter.api.Test;

public class HeaderTest {

    private static String ls = System.lineSeparator();

    @Entity
    public class AnnotatedEntity {

        @Column(name = "title")
        private String name;
        @Column(name = "duration")
        private int age;
        @Embedded
        private Address address;

        public AnnotatedEntity(String name, int age, Address address) {
            this.name = name;
            this.age = age;
            this.address = address;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public Address getAddress() {
            return address;
        }
    }

    @Embeddable
    public class Address {

        private String street;

        private String city;

        public Address(String street, String city) {
            this.street = street;
            this.city = city;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

    }

    @Test
    public void emptyCollectionOnlyGeneratesHeader() throws IOException {
        CsvConfiguration<SomeEntity> config = new CsvConfiguration<>(createBuilderForSomeEntity().build());
        String result = writeCsvToString(config, Collections.emptyList());
        assertEquals("name,age" + ls, result);
    }

    @Test
    public void emptyCollectionWithoutHeaderIsEmpty() throws IOException {
        Builder<SomeEntity> config = new Builder<>(createBuilderForSomeEntity().build());
        config.header(false);
        String result = writeCsvToString(config.build(), Collections.emptyList());
        assertEquals("", result);
    }

    @Test
    public void elementIsAfterHeader() throws IOException {
        CsvConfiguration<SomeEntity> config = new CsvConfiguration<>(createBuilderForSomeEntity().build());
        String result = writeCsvToString(config, new SomeEntity("John", 10));
        assertEquals("name,age" + ls + "John,10" + ls, result);
    }

    @Test
    public void elementWithoutHeaderIsAlone() throws IOException {
        Builder<SomeEntity> config = new Builder<>(createBuilderForSomeEntity().build());
        config.header(false);
        String result = writeCsvToString(config.build(), new SomeEntity("John", 10));
        assertEquals("John,10" + ls, result);
    }

    @Test
    public void columnsMantainsDeclaredOrder() throws IOException {
        EntityInfoBuilder<SomeEntity> entityBuilder = new EntityInfoBuilder<>(SomeEntity.class);
        entityBuilder.addColumn("age", FieldTypeEnum.INT, SomeEntity::getAge);
        entityBuilder.addColumn("name", FieldTypeEnum.STRING, SomeEntity::getName);
        CsvConfiguration<SomeEntity> config = new CsvConfiguration<>(entityBuilder.build());
        String result = writeCsvToString(config, new SomeEntity("John", 10));
        assertEquals("age,name" + ls + "10,John" + ls, result);
    }

    @Test
    public void columnsMantainsDeclaredOrderInClass() throws IOException {
        CsvConfiguration<AnnotatedEntity> config = new CsvConfiguration<>(AnnotatedEntity.class);
        Address address = new Address("221B Baker Street", "London");
        String result = writeCsvToString(config, new AnnotatedEntity("Sherlock", 63, address));
        assertEquals("title,duration,street,city" + ls + "Sherlock,63,221B Baker Street,London" + ls, result);
    }

}
