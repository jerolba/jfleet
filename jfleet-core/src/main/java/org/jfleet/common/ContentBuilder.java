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
package org.jfleet.common;

public class ContentBuilder {

    private final EntityRowBuilder entityRowBuilder;
    private final DoubleBufferStringContent doubleBuffer;
    protected StringContent stringContent;

    public ContentBuilder(EntityRowBuilder entityRowBuilder, int batchSize, boolean concurrent) {
        this.entityRowBuilder = entityRowBuilder;
        this.doubleBuffer = new DoubleBufferStringContent(batchSize, concurrent);
        this.stringContent = doubleBuffer.next();
    }

    public void reset() {
        this.stringContent = doubleBuffer.next();
    }

    public boolean isFilled() {
        return stringContent.isFilled();
    }

    public int getContentSize() {
        return stringContent.getContentSize();
    }

    public int getRecords() {
        return stringContent.getRecords();
    }

    public StringContent getContent() {
        return stringContent;
    }

    public <T> void add(T entity) {
        entityRowBuilder.add(stringContent, entity);
        stringContent.inc();
    }

}
