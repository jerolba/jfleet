package org.jfleet.avro;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.jfleet.ColumnInfo;
import org.jfleet.EntityInfo;

public class AvroSchemaBuilder {
    private EntityInfo entityInfo;

    public AvroSchemaBuilder(EntityInfo entityInfo) {
        this.entityInfo = entityInfo;
    }

    public Schema build() {
        SchemaBuilder.FieldAssembler<Schema> fields = SchemaBuilder.record(entityInfo.getEntityClass().getName())
                .namespace(entityInfo.getEntityClass().getPackage().getName()).fields();

        for (ColumnInfo columnInfo : entityInfo.getColumns()) {
            fields = getFieldSchema(columnInfo, fields);
        }
        return fields.endRecord();
    }

    private SchemaBuilder.FieldAssembler<Schema> getFieldSchema(ColumnInfo columnInfo, SchemaBuilder.FieldAssembler<Schema> fields) {
        SchemaBuilder.FieldTypeBuilder<Schema> beginType = fields.name(columnInfo.getColumnName()).type();
        boolean isPrimitive = columnInfo.getFieldType().isPrimitive();

        switch (columnInfo.getFieldType().getFieldType()) {
            case STRING:
            case ENUMSTRING:
                return buildAsString(beginType);
            case ENUMORDINAL:
                return buildAsInteger(beginType, false);
            case INT:
            case SHORT:
            case BYTE:
                return buildAsInteger(beginType, isPrimitive);
            case DOUBLE:
                return buildAsDouble(beginType, isPrimitive);
            case LONG:
                return buildAsLong(beginType, isPrimitive);
            case FLOAT:
                return buildAsFloat(beginType, isPrimitive);
            case BOOLEAN:
                return buildAsBoolean(beginType, isPrimitive);
            default:
                throw new UnsupportedTypeException(String.format("Unsupported type: %s", columnInfo.getFieldType().getFieldType()));
        }

    }

    private SchemaBuilder.FieldAssembler<Schema> buildAsString(SchemaBuilder.FieldTypeBuilder<Schema> type) {
        return type.unionOf().stringType().and().nullType().endUnion().noDefault();
    }

    private SchemaBuilder.FieldAssembler<Schema> buildAsBoolean(SchemaBuilder.FieldTypeBuilder<Schema> type, boolean isPrimitive) {
        if (isPrimitive) {
            return type.booleanType().noDefault();
        }
        return type.unionOf().booleanType().and().nullType().endUnion().noDefault();
    }

    private SchemaBuilder.FieldAssembler<Schema> buildAsFloat(SchemaBuilder.FieldTypeBuilder<Schema> type, boolean isPrimitive) {
        if (isPrimitive) {
            return type.floatType().noDefault();
        }
        return type.unionOf().floatType().and().nullType().endUnion().noDefault();
    }

    private SchemaBuilder.FieldAssembler<Schema> buildAsLong(SchemaBuilder.FieldTypeBuilder<Schema> type, boolean isPrimitive) {
        if (isPrimitive) {
            return type.longType().noDefault();
        }
        return type.unionOf().longType().and().nullType().endUnion().noDefault();
    }

    private SchemaBuilder.FieldAssembler<Schema> buildAsInteger(SchemaBuilder.FieldTypeBuilder<Schema> type, boolean isPrimitive) {
        if (isPrimitive) {
            return type.intType().noDefault();
        }
        return type.unionOf().intType().and().nullType().endUnion().noDefault();
    }

    private SchemaBuilder.FieldAssembler<Schema> buildAsDouble(SchemaBuilder.FieldTypeBuilder<Schema> type, boolean isPrimitive) {
        if (isPrimitive) {
            return type.doubleType().noDefault();
        }
        return type.unionOf().doubleType().and().nullType().endUnion().noDefault();
    }
}
