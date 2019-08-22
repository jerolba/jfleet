package org.jfleet.mssql;

import static java.util.stream.Collectors.toSet;
import static java.util.stream.IntStream.range;

import java.sql.Types;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.jfleet.ColumnInfo;
import org.jfleet.EntityFieldType.FieldTypeEnum;
import org.jfleet.EntityInfo;

import com.microsoft.sqlserver.jdbc.ISQLServerBulkRecord;
import com.microsoft.sqlserver.jdbc.SQLServerException;

public class JFleetSQLServerBulkRecord implements ISQLServerBulkRecord {

    private static final long serialVersionUID = 7107797683112745162L;
   
    private static final EnumMap<FieldTypeEnum, Integer> fieldTypes = createTypes();
    
    private final EntityInfo entityInfo;
    private final List<ColumnInfo> columns;
    private final Iterator<?> iterator;
    
    private static EnumMap<FieldTypeEnum, Integer> createTypes() {
        EnumMap<FieldTypeEnum, Integer> fieldTypes = new EnumMap<>(FieldTypeEnum.class);
        fieldTypes.put(FieldTypeEnum.BOOLEAN, Types.BIT);
        fieldTypes.put(FieldTypeEnum.BYTE, Types.TINYINT); 
        fieldTypes.put(FieldTypeEnum.SHORT, Types.SMALLINT);
        fieldTypes.put(FieldTypeEnum.INT, Types.INTEGER);
        fieldTypes.put(FieldTypeEnum.LONG, Types.BIGINT);
        fieldTypes.put(FieldTypeEnum.CHAR, Types.CHAR);
        fieldTypes.put(FieldTypeEnum.FLOAT, Types.FLOAT);
        fieldTypes.put(FieldTypeEnum.DOUBLE, Types.DOUBLE);
        fieldTypes.put(FieldTypeEnum.STRING, Types.NVARCHAR);
        fieldTypes.put(FieldTypeEnum.DATE, Types.DATE);
        fieldTypes.put(FieldTypeEnum.TIME, Types.TIME);
        fieldTypes.put(FieldTypeEnum.TIMESTAMP, Types.TIMESTAMP);
        fieldTypes.put(FieldTypeEnum.BIGDECIMAL, Types.DECIMAL);
        fieldTypes.put(FieldTypeEnum.BIGINTEGER, Types.BIGINT);
        fieldTypes.put(FieldTypeEnum.LOCALDATE, Types.DATE); //??
        fieldTypes.put(FieldTypeEnum.LOCALTIME, Types.TIME); //??
        fieldTypes.put(FieldTypeEnum.LOCALDATETIME, Types.TIMESTAMP); //??
        fieldTypes.put(FieldTypeEnum.ENUMSTRING, Types.VARCHAR); 
        fieldTypes.put(FieldTypeEnum.ENUMORDINAL, Types.TINYINT);
        return fieldTypes;
    }
    
    public JFleetSQLServerBulkRecord(EntityInfo entityInfo, Stream<?> stream) {
        this.entityInfo = entityInfo;
        this.columns = entityInfo.getColumns();
        this.iterator = stream.iterator();
    }
    

    @Override
    public Set<Integer> getColumnOrdinals() {
        return range(1, columns.size() + 1).boxed().collect(toSet());
    }

    @Override
    public String getColumnName(int column) {
        return columns.get(column - 1).getColumnName();
    }

    @Override
    public int getColumnType(int column) {
        return fieldTypes.get(columns.get(column - 1).getFieldType().getFieldType());
    }

    @Override
    public int getPrecision(int column) {
        return 0;
    }

    @Override
    public int getScale(int column) {
        return 0;
    }

    @Override
    public boolean isAutoIncrement(int column) {
        return false;
    }

    @Override
    public Object[] getRowData() throws SQLServerException {
        Object next = iterator.next();
        Object[] row = new Object[columns.size()];
        for (int i = 0; i < columns.size(); i++) {
            ColumnInfo info = columns.get(i);
            row[i] = info.getAccessor().apply(next);
        }
        return row;
    }

    @Override
    public boolean next() throws SQLServerException {
        return iterator.hasNext();
    }

    @Override
    public void addColumnMetadata(int positionInFile, String name, int jdbcType, int precision, int scale,
            DateTimeFormatter dateTimeFormatter) throws SQLServerException {
        throw new UnsupportedOperationException();

    }

    @Override
    public void addColumnMetadata(int positionInFile, String name, int jdbcType, int precision, int scale)
            throws SQLServerException {
        throw new UnsupportedOperationException();

    }

    @Override
    public void setTimestampWithTimezoneFormat(String dateTimeFormat) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setTimestampWithTimezoneFormat(DateTimeFormatter dateTimeFormatter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setTimeWithTimezoneFormat(String timeFormat) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setTimeWithTimezoneFormat(DateTimeFormatter dateTimeFormatter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DateTimeFormatter getColumnDateTimeFormatter(int column) {
        throw new UnsupportedOperationException();
    }

}
