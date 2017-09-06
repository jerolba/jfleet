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
