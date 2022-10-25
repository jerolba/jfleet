package org.jfleet.parquet;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.jfleet.avro.AvroSchemaBuilder;
import org.jfleet.avro.EntityGenericRecordMapper;

public class ParquetWriter<T> {

  private ParquetConfiguration config;
  private EntityGenericRecordMapper mapper;
  private Schema schema;

  public ParquetWriter(ParquetConfiguration config) {
    this.config = config;
    schema = new AvroSchemaBuilder(this.config.getEntityInfo()).build();
    mapper = new EntityGenericRecordMapper(schema, this.config.getEntityInfo());
  }

  public void writeAll(Collection<T> collection) throws IOException {
    writeAll(collection.stream());
  }

  public void writeAll(Stream<T> stream) throws IOException {
    try (org.apache.parquet.hadoop.ParquetWriter<GenericRecord> writer = config
      .getWriterBuilder()
      .withSchema(schema)
      .build()) {

      Iterator<T> it = stream.iterator();
      while (it.hasNext()) {
        writer.write(mapper.buildAvroRecord(it.next()));
      }
    }
  }
}
