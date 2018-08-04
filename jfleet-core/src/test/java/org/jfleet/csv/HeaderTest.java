package org.jfleet.csv;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
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

        @Column(name="title")
        private String name;
        @Column(name="duration")
        private int age;

        public AnnotatedEntity(String name, int age) {
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
        String result = writeCsvToString(config, Arrays.asList(new AnnotatedEntity("John", 10)));
        assertEquals("title,duration\nJohn,10\n", result);
    }

    
}
