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

import java.util.function.Function;

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

    @Test
    public void mainFieldAccessTest() {
        Function<Object, Object> accessorId = factory.getAccessor(Person.class, fieldFor("id"));
        assertEquals(1L, accessorId.apply(person));
        Function<Object, Object> accessorName = factory.getAccessor(Person.class, fieldFor("name"));
        assertEquals("Sherlock Holmes", accessorName.apply(person));
    }

    @Test
    public void embeddedFieldAccessTest() {
        Function<Object, Object> accessorStreet = factory.getAccessor(Person.class, fieldFor("address.street"));
        assertEquals("221b Baker St", accessorStreet.apply(person));
        Function<Object, Object> accessorCity = factory.getAccessor(Person.class, fieldFor("address.city"));
        assertEquals("London", accessorCity.apply(person));
    }

    private FieldInfo fieldFor(String name) {
        FieldInfo fi = new FieldInfo();
        fi.setFieldName(name);
        return fi;
    }

}
