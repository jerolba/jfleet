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

public class StringContent {

    private final StringBuilder sb;
    private final int batchSize;
    private int records;

    public StringContent(int batchSize) {
        this.sb = new StringBuilder(batchSize + Math.min(1024, batchSize / 1000));
        this.batchSize = batchSize;
        this.records = 0;
    }

    public void append(char c) {
        sb.append(c);
    }

    public void append(String value) {
        sb.append(value);
    }

    public void inc() {
        this.records++;
    }

    public boolean isFilled() {
        return sb.length() > batchSize;
    }

    public void reset() {
        this.sb.setLength(0);
        this.records = 0;
    }

    public int getContentSize() {
        return sb.length();
    }

    public StringBuilder getContent() {
        return sb;
    }

    public int getRecords() {
        return records;
    }

}
