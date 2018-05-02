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

import org.jfleet.EntityFieldType.FieldTypeEnum;
import org.jfleet.FieldInfo;
import org.jfleet.entities.Company;
import org.jfleet.entities.Person;
import org.junit.Test;

public class EmbeddedInspectorTest {

    @Test
    public void inspectEmbeddedEntity() {
        EntityInspectHelper entityInfo = new EntityInspectHelper(Person.class);

        FieldInfo id = entityInfo.findField("id");
        assertEquals(FieldTypeEnum.LONG, id.getFieldType().getFieldType());
        assertEquals("id", id.getColumnName());

        FieldInfo name = entityInfo.findField("name");
        assertEquals(FieldTypeEnum.STRING, name.getFieldType().getFieldType());
        assertEquals("name", name.getColumnName());

        FieldInfo street = entityInfo.findField("address.street");
        assertEquals(FieldTypeEnum.STRING, street.getFieldType().getFieldType());
        assertEquals("street", street.getColumnName());

        FieldInfo city = entityInfo.findField("address.city");
        assertEquals(FieldTypeEnum.STRING, city.getFieldType().getFieldType());
        assertEquals("city", city.getColumnName());
    }

    @Test
    public void inspectEmbeddedEntityWithAttributeOverrides() {
        EntityInspectHelper entityInfo = new EntityInspectHelper(Company.class);

        FieldInfo id = entityInfo.findField("id");
        assertEquals(FieldTypeEnum.LONG, id.getFieldType().getFieldType());
        assertEquals("id", id.getColumnName());

        FieldInfo name = entityInfo.findField("name");
        assertEquals(FieldTypeEnum.STRING, name.getFieldType().getFieldType());
        assertEquals("name", name.getColumnName());

        FieldInfo postalStreet = entityInfo.findField("postalAddress.street");
        assertEquals(FieldTypeEnum.STRING, postalStreet.getFieldType().getFieldType());
        assertEquals("postal_street", postalStreet.getColumnName());

        FieldInfo postalCity = entityInfo.findField("postalAddress.city");
        assertEquals(FieldTypeEnum.STRING, postalCity.getFieldType().getFieldType());
        assertEquals("postal_city", postalCity.getColumnName());

        FieldInfo fiscalStreet = entityInfo.findField("fiscalAddress.street");
        assertEquals(FieldTypeEnum.STRING, fiscalStreet.getFieldType().getFieldType());
        assertEquals("fiscal_street", fiscalStreet.getColumnName());

        FieldInfo fiscalCity = entityInfo.findField("fiscalAddress.city");
        assertEquals(FieldTypeEnum.STRING, fiscalCity.getFieldType().getFieldType());
        assertEquals("fiscal_city", fiscalCity.getColumnName());
    }

}
