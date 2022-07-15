package org.jfleet.avro;

import org.jfleet.EntityFieldType;
import org.jfleet.EntityInfo;
import org.jfleet.EntityInfoBuilder;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AvroWriterTest {
    @Test
    void shouldConvertEntityInfoWithStringTypesToAvro() throws IOException {
        EntityInfo entityInfo = new EntityInfoBuilder<>(TestEntity.class)
                .addColumn("foo", EntityFieldType.FieldTypeEnum.STRING, TestEntity::getFoo)
                .build();

        AvroConfiguration avroConfiguration = new AvroConfiguration(entityInfo);
        AvroWriter<TestEntity> avroWriter = new AvroWriter<>(avroConfiguration);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        TestEntity testEntity = new TestEntity("foo", null, null);
        avroWriter.writeAll(outputStream, Arrays.asList(testEntity));

        assertNotNull(avroWriter);
        assertTrue(outputStream.size() > 0);
    }


}
