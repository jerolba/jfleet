package org.jfleet.csv;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.jfleet.EntityFieldType.FieldTypeEnum;
import org.jfleet.EntityInfoBuilder;
import org.jfleet.csv.CsvConfiguration.Builder;
import org.junit.Test;

public class CsvFormatTest {

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

    private Builder<SomeEntity> config = new Builder<>(createBuilderForSomeEntity().build());

    @Test
    public void fieldSeparatorTest() throws IOException {
        config.fieldSeparator(';');
        String result = writeCsvToString(config.build(), asList(new SomeEntity("John", 10)));
        assertEquals("name;age\nJohn;10\n", result);
    }
    
    @Test
    public void escapeFieldSeparatorTest() throws IOException {
        config.fieldSeparator(';').textDelimiter('\'');
        String result = writeCsvToString(config.build(), asList(new SomeEntity("John;Smith", 10)));
        assertEquals("name;age\n'John;Smith';10\n", result);
    }

    @Test
    public void optionalTextDelimiterNotUsedTest() throws IOException {
        config.textDelimiter('$');
        String result = writeCsvToString(config.build(), asList(new SomeEntity("John", 10)));
        assertEquals("name,age\nJohn,10\n", result);
    }

    @Test
    public void optionalTextDelimiterUsedTest() throws IOException {
        config.textDelimiter('"');
        String result = writeCsvToString(config.build(), asList(new SomeEntity("John \"Smith\"", 10)));
        assertEquals("name,age\n\"John \"\"Smith\"\"\",10\n", result);
    }

    @Test
    public void mandatoryTextDelimiterUsedTest() throws IOException {
        config.textDelimiter('\'').alwaysDelimitText(true);
        String result = writeCsvToString(config.build(),
                asList(new SomeEntity("John 'Smith'", 10), new SomeEntity("Amanda", 20)));
        assertEquals("'name','age'\n'John ''Smith''','10'\n'Amanda','20'\n", result);
    }
    
    @Test
    public void escapeLineDelimiterTest() throws IOException {
        config.textDelimiter('\'');
        String result = writeCsvToString(config.build(), asList(new SomeEntity("John \nSmith", 10)));
        assertEquals("name,age\n'John \nSmith',10\n", result);
    }
    

    private <T> String writeCsvToString(CsvConfiguration<T> config, List<T> collection) throws IOException {
        CsvWriter<T> writer = new CsvWriter<>(config);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writer.writeAll(baos, collection);
        return baos.toString(Charset.defaultCharset().name());
    }

    private EntityInfoBuilder<SomeEntity> createBuilderForSomeEntity() {
        EntityInfoBuilder<SomeEntity> entityBuilder = new EntityInfoBuilder<>(SomeEntity.class, "");
        entityBuilder.addColumn("name", FieldTypeEnum.STRING, SomeEntity::getName);
        entityBuilder.addColumn("age", FieldTypeEnum.INT, SomeEntity::getAge);
        return entityBuilder;
    }

}
