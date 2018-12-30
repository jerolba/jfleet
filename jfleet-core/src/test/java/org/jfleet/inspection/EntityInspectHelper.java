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
package org.jfleet.inspection;

import java.util.List;

import org.jfleet.FieldInfo;

public class EntityInspectHelper {

    private static JpaFieldsInspector fieldsInspector = new JpaFieldsInspector();

    private final List<FieldInfo> fields;

    public EntityInspectHelper(Class<?> clasz) {
        fields = fieldsInspector.getFieldsFromClass(clasz);
    }

    public FieldInfo findField(String fieldName) {
        return fields.stream().filter(f -> f.getFieldName().equals(fieldName)).findFirst().get();
    }

}
