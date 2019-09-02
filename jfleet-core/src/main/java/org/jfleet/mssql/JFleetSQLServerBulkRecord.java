/**
 * Copyright 2017 Jerónimo López Bezanilla
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jfleet.mssql;

import static java.util.stream.Collectors.toSet;
import static java.util.stream.IntStream.range;

import java.math.BigInteger;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
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
    
    private static final EnumMap<FieldTypeEnum, Integer> FIELD_TYPES = createTypes();

    private final SimpleDateFormat dateAndTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    private final DateTimeFormatter localDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter localTimeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final DateTimeFormatter localDateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final List<ColumnInfo> columns;
    private final Iterator<?> iterator;

    public JFleetSQLServerBulkRecord(EntityInfo entityInfo, Stream<?> stream) {
        this.columns = entityInfo.getNotIdentityColumns();
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
        return FIELD_TYPES.get(columns.get(column - 1).getFieldType().getFieldType());
    }

    @Override
    public int getPrecision(int column) {
        if (columns.get(column - 1).getFieldType().getFieldType() == FieldTypeEnum.BIGDECIMAL) {
            return 18;
        }
        return 0;
    }

    @Override
    public int getScale(int column) {
        if (columns.get(column - 1).getFieldType().getFieldType() == FieldTypeEnum.BIGDECIMAL) {
            return 2;
        }
        return 0;
    }

    @Override
    public boolean isAutoIncrement(int column) {
        return columns.get(column - 1).getFieldType().isIdentityId();
    }

    @Override
    public Object[] getRowData() throws SQLServerException {
        Object next = iterator.next();
        Object[] row = new Object[columns.size()];
        for (int i = 0; i < columns.size(); i++) {
            ColumnInfo info = columns.get(i);
            Object value = info.getAccessor().apply(next);
            if (value != null) {
                FieldTypeEnum fieldType = info.getFieldType().getFieldType();
                if (fieldType == FieldTypeEnum.TIMESTAMP) {
                    value = dateAndTimeFormat.format((Date) value);
                } else if (fieldType == FieldTypeEnum.DATE) {
                    value = dateFormat.format((Date) value);
                } else if (fieldType == FieldTypeEnum.TIME) {
                    value = timeFormat.format((Date) value);
                } else if (fieldType == FieldTypeEnum.LOCALDATE) {
                    value = localDateFormat.format((LocalDate) value);
                } else if (fieldType == FieldTypeEnum.LOCALTIME) {
                    value = localTimeFormat.format((LocalTime) value);
                } else if (fieldType == FieldTypeEnum.LOCALDATETIME) {
                    value = localDateTimeFormat.format((LocalDateTime) value);
                } else if (fieldType == FieldTypeEnum.ENUMORDINAL) {
                    value = ((Enum<?>) value).ordinal();
                // TODO: We lose information, change to Exact or suggest to
                // use a varchar or byte[] column
                // https://stackoverflow.com/questions/35883725/mapping-between-bigint-in-sql-with-biginteger-in-java
                } else if (fieldType == FieldTypeEnum.BIGINTEGER) {
                    value = ((BigInteger) value).longValue();
                }
            }
            row[i] = value;
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
        fieldTypes.put(FieldTypeEnum.DATE, Types.VARCHAR);
        fieldTypes.put(FieldTypeEnum.TIME, Types.VARCHAR);
        fieldTypes.put(FieldTypeEnum.TIMESTAMP, Types.VARCHAR);
        fieldTypes.put(FieldTypeEnum.BIGDECIMAL, Types.DECIMAL);
        fieldTypes.put(FieldTypeEnum.BIGINTEGER, Types.BIGINT);
        fieldTypes.put(FieldTypeEnum.LOCALDATE, Types.VARCHAR);
        fieldTypes.put(FieldTypeEnum.LOCALTIME, Types.VARCHAR);
        fieldTypes.put(FieldTypeEnum.LOCALDATETIME, Types.VARCHAR);
        fieldTypes.put(FieldTypeEnum.ENUMSTRING, Types.VARCHAR);
        fieldTypes.put(FieldTypeEnum.ENUMORDINAL, Types.TINYINT);
        return fieldTypes;
    }
}
