package org.jfleet.csv;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;

import org.jfleet.EntityFieldType.FieldTypeEnum;
import org.jfleet.EntityInfoBuilder;
import org.jfleet.csv.CsvConfiguration.Builder;
import org.junit.Test;

public class HeaderTest {

    public class SomeEntity {

        private String name;
        private int age;

        public SomeEntity(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

    }

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
        assertEquals("name,age\n", result);
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
        String result = writeCsvToString(config, Arrays.asList(new SomeEntity("John", 10)));
        assertEquals("name,age\nJohn,10\n", result);
    }

    @Test
    public void elementWithoutHeaderIsAlone() throws IOException {
        Builder<SomeEntity> config = new Builder<>(createBuilderForSomeEntity().build());
        config.header(false);
        String result = writeCsvToString(config.build(), Arrays.asList(new SomeEntity("John", 10)));
        assertEquals("John,10\n", result);
    }

    @Test
    public void columnsMantainsDeclaredOrder() throws IOException {
        EntityInfoBuilder<SomeEntity> entityBuilder = new EntityInfoBuilder<>(SomeEntity.class, "");
        entityBuilder.addColumn("age", FieldTypeEnum.INT, SomeEntity::getAge);
        entityBuilder.addColumn("name", FieldTypeEnum.STRING, SomeEntity::getName);
        CsvConfiguration<SomeEntity> config = new CsvConfiguration<>(entityBuilder.build());
        String result = writeCsvToString(config, Arrays.asList(new SomeEntity("John", 10)));
        assertEquals("age,name\n10,John\n", result);
    }

    @Test
    public void columnsMantainsDeclaredOrderInClass() throws IOException {
        CsvConfiguration<AnnotatedEntity> config = new CsvConfiguration<>(AnnotatedEntity.class);
        Address address = new Address("221B Baker Street", "London");
        String result = writeCsvToString(config, Arrays.asList(new AnnotatedEntity("Sherlock", 63, address)));
        assertEquals("title,duration,street,city\nSherlock,63,221B Baker Street,London\n", result);
    }

    private EntityInfoBuilder<SomeEntity> createBuilderForSomeEntity() {
        EntityInfoBuilder<SomeEntity> entityBuilder = new EntityInfoBuilder<>(SomeEntity.class, "");
        entityBuilder.addColumn("name", FieldTypeEnum.STRING, SomeEntity::getName);
        entityBuilder.addColumn("age", FieldTypeEnum.INT, SomeEntity::getAge);
        return entityBuilder;
    }

    private <T> String writeCsvToString(CsvConfiguration<T> config, List<T> collection) throws IOException {
        CsvWriter<T> writer = new CsvWriter<>(config);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writer.writeAll(baos, collection);
        return baos.toString(Charset.defaultCharset().name());
    }

}
