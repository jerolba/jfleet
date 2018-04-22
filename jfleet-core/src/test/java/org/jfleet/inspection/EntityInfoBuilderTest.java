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
package org.jfleet.inspection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.jfleet.EntityInfo;
import org.jfleet.EntityInfoBuilder;
import org.jfleet.FieldInfo;
import org.jfleet.EntityFieldType.FieldTypeEnum;
import org.jfleet.metadata.JFleetEnumType;
import org.jfleet.metadata.JFleetEnumerated;
import org.jfleet.metadata.JFleetTemporal;
import org.jfleet.metadata.JFleetTemporalType;
import org.junit.Test;

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

    @Test(expected = NoSuchFieldException.class)
    public void testNonExistentField() {
        EntityInfoBuilder builder = new EntityInfoBuilder(SimpleEntity.class, "simple_entity");
        builder.addField("noField", "someName");
    }

    @Test(expected = NoSuchFieldException.class)
    public void testNonExistentFieldInHierarchy() {
        EntityInfoBuilder builder = new EntityInfoBuilder(SimpleEntityEx.class, "simple_entity");
        builder.addField("noField", "someName");
    }

    @Test
    public void testExistentField() {
        EntityInfoBuilder builder = new EntityInfoBuilder(SimpleEntity.class, "simple_entity");
        builder.addField("name", "name");
        EntityInfo entityInfo = builder.build();
        FieldInfo field = entityInfo.findField("name");
        assertEquals("name", field.getFieldName());
        assertEquals("name", field.getColumnName());
        assertEquals(FieldTypeEnum.STRING, field.getFieldType().getFieldType());
        assertFalse(field.getFieldType().isPrimitive());
    }

    @Test
    public void testExistentFieldInHierarchy() {
        EntityInfoBuilder builder = new EntityInfoBuilder(SimpleEntityEx.class, "simple_entity");
        builder.addField("height", "height");
        EntityInfo entityInfo = builder.build();
        FieldInfo field = entityInfo.findField("height");
        assertEquals("height", field.getFieldName());
        assertEquals("height", field.getColumnName());
        assertEquals(FieldTypeEnum.INT, field.getFieldType().getFieldType());
        assertTrue(field.getFieldType().isPrimitive());
    }

    @Test
    public void testExistentPathField() {
        EntityInfoBuilder builder = new EntityInfoBuilder(SimpleEntity.class, "simple_entity");
        builder.addField("city.name", "city_name");
        EntityInfo entityInfo = builder.build();
        FieldInfo field = entityInfo.findField("city.name");
        assertEquals("city.name", field.getFieldName());
        assertEquals("city_name", field.getColumnName());
        assertEquals(FieldTypeEnum.STRING, field.getFieldType().getFieldType());
        assertFalse(field.getFieldType().isPrimitive());
    }

    @Test
    public void testImplicitColumnName() {
        EntityInfoBuilder builder = new EntityInfoBuilder(SimpleEntity.class, "simple_entity");
        builder.addField("birthDay");
        EntityInfo entityInfo = builder.build();
        FieldInfo field = entityInfo.findField("birthDay");
        assertEquals("birthday", field.getColumnName());
    }

    @Test
    public void testImplicitColumnNameComposed() {
        EntityInfoBuilder builder = new EntityInfoBuilder(SimpleEntity.class, "simple_entity");
        builder.addField("city.name");
        EntityInfo entityInfo = builder.build();
        FieldInfo field = entityInfo.findField("city.name");
        assertEquals("city_name", field.getColumnName());
    }

    @Test
    public void testExplicitEnumField() {
        EntityInfoBuilder builder = new EntityInfoBuilder(SimpleEntity.class, "simple_entity");
        builder.addField("floor", "floor", FieldTypeEnum.ENUMSTRING);
        EntityInfo entityInfo = builder.build();
        FieldInfo field = entityInfo.findField("floor");
        assertEquals("floor", field.getFieldName());
        assertEquals("floor", field.getColumnName());
        assertEquals(FieldTypeEnum.ENUMSTRING, field.getFieldType().getFieldType());
        assertFalse(field.getFieldType().isPrimitive());
    }

    @Test
    public void testExplicitDateField() {
        EntityInfoBuilder builder = new EntityInfoBuilder(SimpleEntity.class, "simple_entity");
        builder.addField("birthDay", "birthday", FieldTypeEnum.DATE);
        EntityInfo entityInfo = builder.build();
        FieldInfo field = entityInfo.findField("birthDay");
        assertEquals("birthDay", field.getFieldName());
        assertEquals("birthday", field.getColumnName());
        assertEquals(FieldTypeEnum.DATE, field.getFieldType().getFieldType());
        assertFalse(field.getFieldType().isPrimitive());
    }

    @Test
    public void testAnnotatedEnumField() {
        EntityInfoBuilder builder = new EntityInfoBuilder(SimpleEntityEx.class, "simple_entity");
        builder.addField("order", "order");
        EntityInfo entityInfo = builder.build();
        FieldInfo field = entityInfo.findField("order");
        assertEquals("order", field.getFieldName());
        assertEquals("order", field.getColumnName());
        assertEquals(FieldTypeEnum.ENUMORDINAL, field.getFieldType().getFieldType());
        assertFalse(field.getFieldType().isPrimitive());
    }

    @Test
    public void testAnnotatedDateField() {
        EntityInfoBuilder builder = new EntityInfoBuilder(SimpleEntityEx.class, "simple_entity");
        builder.addField("created", "created");
        EntityInfo entityInfo = builder.build();
        FieldInfo field = entityInfo.findField("created");
        assertEquals("created", field.getFieldName());
        assertEquals("created", field.getColumnName());
        assertEquals(FieldTypeEnum.TIMESTAMP, field.getFieldType().getFieldType());
        assertFalse(field.getFieldType().isPrimitive());
    }

}
