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

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;

import org.jfleet.EntityFieldType.FieldTypeEnum;
import org.jfleet.FieldInfo;
import org.junit.Test;

public class FailedManyToOneEntityInspectorTest {

    @Entity
    public class NotIdentified {

        private Long id;
        private String name;

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

    }

    @Entity
    public class Foo {

        @Id
        private Long id;
        @ManyToOne
        private NotIdentified reference;

        public Long getId() {
            return id;
        }

        public NotIdentified getReference() {
            return reference;
        }

    }

    @Entity
    public class Bar {

        @Id
        private String uuid;

        @ManyToOne
        @JoinTable(name = "some_table")
        private Foo foo;

        public String getUuid() {
            return uuid;
        }

        public Foo getFoo() {
            return foo;
        }

    }

    @Entity
    public class FooBar {

        @Id
        private int serialId;

        @ManyToOne
        @JoinColumns(value = {})
        private Foo foo;

        public int getSerialId() {
            return serialId;
        }

        public Foo getFoo() {
            return foo;
        }
    }

    private JpaFieldsInspector fieldsInspector = new JpaFieldsInspector();

    @Test
    public void nonIdentifiedIsNotMapped() {
        List<FieldInfo> fields = fieldsInspector.getFieldsFromClass(Foo.class);
        assertEquals(1, fields.size());
        FieldInfo id = fields.get(0);
        assertEquals(FieldTypeEnum.LONG, id.getFieldType().getFieldType());
        assertEquals("id", id.getColumnName());
    }

    @Test
    public void joinTableIsNotMapped() {
        List<FieldInfo> fields = fieldsInspector.getFieldsFromClass(Bar.class);
        assertEquals(1, fields.size());
        FieldInfo id = fields.get(0);
        assertEquals(FieldTypeEnum.STRING, id.getFieldType().getFieldType());
        assertEquals("uuid", id.getColumnName());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void joinColumnsIsNotMapped() {
        List<FieldInfo> fields = fieldsInspector.getFieldsFromClass(FooBar.class);
        assertEquals(1, fields.size());
        FieldInfo id = fields.get(0);
        assertEquals(FieldTypeEnum.INT, id.getFieldType().getFieldType());
        assertEquals("serialId", id.getColumnName());
    }

}
