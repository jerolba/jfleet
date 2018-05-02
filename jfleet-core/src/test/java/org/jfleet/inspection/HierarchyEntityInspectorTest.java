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

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

import org.junit.Test;

public class HierarchyEntityInspectorTest {

    private JpaFieldsInspector fieldsInspector = new JpaFieldsInspector();

    @Entity
    public class BaseClass {

        private String someField;

        public String getSomeField() {
            return someField;
        }

        public void setSomeField(String someField) {
            this.someField = someField;
        }

    }

    @Entity
    public class ChildClass extends BaseClass {

        private int otherField;

        public int getOtherField() {
            return otherField;
        }

        public void setOtherField(int otherField) {
            this.otherField = otherField;
        }

    }

    @Test
    public void inspectEntityWithHierarchy() {
        List<FieldInfo> fields = fieldsInspector.getFieldsFromClass(ChildClass.class);
        assertEquals(2, fields.size());
        assertEquals("otherField", fields.get(0).getFieldName());
        assertEquals("someField", fields.get(1).getFieldName());
    }

    @MappedSuperclass
    public class BaseClassMapped {

        private String someField;

        public String getSomeField() {
            return someField;
        }

        public void setSomeField(String someField) {
            this.someField = someField;
        }

    }

    @Entity
    public class ChildClassWithMapped extends BaseClassMapped {

        private int otherField;

        public int getOtherField() {
            return otherField;
        }

        public void setOtherField(int otherField) {
            this.otherField = otherField;
        }

    }

    @Test
    public void inspectEntityWithHierarchyMapped() {
        List<FieldInfo> fields = fieldsInspector.getFieldsFromClass(ChildClassWithMapped.class);
        assertEquals(2, fields.size());
        assertEquals("otherField", fields.get(0).getFieldName());
        assertEquals("someField", fields.get(1).getFieldName());
    }

    public class BaseClassNonEntity {

        private String someField;

        public String getSomeField() {
            return someField;
        }

        public void setSomeField(String someField) {
            this.someField = someField;
        }

    }

    @Entity
    public class ChildClassNonEntity extends BaseClassNonEntity {

        private int otherField;

        public int getOtherField() {
            return otherField;
        }

        public void setOtherField(int otherField) {
            this.otherField = otherField;
        }

    }

    @Test
    public void inspectEntityWithNonEntityParent() {
        List<FieldInfo> fields = fieldsInspector.getFieldsFromClass(ChildClassNonEntity.class);
        assertEquals(1, fields.size());
        assertEquals("otherField", fields.get(0).getFieldName());
    }

    @Entity
    @AttributeOverrides(value = {
        @AttributeOverride(name = "someField", column = @Column(name = "field_override")),
    })
    public class ChildClassWithAttributeOverrides extends BaseClassMapped {

        private int otherField;

        public int getOtherField() {
            return otherField;
        }

        public void setOtherField(int otherField) {
            this.otherField = otherField;
        }

    }

    @Test
    public void inspectEntityWithAttributeOverrides() {
        List<FieldInfo> fields = fieldsInspector.getFieldsFromClass(ChildClassWithAttributeOverrides.class);
        assertEquals(2, fields.size());
        assertEquals("otherField", fields.get(0).getFieldName());
        assertEquals("someField", fields.get(1).getFieldName());
        assertEquals("field_override", fields.get(1).getColumnName());

    }

    @Entity
    @AttributeOverride(name = "someField", column = @Column(name = "field_override"))
    public class ChildClassWithAttributeOverride extends BaseClassMapped {

        private int otherField;

        public int getOtherField() {
            return otherField;
        }

        public void setOtherField(int otherField) {
            this.otherField = otherField;
        }

    }

    @Test
    public void inspectEntityWithAttributeOverride() {
        List<FieldInfo> fields = fieldsInspector.getFieldsFromClass(ChildClassWithAttributeOverride.class);
        assertEquals(2, fields.size());
        assertEquals("otherField", fields.get(0).getFieldName());
        assertEquals("someField", fields.get(1).getFieldName());
        assertEquals("field_override", fields.get(1).getColumnName());

    }

}
