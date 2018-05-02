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
import static org.junit.Assert.assertNull;

import java.util.function.Function;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.junit.Test;

public class EntityFieldAccesorTest {

    @Entity
    public class PublicClass {

        @Id
        private String privateField;

        public String publicField;

        private String beanField;

        public PublicClass(String privateField, String publicField, String beanField) {
            this.privateField = privateField;
            this.publicField = publicField;
            this.beanField = beanField;
        }

        public String getBeanField() {
            return beanField;
        }

        public void setBeanField(String beanField) {
            this.beanField = beanField;
        }

    }

    @Entity
    class PackageClass {

        @Id
        private String privateField;

        public String publicField;

        private String beanField;

        PackageClass(String privateField, String publicField, String beanField) {
            this.privateField = privateField;
            this.publicField = publicField;
            this.beanField = beanField;
        }

        public String getBeanField() {
            return beanField;
        }

        public void setBeanField(String beanField) {
            this.beanField = beanField;
        }

    }

    @Entity
    @SuppressWarnings("unused")
    private class PrivateClass {

        @Id
        private String privateField;

        public String publicField;

        private String beanField;

        PrivateClass(String privateField, String publicField, String beanField) {
            this.privateField = privateField;
            this.publicField = publicField;
            this.beanField = beanField;
        }

        public String getBeanField() {
            return beanField;
        }

        public void setBeanField(String beanField) {
            this.beanField = beanField;
        }

    }

    @Entity
    public class BaseClass {

        private String beanField;

        public String publicField;

        public String getBeanField() {
            return beanField;
        }

        public void setBeanField(String beanField) {
            this.beanField = beanField;
        }

    }

    @Entity
    @Table(name = "child_table")
    public class ChildClass extends BaseClass {

        private int otherField;

        public int getOtherField() {
            return otherField;
        }

        public void setOtherField(int otherField) {
            this.otherField = otherField;
        }

        public ChildClass(int other, String bean, String parentPublic) {
            this.otherField = other;
            this.setBeanField(bean);
            this.publicField = parentPublic;
        }

    }

    @Entity
    public class ManyToOneClass {

        @ManyToOne
        @JoinColumn(name = "ref")
        private PublicClass reference;

        private int someField;

        public ManyToOneClass(int someField, PublicClass reference) {
            this.someField = someField;
            this.reference = reference;
        }

        public PublicClass getReference() {
            return reference;
        }

        public void setReference(PublicClass reference) {
            this.reference = reference;
        }

        public int getSomeField() {
            return someField;
        }

        public void setSomeField(int someField) {
            this.someField = someField;
        }

    }

    public class ManyToOneChildClass extends ManyToOneClass {

        public ManyToOneChildClass(int someField, PublicClass reference) {
            super(someField, reference);
        }
    }

    private PublicClass instancePublic = new PublicClass("private", "public", "bean");
    private PackageClass instancePackage = new PackageClass("private", "public", "bean");
    private PrivateClass instancePrivate = new PrivateClass("private", "public", "bean");
    private ChildClass instanceChild = new ChildClass(1, "bean value", "public one");
    private ManyToOneClass intanceManyToOne = new ManyToOneClass(10, instancePublic);

    private EntityFieldAccesorFactory factory = new EntityFieldAccesorFactory();

    @Test
    public void privateFieldOnPublicClassTest() {
        Function<Object, Object> accessor = factory.getAccessor(PublicClass.class, fieldFor("privateField"));
        assertEquals("private", accessor.apply(instancePublic));
    }

    @Test
    public void publicFieldOnPublicClassTest() {
        Function<Object, Object> accessor = factory.getAccessor(PublicClass.class, fieldFor("publicField"));
        assertEquals("public", accessor.apply(instancePublic));
    }

    @Test
    public void beanFieldOnPublicClassTest() {
        Function<Object, Object> accessor = factory.getAccessor(PublicClass.class, fieldFor("beanField"));
        assertEquals("bean", accessor.apply(instancePublic));
    }

    @Test
    public void privateFieldOnPackageClassTest() {
        Function<Object, Object> accessor = factory.getAccessor(PackageClass.class, fieldFor("privateField"));
        assertEquals("private", accessor.apply(instancePackage));
    }

    @Test
    public void publicFieldOnPackageClassTest() {
        Function<Object, Object> accessor = factory.getAccessor(PackageClass.class, fieldFor("publicField"));
        assertEquals("public", accessor.apply(instancePackage));
    }

    @Test
    public void beanFieldOnPackageClassTest() {
        Function<Object, Object> accessor = factory.getAccessor(PackageClass.class, fieldFor("beanField"));
        assertEquals("bean", accessor.apply(instancePackage));
    }

    @Test
    public void privateFieldOnPrivateClassTest() {
        Function<Object, Object> accessor = factory.getAccessor(PrivateClass.class, fieldFor("privateField"));
        assertEquals("private", accessor.apply(instancePrivate));
    }

    @Test
    public void publicFieldOnPrivateClassTest() {
        Function<Object, Object> accessor = factory.getAccessor(PrivateClass.class, fieldFor("publicField"));
        assertEquals("public", accessor.apply(instancePrivate));
    }

    @Test
    public void beanFieldOnPrivateClassTest() {
        Function<Object, Object> accessor = factory.getAccessor(PrivateClass.class, fieldFor("beanField"));
        assertEquals("bean", accessor.apply(instancePrivate));
    }

    @Test
    public void parentFieldOnHierarchy() {
        Function<Object, Object> accessor = factory.getAccessor(ChildClass.class, fieldFor("beanField"));
        assertEquals("bean value", accessor.apply(instanceChild));
    }

    @Test
    public void parentPublicFieldOnHierarchy() {
        Function<Object, Object> accessor = factory.getAccessor(ChildClass.class, fieldFor("publicField"));
        assertEquals("public one", accessor.apply(instanceChild));
    }

    @Test
    public void childFieldOnHierarchy() {
        Function<Object, Object> accessor = factory.getAccessor(ChildClass.class, fieldFor("otherField"));
        assertEquals(1, accessor.apply(instanceChild));
    }

    @Test
    public void composedField() {
        Function<Object, Object> accessor = factory.getAccessor(ManyToOneClass.class,
                fieldFor("reference.privateField"));
        assertEquals("private", accessor.apply(intanceManyToOne));
    }

    @Test
    public void nonExistentField() {
        Function<Object, Object> accessor = factory.getAccessor(PublicClass.class, fieldFor("nonExistent"));
        assertNull(accessor);
    }

    @Test
    public void nonExistentComposedField() {
        Function<Object, Object> accessor = factory.getAccessor(ManyToOneClass.class,
                fieldFor("nonExistent.privateField"));
        assertNull(accessor);
    }

    @Test
    public void nonExistentComposedFieldOnChildClass() {
        Function<Object, Object> accessor = factory.getAccessor(ManyToOneChildClass.class,
                fieldFor("nonExistent.privateField"));
        assertNull(accessor);
    }

    private FieldInfo fieldFor(String name) {
        FieldInfo fi = new FieldInfo();
        fi.setFieldName(name);
        return fi;
    }

}
