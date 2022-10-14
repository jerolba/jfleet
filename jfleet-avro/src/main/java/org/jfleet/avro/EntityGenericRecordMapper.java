package org.jfleet.avro;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.jfleet.ColumnInfo;
import org.jfleet.EntityFieldType.FieldTypeEnum;
import org.jfleet.EntityInfo;

public class EntityGenericRecordMapper<T> {

  private final Schema schema;
  private final EntityInfo entityInfo;

  public EntityGenericRecordMapper(Schema schema, EntityInfo entityInfo) {
    this.schema = schema;
    this.entityInfo = entityInfo;
  }

  public GenericRecord buildAvroRecord(T entity) {
    GenericRecord genericRecord = new GenericData.Record(schema);
    for (ColumnInfo columnInfo : entityInfo.getColumns()) {
      Object value = columnInfo.getAccessor().apply(entity);
      if (value != null) {
        Object extractedValue = extractValue(columnInfo, value);
        genericRecord.put(columnInfo.getColumnName(), extractedValue);
      }
    }
    return genericRecord;
  }

  private Object extractValue(ColumnInfo columnInfo, Object value) {
    FieldTypeEnum fieldType = columnInfo.getFieldType().getFieldType();
    if (fieldType == FieldTypeEnum.BYTE) {
      value = ((Byte) value).intValue();
    } else if (fieldType == FieldTypeEnum.SHORT) {
      value = ((Short) value).intValue();
    } else if (fieldType == FieldTypeEnum.ENUMSTRING) {
      value = ((Enum<?>) value).name();
    } else if (fieldType == FieldTypeEnum.ENUMORDINAL) {
      value = ((Enum<?>) value).ordinal();
    }
    return value;
  }
}
