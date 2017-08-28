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
package org.jfleet.postgres;

import java.text.SimpleDateFormat;

import org.jfleet.EntityFieldType.FieldTypeEnum;
import org.jfleet.common.BaseTypeSerializer;

public class PostgrestTypeSerializer extends BaseTypeSerializer{

    public PostgrestTypeSerializer() {
        super();
        add(FieldTypeEnum.BOOLEAN, FROM_BOOLEAN);
        add(FieldTypeEnum.TIMESTAMP, FROM_TIMESTAMP);
    }

    private static final Mapper FROM_BOOLEAN = (obj) -> ((Boolean) obj).booleanValue() ? "true" : "false";

    private static final Mapper FROM_TIMESTAMP = (obj) -> {
        if (obj instanceof java.util.Date) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            return sdf.format((java.util.Date) obj);
        }
        return null;
    };
}
