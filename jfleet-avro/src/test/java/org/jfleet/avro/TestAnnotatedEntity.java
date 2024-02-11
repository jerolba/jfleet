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
package org.jfleet.avro;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class TestAnnotatedEntity {

    private final String id;

    private final String someCode;

    @Column(name = "some_column")
    private final long someColumn;

    public TestAnnotatedEntity(String id, String someCode, long someColumn) {
        this.id = id;
        this.someCode = someCode;
        this.someColumn = someColumn;
    }

    public String getId() {
        return id;
    }

    public String getSomeCode() {
        return someCode;
    }

    public long getSomeColumn() {
        return someColumn;
    }

}
