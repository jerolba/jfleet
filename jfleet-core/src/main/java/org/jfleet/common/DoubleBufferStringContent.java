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

class DoubleBufferStringContent {

    private final StringContent[] buffer;
    private final int batchSize;
    private final int size;
    private int current = 0;

    DoubleBufferStringContent(int batchSize, boolean concurrent) {
        this.batchSize = batchSize;
        this.size = concurrent ? 2 : 1;
        this.buffer = new StringContent[size];
    }

    public StringContent next() {
        int next = (current + 1) % size;
        if (buffer[next] == null) {
            buffer[next] = new StringContent(batchSize);
        }
        StringContent sc = buffer[next];
        sc.reset();
        current = next;
        return sc;
    }

}
