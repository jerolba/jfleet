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
package org.jfleet.parameterized;

import java.lang.reflect.Method;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public class DatabaseArgumentProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context)  {
        Method testMethod = context.getTestMethod().get();
        DBs dbs = testMethod.getAnnotation(DBs.class);
        if (dbs != null) {
            Databases[] value = dbs.value();
            if (value != null && value.length > 0) {
                return getDatabases(value);
            }
        }
        return getDatabases(Databases.values());
    }

    private Stream<? extends Arguments> getDatabases(Databases[] dbs) {
        return Stream.of(dbs).map(DatabaseProvider::getDatabase).map(Arguments::of);
    }

}
