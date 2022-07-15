package org.jfleet.avro;

import org.jfleet.EntityFieldType;
import org.jfleet.EntityInfo;
import org.jfleet.EntityInfoBuilder;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AvroWriterTest {
    @Test
    void shouldConvertEntityInfoToAvro() throws IOException {
        EntityInfo entityInfo = new EntityInfoBuilder<>(TestEntity.class)
                .addColumn("foo", EntityFieldType.FieldTypeEnum.STRING, TestEntity::getFoo)
                .addColumn("bar", EntityFieldType.FieldTypeEnum.STRING, TestEntity::getBar)
                .build();
        
        AvroConfiguration avroConfiguration = new AvroConfiguration(entityInfo);
        AvroWriter avroWriter = new AvroWriter<TestEntity>(avroConfiguration);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        TestEntity testEntity = new TestEntity("foo", "bar");
        avroWriter.writeAll(outputStream, List.of(testEntity));

        assertNotNull(avroWriter);
        assertTrue(outputStream.size() > 0);
    }
}
