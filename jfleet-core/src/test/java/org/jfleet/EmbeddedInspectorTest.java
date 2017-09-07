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

import org.jfleet.EntityFieldType.FieldTypeEnum;
import org.jfleet.entities.Person;
import org.junit.Test;

public class EmbeddedInspectorTest {

    @Test
    public void inspectEmbeddedEntity() {
        JpaEntityInspector inspector = new JpaEntityInspector(Person.class);
        EntityInfo entityInfo = inspector.inspect();

        FieldInfo id = getField(entityInfo, "id");
        assertEquals(FieldTypeEnum.LONG, id.getFieldType().getFieldType());
        assertEquals("id", id.getColumnName());

        FieldInfo name = getField(entityInfo, "name");
        assertEquals(FieldTypeEnum.STRING, name.getFieldType().getFieldType());
        assertEquals("name", name.getColumnName());

        FieldInfo street = getField(entityInfo, "address.street");
        assertEquals(FieldTypeEnum.STRING, street.getFieldType().getFieldType());
        assertEquals("street", street.getColumnName());

        FieldInfo city = getField(entityInfo, "address.city");
        assertEquals(FieldTypeEnum.STRING, city.getFieldType().getFieldType());
        assertEquals("city", city.getColumnName());
    }

    private FieldInfo getField(EntityInfo entityInfo, String fieldName) {
        return entityInfo.getFields().stream().filter(f -> f.getFieldName().equals(fieldName)).findFirst().get();
    }

}
