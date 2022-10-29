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
package org.jfleet.mysql;

import static org.jfleet.mysql.LoadDataConstants.ESCAPED_BY_CHAR;
import static org.jfleet.mysql.LoadDataConstants.FIELD_TERMINATED_CHAR;
import static org.jfleet.mysql.LoadDataConstants.LINE_TERMINATED_CHAR;

class LoadDataEscaper {

    public String escapeForLoadFile(String text) {
        if (text == null) {
            return null;
        }

        int firstEscapableChar = findFirstEscapableChar(text);
        if (firstEscapableChar == -1) {
            return text;
        }

        StringBuilder sb = new StringBuilder(text.substring(0, firstEscapableChar));

        int textLength = text.length();
        for (int i = firstEscapableChar; i < textLength; i++) {
            char textCharacter = text.charAt(i);
            if (isEscapable(textCharacter)) {
                sb.append(ESCAPED_BY_CHAR);
                sb.append(textCharacter);
            } else {
                sb.append(textCharacter);
            }
        }
        return sb.toString();
    }

    private int findFirstEscapableChar(String text) {
        int textLength = text.length();
        for (int i = 0; i < textLength; i++) {
            if (isEscapable(text.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    private boolean isEscapable(char character) {
        switch (character) {
        case ESCAPED_BY_CHAR:
        case LINE_TERMINATED_CHAR:
        case FIELD_TERMINATED_CHAR:
            return true;
        default:
            return false;
        }
    }
}
