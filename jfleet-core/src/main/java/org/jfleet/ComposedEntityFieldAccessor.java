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
package org.jfleet;

public class ComposedEntityFieldAccessor implements EntityFieldAccessor {

    private static final EntityFieldAccessor identity = t -> t;

    private final EntityFieldAccessor baseAccessor;
    private EntityFieldAccessor nextAccessor;

    public ComposedEntityFieldAccessor() {
        this(identity);
    }

    private ComposedEntityFieldAccessor(EntityFieldAccessor baseAccessor) {
        this.baseAccessor = baseAccessor;
    }

    @Override
    public Object getValue(Object obj) {
        Object value = baseAccessor.getValue(obj);
        if (value != null) {
            return nextAccessor.getValue(value);
        }
        return null;
    }

    public ComposedEntityFieldAccessor andThen(EntityFieldAccessor then) {
        ComposedEntityFieldAccessor composed = new ComposedEntityFieldAccessor(then);
        this.nextAccessor = composed;
        return composed;
    }

    public void andFinally(EntityFieldAccessor accessor) {
        this.nextAccessor = accessor;
    }

}
