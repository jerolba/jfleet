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
package com.mysql.cj.jdbc;

import java.io.InputStream;

import com.mysql.cj.jdbc.result.ResultSetInternalMethods;

public class StatementImpl implements JdbcStatement {

    @Override
    public void close() {
    }

    @Override
    public void setLocalInfileInputStream(InputStream is) {
    }

    @Override
    public void execute(String sql) {
    }

    public ResultSetInternalMethods getResultSetInternal() {
        return null;
    }

}
