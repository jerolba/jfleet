/**
 * Copyright 2022 Jerónimo López Bezanilla
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
package org.jfleet.inspection;


import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.jfleet.ColumnInfo;
import org.jfleet.EntityFieldType.FieldTypeEnum;
import org.jfleet.EntityInfo;
import org.jfleet.EntityInfoBuilder;
import org.jfleet.inspection.JFleetEntityInspector.NoSuchFieldException;
import org.jfleet.metadata.JFleetEnumType;
import org.jfleet.metadata.JFleetEnumerated;
import org.jfleet.metadata.JFleetTemporal;
import org.jfleet.metadata.JFleetTemporalType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EntityInfoBuilderTest {

    public enum Numbers {
        one, two, three
    }

    public class SimpleEntity {

        private String name;
        private City city;
        private Date birthDay;
        private Numbers floor;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public City getCity() {
            return city;
        }

        public void setCity(City city) {
            this.city = city;
        }

        public Date getBirthDay() {
            return birthDay;
        }

        public void setBirthDay(Date birthDay) {
            this.birthDay = birthDay;
        }

        public Numbers getFloor() {
            return floor;
        }

        public void setFloor(Numbers floor) {
            this.floor = floor;
        }

    }

    public static class City {

        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    public class SimpleEntityEx extends SimpleEntity {

        private int height;
        @JFleetTemporal(JFleetTemporalType.TIMESTAMP)
        private Date created;
        @JFleetEnumerated(JFleetEnumType.ORDINAL)
        private Numbers order;

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public Date getCreated() {
            return created;
        }

        public void setCreated(Date created) {
            this.created = created;
        }

        public Numbers getOrder() {
            return order;
        }

        public void setOrder(Numbers order) {
            this.order = order;
        }

    }

    private City city;
    private SimpleEntity simple;
    private SimpleEntityEx simpleEx;

    @BeforeEach
    public void setup() {
        city = new City();
        city.setId(1);
        city.setName("Madrid");
        simple = new SimpleEntity();
        simple.setBirthDay(new Date());
        simple.setCity(city);
        simple.setFloor(Numbers.three);
        simple.setName("John");

        simpleEx = new SimpleEntityEx();
        simpleEx.setBirthDay(new Date());
        simpleEx.setCity(city);
        simpleEx.setCreated(new Date());
        simpleEx.setFloor(Numbers.two);
        simpleEx.setHeight(100);
        simpleEx.setName("Peter");
        simpleEx.setOrder(Numbers.one);
    }

    @Test
    public void testNonExistentField() {
        EntityInfoBuilder<SimpleEntity> builder = new EntityInfoBuilder<>(SimpleEntity.class, "simple_entity");
        assertThrows(NoSuchFieldException.class, () -> {
            builder.addField("noField", "someName");
        });
    }

    @Test
    public void testNonExistentFieldInHierarchy() {
        EntityInfoBuilder<SimpleEntityEx> builder = new EntityInfoBuilder<>(SimpleEntityEx.class, "simple_entity");
        assertThrows(NoSuchFieldException.class, () -> {
            builder.addField("noField", "someName");
        });
    }

    @Test
    public void testExistentField() {
        EntityInfoBuilder<SimpleEntity> builder = new EntityInfoBuilder<>(SimpleEntity.class, "simple_entity");
        builder.addField("name", "name");
        EntityInfoHelper entityInfo = new EntityInfoHelper(builder);

        ColumnInfo column = entityInfo.findColumn("name");
        assertEquals("name", column.getColumnName());
        assertEquals(FieldTypeEnum.STRING, column.getFieldType().getFieldType());
        assertFalse(column.getFieldType().isPrimitive());
        assertEquals(simple.getName(), column.getAccessor().apply(simple));
    }

    @Test
    public void testExistentFieldInHierarchy() {
        EntityInfoBuilder<SimpleEntityEx> builder = new EntityInfoBuilder<>(SimpleEntityEx.class, "simple_entity");
        builder.addField("height", "height");
        EntityInfoHelper entityInfo = new EntityInfoHelper(builder);

        ColumnInfo column = entityInfo.findColumn("height");
        assertEquals("height", column.getColumnName());
        assertEquals(FieldTypeEnum.INT, column.getFieldType().getFieldType());
        assertTrue(column.getFieldType().isPrimitive());
        assertEquals(simpleEx.getHeight(), column.getAccessor().apply(simpleEx));
    }

    @Test
    public void testExistentPathField() {
        EntityInfoBuilder<SimpleEntity> builder = new EntityInfoBuilder<>(SimpleEntity.class, "simple_entity");
        builder.addField("city.name", "city_name");
        EntityInfoHelper entityInfo = new EntityInfoHelper(builder);

        ColumnInfo column = entityInfo.findColumn("city_name");
        assertEquals("city_name", column.getColumnName());
        assertEquals(FieldTypeEnum.STRING, column.getFieldType().getFieldType());
        assertFalse(column.getFieldType().isPrimitive());
        assertEquals(city.getName(), column.getAccessor().apply(simple));
    }

    @Test
    public void testImplicitColumnName() {
        EntityInfoBuilder<SimpleEntity> builder = new EntityInfoBuilder<>(SimpleEntity.class, "simple_entity");
        builder.addField("birthDay");
        EntityInfoHelper entityInfo = new EntityInfoHelper(builder);

        ColumnInfo column = entityInfo.findColumn("birthday");
        assertEquals("birthday", column.getColumnName());
        assertEquals(simple.getBirthDay(), column.getAccessor().apply(simple));
    }

    @Test
    public void testAddMultipleFields() {
        EntityInfoBuilder<SimpleEntity> builder = new EntityInfoBuilder<>(SimpleEntity.class, "simple_entity");
        builder.addFields("name", "birthDay");
        EntityInfoHelper entityInfo = new EntityInfoHelper(builder);

        ColumnInfo columnName = entityInfo.findColumn("name");
        assertEquals("name", columnName.getColumnName());
        assertEquals(simple.getName(), columnName.getAccessor().apply(simple));

        ColumnInfo columnBirthday = entityInfo.findColumn("birthday");
        assertEquals("birthday", columnBirthday.getColumnName());
        assertEquals(simple.getBirthDay(), columnBirthday.getAccessor().apply(simple));
    }

    @Test
    public void testImplicitColumnNameComposed() {
        EntityInfoBuilder<SimpleEntity> builder = new EntityInfoBuilder<>(SimpleEntity.class, "simple_entity");
        builder.addField("city.name");
        EntityInfoHelper entityInfo = new EntityInfoHelper(builder);

        ColumnInfo column = entityInfo.findColumn("city_name");
        assertEquals("city_name", column.getColumnName());
        assertEquals(city.getName(), column.getAccessor().apply(simple));
    }

    @Test
    public void testExplicitEnumField() {
        EntityInfoBuilder<SimpleEntity> builder = new EntityInfoBuilder<>(SimpleEntity.class, "simple_entity");
        builder.addField("floor", "floor", FieldTypeEnum.ENUMSTRING);
        EntityInfoHelper entityInfo = new EntityInfoHelper(builder);

        ColumnInfo column = entityInfo.findColumn("floor");
        assertEquals("floor", column.getColumnName());
        assertEquals(FieldTypeEnum.ENUMSTRING, column.getFieldType().getFieldType());
        assertFalse(column.getFieldType().isPrimitive());
        assertEquals(simple.getFloor(), column.getAccessor().apply(simple));
    }

    @Test
    public void testExplicitDateField() {
        EntityInfoBuilder<SimpleEntity> builder = new EntityInfoBuilder<>(SimpleEntity.class, "simple_entity");
        builder.addField("birthDay", "birthday", FieldTypeEnum.DATE);
        EntityInfoHelper entityInfo = new EntityInfoHelper(builder);

        ColumnInfo column = entityInfo.findColumn("birthday");
        assertEquals("birthday", column.getColumnName());
        assertEquals(FieldTypeEnum.DATE, column.getFieldType().getFieldType());
        assertFalse(column.getFieldType().isPrimitive());
        assertEquals(simple.getBirthDay(), column.getAccessor().apply(simple));
    }

    @Test
    public void testAnnotatedEnumField() {
        EntityInfoBuilder<SimpleEntityEx> builder = new EntityInfoBuilder<>(SimpleEntityEx.class, "simple_entity");
        builder.addField("order", "order");
        EntityInfoHelper entityInfo = new EntityInfoHelper(builder);

        ColumnInfo column = entityInfo.findColumn("order");
        assertEquals("order", column.getColumnName());
        assertEquals(FieldTypeEnum.ENUMORDINAL, column.getFieldType().getFieldType());
        assertFalse(column.getFieldType().isPrimitive());
        assertEquals(simpleEx.getOrder(), column.getAccessor().apply(simpleEx));
    }

    @Test
    public void testAnnotatedDateField() {
        EntityInfoBuilder<SimpleEntityEx> builder = new EntityInfoBuilder<>(SimpleEntityEx.class, "simple_entity");
        builder.addField("created", "created");
        EntityInfoHelper entityInfo = new EntityInfoHelper(builder);

        ColumnInfo column = entityInfo.findColumn("created");
        assertEquals("created", column.getColumnName());
        assertEquals(FieldTypeEnum.TIMESTAMP, column.getFieldType().getFieldType());
        assertFalse(column.getFieldType().isPrimitive());
        assertEquals(simpleEx.getCreated(), column.getAccessor().apply(simpleEx));
    }

    @Test
    public void testSimpleColumnDeclaration() {
        EntityInfoBuilder<SimpleEntity> builder = new EntityInfoBuilder<>(SimpleEntity.class, "simple_entity");
        builder.addColumn("name", FieldTypeEnum.STRING, SimpleEntity::getName);
        EntityInfoHelper entityInfo = new EntityInfoHelper(builder);

        ColumnInfo column = entityInfo.findColumn("name");
        assertEquals("name", column.getColumnName());
        assertEquals(FieldTypeEnum.STRING, column.getFieldType().getFieldType());
        assertFalse(column.getFieldType().isPrimitive());
        assertEquals(simple.getName(), column.getAccessor().apply(simple));
    }

    @Test
    public void testColumnChildEntity() {
        EntityInfoBuilder<SimpleEntity> builder = new EntityInfoBuilder<>(SimpleEntity.class, "simple_entity");
        builder.addColumn("city_name", FieldTypeEnum.STRING, obj -> obj.getCity().getName());
        EntityInfoHelper entityInfo = new EntityInfoHelper(builder);

        ColumnInfo column = entityInfo.findColumn("city_name");
        assertEquals("city_name", column.getColumnName());
        assertEquals(FieldTypeEnum.STRING, column.getFieldType().getFieldType());
        assertFalse(column.getFieldType().isPrimitive());
        assertEquals(simple.getCity().getName(), column.getAccessor().apply(simple));
    }

    @Test
    public void testSyntheticColumn() {
        EntityInfoBuilder<SimpleEntity> builder = new EntityInfoBuilder<>(SimpleEntity.class, "simple_entity");
        builder.addColumn("city_code", FieldTypeEnum.STRING,
                obj -> obj.getCity().getId() + ": " + obj.getCity().getName());
        EntityInfoHelper entityInfo = new EntityInfoHelper(builder);

        ColumnInfo column = entityInfo.findColumn("city_code");
        assertEquals("city_code", column.getColumnName());
        assertEquals(FieldTypeEnum.STRING, column.getFieldType().getFieldType());
        assertFalse(column.getFieldType().isPrimitive());
        assertEquals(simple.getCity().getId() + ": " + simple.getCity().getName(), column.getAccessor().apply(simple));
    }

    @Test
    public void testColumnCapturedValue() {
        String value = "Some Value";
        EntityInfoBuilder<SimpleEntity> builder = new EntityInfoBuilder<>(SimpleEntity.class, "simple_entity");
        builder.addColumn("some_column", FieldTypeEnum.STRING, obj -> value);
        EntityInfoHelper entityInfo = new EntityInfoHelper(builder);

        ColumnInfo column = entityInfo.findColumn("some_column");
        assertEquals("some_column", column.getColumnName());
        assertEquals(FieldTypeEnum.STRING, column.getFieldType().getFieldType());
        assertFalse(column.getFieldType().isPrimitive());
        assertEquals(value, column.getAccessor().apply(simple));
    }

    public static class Collision {

        private String city_name;
        private City city;

        public String getCityName() {
            return city_name;
        }

        public void setCityName(String cityName) {
            this.city_name = cityName;
        }

        public City getCity() {
            return city;
        }

        public void setCity(City city) {
            this.city = city;
        }

    }

    @Test
    public void childColumnNamesCanCollision() {
        EntityInfoBuilder<Collision> builder = new EntityInfoBuilder<>(Collision.class, "collision_entity");
        builder.addField("city_name");
        assertThrows(RuntimeException.class, () -> {
            builder.addField("city.name");
        });
    }

    public void childColumnNamesCollisionMustBeDisambiguated() {
        EntityInfoBuilder<Collision> builder = new EntityInfoBuilder<>(Collision.class, "collision_entity");
        builder.addField("city_name", "city_name_main");
        builder.addField("city.name");
        EntityInfoHelper entityInfo = new EntityInfoHelper(builder);

        assertNotNull(entityInfo.findColumn("city_name_main"));
        assertNotNull(entityInfo.findColumn("city_name"));
    }

    private class EntityInfoHelper {

        private final EntityInfo entityInfo;

        EntityInfoHelper(EntityInfoBuilder<?> builder) {
            entityInfo = builder.build();
        }

        public ColumnInfo findColumn(String columnName) {
            return entityInfo.getColumns().stream().filter(c -> c.getColumnName().equals(columnName)).findFirst().get();
        }

    }

}
