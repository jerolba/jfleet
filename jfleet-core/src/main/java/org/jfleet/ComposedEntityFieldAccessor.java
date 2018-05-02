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

import java.util.function.Function;

public class ComposedEntityFieldAccessor implements Function<Object, Object> {

    private static final Function<Object, Object> IDENTITY = t -> t;

    private final Function<Object, Object> baseAccessor;
    private Function<Object, Object> nextAccessor;

    public ComposedEntityFieldAccessor() {
        this(IDENTITY);
    }

    private ComposedEntityFieldAccessor(Function<Object, Object> baseAccessor) {
        this.baseAccessor = baseAccessor;
    }

    @Override
    public Object apply(Object obj) {
        Object value = baseAccessor.apply(obj);
        if (value != null) {
            return nextAccessor.apply(value);
        }
        return null;
    }

    public ComposedEntityFieldAccessor andThenApply(Function<Object, Object> then) {
        ComposedEntityFieldAccessor composed = new ComposedEntityFieldAccessor(then);
        this.nextAccessor = composed;
        return composed;
    }

    public void andFinally(Function<Object, Object> accessor) {
        this.nextAccessor = accessor;
    }

}
