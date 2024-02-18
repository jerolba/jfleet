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
package org.jfleet.csv;

public class CsvEscaper {

    private static final char NEWLINE_CHAR = '\n';
    private static final char CARRIAGE_RETURN_CHAR = '\r';
    private final StringBuilder sb = new StringBuilder();
    private final char delimiterChar; // '"'
    private final char separatorChar; // ','
    private final boolean alwaysDelimit;
    private StringState baseState = ROOT_STATE;

    CsvEscaper(CsvConfiguration<?> configuration) {
        this.delimiterChar = configuration.getTextDelimiter();
        this.separatorChar = configuration.getFieldSeparator();
        this.alwaysDelimit = configuration.isAlwaysDelimitText();
        if (this.alwaysDelimit) {
            this.baseState = NEEDS_BUT_DOESNT_HAVE;
        }

    }

    public String escapeAndDelimite(String text) {
        StringState stringState = findStringConfig(text);
        if (stringState == ROOT_STATE) {
            return text;
        }
        sb.setLength(0);
        if (stringState.needsDelimiter()) {
            sb.append(delimiterChar);
        }
        if (stringState.hasDelimiter()) {
            int textLength = text.length();
            for (int i = 0; i < textLength; i++) {
                char c = text.charAt(i);
                if (c == delimiterChar) {
                    sb.append(delimiterChar);
                }
                sb.append(c);
            }
        } else {
            sb.append(text);
        }
        if (stringState.needsDelimiter()) {
            sb.append(delimiterChar);
        }
        return sb.toString();
    }

    private StringState findStringConfig(String text) {
        int textLength = text.length();
        StringState state = baseState;
        for (int i = 0; i < textLength; i++) {
            char c = text.charAt(i);
            if (c == delimiterChar) {
                return NEEDS_AND_HAVE;
            }
            if (c == separatorChar || c == NEWLINE_CHAR || c == CARRIAGE_RETURN_CHAR) {
                state = state.needs();
            }
        }
        return state;
    }

    private static final StringState ROOT_STATE = new StringState();
    private static final NeedsDelimiterButDoesntHave NEEDS_BUT_DOESNT_HAVE = new NeedsDelimiterButDoesntHave();
    private static final NeedsAndHaveDelimiter NEEDS_AND_HAVE = new NeedsAndHaveDelimiter();

    private static class StringState {

        public boolean needsDelimiter() {
            return false;
        }

        public boolean hasDelimiter() {
            return false;
        }

        public StringState needs() {
            return NEEDS_BUT_DOESNT_HAVE;
        }

    }

    private static class NeedsDelimiterButDoesntHave extends StringState {

        @Override
        public boolean needsDelimiter() {
            return true;
        }

        @Override
        public boolean hasDelimiter() {
            return false;
        }

        @Override
        public StringState needs() {
            return this;
        }

    }

    private static class NeedsAndHaveDelimiter extends StringState {

        @Override
        public boolean needsDelimiter() {
            return true;
        }

        @Override
        public boolean hasDelimiter() {
            return true;
        }

        @Override
        public StringState needs() {
            return this;
        }

    }

}
