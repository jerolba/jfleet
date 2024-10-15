/**
 * Copyright 2022 Jerónimo López Bezanilla
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

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.jfleet.EntityFieldType.FieldTypeEnum;
import org.jfleet.FieldInfo;
import org.junit.jupiter.api.Test;

public class ManyToOneEntityInspectorTest {

    @Entity
    public class Product {

        @Id
        private Long id;
        private String name;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    @Entity
    public class Sku {

        @Id
        private Long id;
        private String reference;
        @ManyToOne
        private Product product;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getReference() {
            return reference;
        }

        public void setReference(String reference) {
            this.reference = reference;
        }

        public Product getProduct() {
            return product;
        }

        public void setProduct(Product product) {
            this.product = product;
        }

    }

    @Entity
    public class Foo {

        @Id
        private Long id;
        @ManyToOne
        @JoinColumn(name = "foo_id_product_fk")
        private Product productRef;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Product getProductRef() {
            return productRef;
        }

        public void setProduct(Product productRef) {
            this.productRef = productRef;
        }

    }

    @Test
    public void inspectAnEntityWithManyToOneRelationship() {
        EntityInspectHelper entityInfo = new EntityInspectHelper(Sku.class);

        FieldInfo id = entityInfo.findField("id");
        assertEquals(FieldTypeEnum.LONG, id.getFieldType().getFieldType());
        assertEquals("id", id.getColumnName());

        FieldInfo reference = entityInfo.findField("reference");
        assertEquals(FieldTypeEnum.STRING, reference.getFieldType().getFieldType());
        assertEquals("reference", reference.getColumnName());

        FieldInfo street = entityInfo.findField("product.id");
        assertEquals(FieldTypeEnum.LONG, street.getFieldType().getFieldType());
        assertEquals("product_id", street.getColumnName());
    }

    @Test
    public void inspectAnEntityWithManyToOneJoinColumn() {
        EntityInspectHelper entityInfo = new EntityInspectHelper(Foo.class);

        FieldInfo id = entityInfo.findField("id");
        assertEquals(FieldTypeEnum.LONG, id.getFieldType().getFieldType());
        assertEquals("id", id.getColumnName());

        FieldInfo street = entityInfo.findField("productRef.id");
        assertEquals(FieldTypeEnum.LONG, street.getFieldType().getFieldType());
        assertEquals("foo_id_product_fk", street.getColumnName());
    }

}
