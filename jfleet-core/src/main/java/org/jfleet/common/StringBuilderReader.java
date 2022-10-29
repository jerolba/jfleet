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

import java.io.IOException;
import java.io.Reader;

public class StringBuilderReader extends Reader {

    private final StringBuilder sb;
    private int next = 0;

    public StringBuilderReader(StringBuilder sb) {
        this.sb = sb;
    }

    @Override
    public int read(char cbuf[], int off, int len) throws IOException {
        if (off < 0 || off > cbuf.length || len < 0 || off + len > cbuf.length) {
            throw new IndexOutOfBoundsException("off=" + off + " len=" + len + " cbuf.length=" + cbuf.length);
        }
        if (len == 0) {
            return 0;
        }
        if (next >= sb.length()) {
            return -1;
        }
        int n = Math.min(sb.length() - next, len);
        sb.getChars(next, next + n, cbuf, off);
        next += n;
        return n;
    }

    @Override
    public void close() throws IOException {

    }
}
