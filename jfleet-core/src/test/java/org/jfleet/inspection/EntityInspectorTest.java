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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.jfleet.EntityFieldType;
import org.jfleet.EntityFieldType.FieldTypeEnum;
import org.jfleet.EntityInfo;
import org.jfleet.FieldInfo;
import org.junit.jupiter.api.Test;

public class EntityInspectorTest {

    private JpaFieldsInspector fieldsInspector = new JpaFieldsInspector();

    public class SimpleClass {

    }

    @Test
    public void entityMustBeAnnotated() {
        assertThrows(RuntimeException.class, () -> {
            new JpaEntityInspector(SimpleClass.class);
        });
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
        List<FieldInfo> fields = fieldsInspector.getFieldsFromClass(SimpleEntityUnannotated.class);
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
        List<FieldInfo> fields = fieldsInspector.getFieldsFromClass(SimpleEntityAnnotated.class);
        assertEquals(1, fields.size());
        FieldInfo field = fields.get(0);
        assertEquals("some_column", field.getColumnName());
        assertEquals("someField", field.getFieldName());
    }

    @Test
    public void inspectFieldType() {
        List<FieldInfo> fields = fieldsInspector.getFieldsFromClass(SimpleEntityAnnotated.class);
        assertEquals(1, fields.size());
        FieldInfo field = fields.get(0);
        EntityFieldType fieldType = field.getFieldType();
        assertFalse(fieldType.isPrimitive());
        assertEquals(FieldTypeEnum.STRING, fieldType.getFieldType());
    }

    @Entity
    @Table(name = "entity_with_sequence_id")
    public class EntityWithGeneratedSequenceId {

        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE)
        private Long id;

        @Column(name = "some_column")
        private String someField;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getSomeField() {
            return someField;
        }

        public void setSomeField(String someField) {
            this.someField = someField;
        }

    }

    @Test
    public void inspectEntitySequenceId() {
        List<FieldInfo> fields = fieldsInspector.getFieldsFromClass(EntityWithGeneratedSequenceId.class);
        assertEquals(2, fields.size());
        FieldInfo field = fields.get(0);
        EntityFieldType fieldType = field.getFieldType();
        assertFalse(fieldType.isPrimitive());
        assertEquals(FieldTypeEnum.LONG, fieldType.getFieldType());
        assertFalse(fieldType.isIdentityId());
    }

    @Entity
    @Table(name = "entity_with_identity_id")
    public class EntityWithGeneratedIdentityId {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "some_column")
        private String someField;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getSomeField() {
            return someField;
        }

        public void setSomeField(String someField) {
            this.someField = someField;
        }

    }

    @Test
    public void inspectEntityIdetityId() {
        List<FieldInfo> fields = fieldsInspector.getFieldsFromClass(EntityWithGeneratedIdentityId.class);
        assertEquals(2, fields.size());
        FieldInfo field = fields.get(0);
        EntityFieldType fieldType = field.getFieldType();
        assertFalse(fieldType.isPrimitive());
        assertEquals(FieldTypeEnum.LONG, fieldType.getFieldType());
        assertTrue(fieldType.isIdentityId());
    }

}
