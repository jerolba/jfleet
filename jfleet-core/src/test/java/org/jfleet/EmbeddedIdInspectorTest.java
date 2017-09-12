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

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import org.jfleet.EntityFieldType.FieldTypeEnum;
import org.junit.Test;

public class EmbeddedIdInspectorTest {

    @Embeddable
    public class CompositeKey {
        private String someId;
        private Long otherId;

        public String getSomeId() {
            return someId;
        }

        public void setSomeId(String someId) {
            this.someId = someId;
        }

        public Long getOtherId() {
            return otherId;
        }

        public void setOtherId(Long otherId) {
            this.otherId = otherId;
        }
    }

    @Entity
    public class SomeClass {

        @EmbeddedId
        private CompositeKey key;
        private String someValue;

        public CompositeKey getKey() {
            return key;
        }

        public void setKey(CompositeKey key) {
            this.key = key;
        }

        public String getSomeValue() {
            return someValue;
        }

        public void setSomeValue(String someValue) {
            this.someValue = someValue;
        }

    }

    @Entity
    public class SomeClassWithOverride {

        @EmbeddedId
        @AttributeOverrides({
            @AttributeOverride(name = "someId", column = @Column(name = "id_string")),
            @AttributeOverride(name = "otherId", column = @Column(name = "id_long"))
        })
        private CompositeKey key;

        @Column(name = "value")
        private String someValue;

        public CompositeKey getKey() {
            return key;
        }

        public void setKey(CompositeKey key) {
            this.key = key;
        }

        public String getSomeValue() {
            return someValue;
        }

        public void setSomeValue(String someValue) {
            this.someValue = someValue;
        }

    }


    @Test
    public void inspectEmbeddedIdEntity() {
        JpaEntityInspector inspector = new JpaEntityInspector(SomeClass.class);
        EntityInfo entityInfo = inspector.inspect();

        FieldInfo someId = getField(entityInfo, "key.someId");
        assertEquals(FieldTypeEnum.STRING, someId.getFieldType().getFieldType());
        assertEquals("someId", someId.getColumnName());

        FieldInfo otherId = getField(entityInfo, "key.otherId");
        assertEquals(FieldTypeEnum.LONG, otherId.getFieldType().getFieldType());
        assertEquals("otherId", otherId.getColumnName());

        FieldInfo someValue = getField(entityInfo, "someValue");
        assertEquals(FieldTypeEnum.STRING, someValue.getFieldType().getFieldType());
        assertEquals("someValue", someValue.getColumnName());
    }


    @Test
    public void inspectEmbeddedIdEntityWithOverride() {
        JpaEntityInspector inspector = new JpaEntityInspector(SomeClassWithOverride.class);
        EntityInfo entityInfo = inspector.inspect();

        FieldInfo someId = getField(entityInfo, "key.someId");
        assertEquals(FieldTypeEnum.STRING, someId.getFieldType().getFieldType());
        assertEquals("id_string", someId.getColumnName());

        FieldInfo otherId = getField(entityInfo, "key.otherId");
        assertEquals(FieldTypeEnum.LONG, otherId.getFieldType().getFieldType());
        assertEquals("id_long", otherId.getColumnName());

        FieldInfo someValue = getField(entityInfo, "someValue");
        assertEquals(FieldTypeEnum.STRING, someValue.getFieldType().getFieldType());
        assertEquals("value", someValue.getColumnName());
    }


    private FieldInfo getField(EntityInfo entityInfo, String fieldName) {
        return entityInfo.getFields().stream().filter(f -> f.getFieldName().equals(fieldName)).findFirst().get();
    }

}
