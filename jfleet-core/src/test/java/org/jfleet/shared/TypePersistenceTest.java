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
package org.jfleet.shared;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.stream.Stream;

import org.jfleet.BulkInsert;
import org.jfleet.parameterized.Backend;
import org.jfleet.parameterized.TestDBs;
import org.jfleet.shared.entities.EntityWithBasicTypes;
import org.jfleet.shared.entities.EnumForTest;
import org.jfleet.util.Database;
import org.jfleet.util.SqlUtil;

public class TypePersistenceTest {

    @TestDBs
    public void persistAllTypes(Database database) throws Exception {
        EntityWithBasicTypes entity = new EntityWithBasicTypes();
        entity.setBooleanObject(true);
        entity.setByteObject((byte) 42);
        entity.setCharObject('A');
        entity.setDoubleObject(1.2);
        entity.setFloatObject(1234567.89f);
        entity.setIntObject(1024);
        entity.setLongObject(12345678L);
        entity.setShortObject((short) 12345);
        entity.setBigDecimal(new BigDecimal("1234567.89"));
        entity.setBigInteger(new BigInteger("1234567890"));
        entity.setString("some string");
        entity.setEnumOrdinal(EnumForTest.one);
        entity.setEnumString(EnumForTest.four);
        entity.setBooleanPrimitive(false);
        entity.setBytePrimitive((byte) 54);
        entity.setCharPrimitive('a');
        entity.setDoublePrimitive(1.229283);
        entity.setFloatPrimitive(1234567.897654f);
        entity.setIntPrimitive(4096);
        entity.setLongPrimitive(123456789L);
        entity.setShortPrimitive((short) 1234);

        BulkInsert<EntityWithBasicTypes> insert = database.getBulkInsert(EntityWithBasicTypes.class);
        try (Connection conn = database.getConnection()) {
            SqlUtil.createTableForEntity(conn, EntityWithBasicTypes.class);
            insert.insertAll(conn, Stream.of(entity));

            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery("SELECT booleanObject, byteObject, charObject,"
                        + " doubleObject, floatObject, intObject, longObject, shortObject, string,"
                        + " bigDecimal, bigInteger, enumOrdinal, enumString, booleanPrimitive, bytePrimitive,"
                        + " charPrimitive, doublePrimitive, floatPrimitive, intPrimitive, longPrimitive, shortPrimitive"
                        + " FROM table_with_basic_types")) {
                    assertTrue(rs.next());
                    assertEquals(true, rs.getBoolean("booleanObject"));
                    assertEquals(42, rs.getByte("byteObject"));
                    assertEquals("A", rs.getString("charObject"));
                    assertEquals(1.2, rs.getDouble("doubleObject"), 0.001);
                    assertEquals(1234567.89f, rs.getFloat("floatObject"), 0.001);
                    assertEquals(1024, rs.getInt("intObject"));
                    assertEquals(12345678L, rs.getLong("longObject"));
                    assertEquals(12345, rs.getShort("shortObject"));
                    assertEquals(new BigDecimal("1234567.89"), rs.getBigDecimal("bigDecimal"));
                    assertEquals(new BigInteger("1234567890").longValueExact(), rs.getInt("bigInteger"));
                    assertEquals(0, rs.getInt("enumOrdinal"));
                    assertEquals("four", rs.getString("enumString"));
                    assertEquals(false, rs.getBoolean("booleanPrimitive"));
                    assertEquals(54, rs.getByte("bytePrimitive"));
                    assertEquals("a", rs.getString("charPrimitive"));
                    assertEquals(1.229283, rs.getDouble("doublePrimitive"), 0.000001);
                    assertEquals(1234567.897654f, rs.getFloat("floatPrimitive"), 0.000001);
                    assertEquals(4096, rs.getInt("intPrimitive"));
                    assertEquals(123456789L, rs.getLong("longPrimitive"));
                    assertEquals(1234, rs.getShort("shortPrimitive"));
                }
            }
        }
    }

    @TestDBs
    public void persistNullValues(Database database) throws Exception {
        EntityWithBasicTypes entity = new EntityWithBasicTypes();
        entity.setBooleanObject(null);
        entity.setByteObject(null);
        entity.setCharObject(null);
        entity.setDoubleObject(null);
        entity.setFloatObject(null);
        entity.setIntObject(null);
        entity.setLongObject(null);
        entity.setShortObject(null);
        entity.setString(null);
        entity.setBigDecimal(null);
        entity.setBigInteger(null);
        entity.setEnumOrdinal(null);
        entity.setEnumString(null);

        entity.setBooleanPrimitive(false);
        entity.setBytePrimitive((byte) 0);
        // Postgres doesn't allow null char \0
        if (database.getType().getBackend() == Backend.Postgres) {
            entity.setCharPrimitive((char) 1);
        } else {
            entity.setCharPrimitive((char) 0);
        }
        entity.setDoublePrimitive(0.0);
        entity.setFloatPrimitive(0.0f);
        entity.setIntPrimitive(0);
        entity.setLongPrimitive(0L);
        entity.setShortPrimitive((short) 0);

        BulkInsert<EntityWithBasicTypes> insert = database.getBulkInsert(EntityWithBasicTypes.class);
        try (Connection conn = database.getConnection()) {
            SqlUtil.createTableForEntity(conn, EntityWithBasicTypes.class);
            insert.insertAll(conn, Stream.of(entity));

            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery("SELECT booleanObject, byteObject, charObject,"
                        + " doubleObject, floatObject, intObject, longObject, shortObject, string,"
                        + " bigDecimal, bigInteger, enumOrdinal, enumString, booleanPrimitive, bytePrimitive,"
                        + " charPrimitive, doublePrimitive, floatPrimitive, intPrimitive, longPrimitive, shortPrimitive"
                        + " FROM table_with_basic_types")) {
                    assertTrue(rs.next());
                    assertEquals(false, rs.getBoolean("booleanObject"));
                    assertTrue(rs.wasNull());
                    assertEquals(0, rs.getByte("byteObject"));
                    assertTrue(rs.wasNull());
                    assertEquals(null, rs.getString("charObject"));
                    assertTrue(rs.wasNull());
                    assertEquals(0, rs.getDouble("doubleObject"), 0.001);
                    assertTrue(rs.wasNull());
                    assertEquals(0, rs.getFloat("floatObject"), 0.001);
                    assertTrue(rs.wasNull());
                    assertEquals(0, rs.getInt("intObject"));
                    assertTrue(rs.wasNull());
                    assertEquals(0, rs.getLong("longObject"));
                    assertTrue(rs.wasNull());
                    assertEquals(0, rs.getShort("shortObject"));
                    assertTrue(rs.wasNull());
                    assertEquals(null, rs.getString("string"));
                    assertTrue(rs.wasNull());
                    assertEquals(null, rs.getBigDecimal("bigDecimal"));
                    assertTrue(rs.wasNull());
                    assertEquals(0, rs.getInt("enumOrdinal"));
                    assertTrue(rs.wasNull());
                    assertEquals(null, rs.getString("enumString"));
                    assertTrue(rs.wasNull());

                    assertEquals(false, rs.getBoolean("booleanPrimitive"));
                    assertFalse(rs.wasNull());
                    assertEquals(0, rs.getByte("bytePrimitive"));
                    assertFalse(rs.wasNull());
                    if (database.getType().getBackend() != Backend.Postgres) {
                        assertEquals(String.valueOf((char) 0), rs.getString("charPrimitive"));
                    }
                    assertFalse(rs.wasNull());
                    assertEquals(0, rs.getDouble("doublePrimitive"), 0.001);
                    assertFalse(rs.wasNull());
                    assertEquals(0, rs.getFloat("floatPrimitive"), 0.001);
                    assertFalse(rs.wasNull());
                    assertEquals(0, rs.getInt("intPrimitive"));
                    assertFalse(rs.wasNull());
                    assertEquals(0, rs.getLong("longPrimitive"));
                    assertFalse(rs.wasNull());
                    assertEquals(0, rs.getShort("shortPrimitive"));
                    assertFalse(rs.wasNull());
                }
            }
        }
    }

}
