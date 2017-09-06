package org.jfleet;

import static org.junit.Assert.assertEquals;

import org.jfleet.entities.Address;
import org.jfleet.entities.Person;
import org.junit.Before;
import org.junit.Test;

public class EmbeddedEntityFieldAccesorTest {

    private Person person;

    @Before
    public void setup() {
        person = new Person();
        person.setId(1L);
        person.setName("Sherlock Holmes");
        Address address = new Address();
        address.setCity("London");
        address.setStreet("221b Baker St");
        person.setAddress(address);
    }

    private EntityFieldAccesorFactory factory = new EntityFieldAccesorFactory();

    //@Test
    public void mainFieldAccessTest() {
        EntityFieldAccessor accessorId = factory.getAccessor(Person.class, fieldFor("id"));
        assertEquals(1L, accessorId.getValue(person));
        EntityFieldAccessor accessorName = factory.getAccessor(Person.class, fieldFor("name"));
        assertEquals("Sherlock Holmes", accessorName.getValue(person));
    }

    @Test
    public void embeddedFieldAccessTest() {
        EntityFieldAccessor accessorStreet = factory.getAccessor(Person.class, fieldFor("address.street"));
        assertEquals("221b Baker St", accessorStreet.getValue(person));
        EntityFieldAccessor accessorCity = factory.getAccessor(Person.class, fieldFor("address.city"));
        assertEquals("London", accessorCity.getValue(person));
    }

    private FieldInfo fieldFor(String name) {
        FieldInfo fi = new FieldInfo();
        fi.setFieldName(name);
        return fi;
    }

}
