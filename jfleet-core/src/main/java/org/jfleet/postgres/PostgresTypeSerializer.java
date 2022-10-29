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
package org.jfleet.postgres;

import static org.jfleet.EntityFieldType.FieldTypeEnum.BOOLEAN;
import static org.jfleet.EntityFieldType.FieldTypeEnum.LOCALDATETIME;
import static org.jfleet.EntityFieldType.FieldTypeEnum.LOCALTIME;
import static org.jfleet.EntityFieldType.FieldTypeEnum.TIME;
import static org.jfleet.EntityFieldType.FieldTypeEnum.TIMESTAMP;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.jfleet.common.BaseTypeSerializer;

class PostgresTypeSerializer extends BaseTypeSerializer {

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    private final SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss.SSS");
    private final DateTimeFormatter dtfLocalTime = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    private final DateTimeFormatter dtfLocalDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    PostgresTypeSerializer() {
        super();
        add(BOOLEAN, FROM_BOOLEAN);
        add(TIMESTAMP, FROM_TIMESTAMP);
        add(TIME, FROM_TIME);
        add(LOCALTIME, FROM_LOCALTIME);
        add(LOCALDATETIME, FROM_LOCALDATETIME);
    }

    private final Mapper FROM_BOOLEAN = (obj) -> ((Boolean) obj).booleanValue() ? "true" : "false";

    private final Mapper FROM_TIMESTAMP = (obj) -> {
        if (obj instanceof java.util.Date) {
            return sdf.format((java.util.Date) obj);
        }
        return null;
    };

    private final Mapper FROM_TIME = (obj) -> {
        if (obj instanceof java.util.Date) {
            return sdfTime.format((java.util.Date) obj);
        }
        return null;
    };

    private final Mapper FROM_LOCALTIME = (obj) -> {
        if (obj instanceof LocalTime) {
            return dtfLocalTime.format((LocalTime) obj);
        }
        return null;
    };

    private final Mapper FROM_LOCALDATETIME = (obj) -> {
        if (obj instanceof LocalDateTime) {
            return dtfLocalDateTime.format((LocalDateTime) obj);
        }
        return null;
    };

}
