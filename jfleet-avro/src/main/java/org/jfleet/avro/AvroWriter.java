package org.jfleet.avro;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;
import org.jfleet.EntityInfo;
import org.jfleet.inspection.JpaEntityInspector;

public class AvroWriter<T> {

  private final Schema schema;
  private final EntityInfo entityInfo;
  private final EntityGenericRecordMapper mapper;

  public AvroWriter(AvroConfiguration<T> avroConfiguration) {
    entityInfo = getEntityInfo(avroConfiguration);
    schema = new AvroSchemaBuilder(entityInfo).build();
    mapper = new EntityGenericRecordMapper(schema, entityInfo);
  }

  public void writeAll(OutputStream output, Collection<T> entities) throws IOException {
    writeAll(output, entities.stream());
  }

  public void writeAll(OutputStream output, Stream<T> entities) throws IOException {

    DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(schema);
    try (DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(datumWriter)) {
      dataFileWriter.create(schema, output);
      Iterator<T> iterator = entities.iterator();
      while (iterator.hasNext()) {
        dataFileWriter.append(mapper.buildAvroRecord(iterator.next()));
      }
    }
  }

  private static <T> EntityInfo getEntityInfo(AvroConfiguration<T> config) {
    EntityInfo configEntityInfo = config.getEntityInfo();
    if (configEntityInfo != null) {
      return configEntityInfo;
    }
    return new JpaEntityInspector(config.getClazz()).inspect();
  }

}
