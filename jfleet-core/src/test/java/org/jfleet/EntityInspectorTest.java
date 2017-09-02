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
package org.jfleet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.jfleet.EntityFieldType.FieldTypeEnum;
import org.junit.Test;

public class EntityInspectorTest {

    public class SimpleClass {

    }

    @Test(expected = RuntimeException.class)
    public void entityMustBeAnnotated() {
        JpaEntityInspector inspector = new JpaEntityInspector(SimpleClass.class);
        inspector.inspect();
    }

    @Entity
    public class SimpleEntityUnannotated {

        private String someField;

        public String getSomeField() {
            return someField;
        }

        public void setSomeField(String someField) {
            this.someField = someField;
        }

    }

    @Test
    public void inspectGetClass() {
        JpaEntityInspector inspector = new JpaEntityInspector(SimpleEntityUnannotated.class);
        EntityInfo entityInfo = inspector.inspect();
        assertEquals(SimpleEntityUnannotated.class, entityInfo.getEntityClass());
    }

    @Test
    public void inspectTableNameByEntityName() {
        JpaEntityInspector inspector = new JpaEntityInspector(SimpleEntityUnannotated.class);
        EntityInfo entityInfo = inspector.inspect();
        assertEquals("simpleentityunannotated", entityInfo.getTableName());
    }

    @Test
    public void inspectColumnByPropertyName() {
        JpaEntityInspector inspector = new JpaEntityInspector(SimpleEntityUnannotated.class);
        EntityInfo entityInfo = inspector.inspect();
        List<FieldInfo> fields = entityInfo.getFields();
        assertEquals(1, fields.size());
        FieldInfo field = fields.get(0);
        assertEquals("someField", field.getColumnName());
        assertEquals("someField", field.getFieldName());
    }

    @Entity
    @Table(name = "simple_entity")
    public class SimpleEntityAnnotated {

        @Column(name = "some_column")
        private String someField;

        public String getSomeField() {
            return someField;
        }

        public void setSomeField(String someField) {
            this.someField = someField;
        }

    }

    @Test
    public void inspectTableNameByTableAnnotation() {
        JpaEntityInspector inspector = new JpaEntityInspector(SimpleEntityAnnotated.class);
        EntityInfo entityInfo = inspector.inspect();
        assertEquals("simple_entity", entityInfo.getTableName());
    }

    @Test
    public void inspectColumnByColumnAnnotation() {
        JpaEntityInspector inspector = new JpaEntityInspector(SimpleEntityAnnotated.class);
        EntityInfo entityInfo = inspector.inspect();
        List<FieldInfo> fields = entityInfo.getFields();
        assertEquals(1, fields.size());
        FieldInfo field = fields.get(0);
        assertEquals("some_column", field.getColumnName());
        assertEquals("someField", field.getFieldName());
    }

    @Test
    public void inspectFieldType() {
        JpaEntityInspector inspector = new JpaEntityInspector(SimpleEntityAnnotated.class);
        EntityInfo entityInfo = inspector.inspect();
        List<FieldInfo> fields = entityInfo.getFields();
        assertEquals(1, fields.size());
        FieldInfo field = fields.get(0);
        EntityFieldType fieldType = field.getFieldType();
        assertFalse(fieldType.isPrimitive());
        assertEquals(FieldTypeEnum.STRING, fieldType.getFieldType());
    }

}
