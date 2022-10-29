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
package org.jfleet;

import java.sql.SQLException;

public class WrappedException extends RuntimeException {

    private static final long serialVersionUID = 5687915465143634780L;

    public WrappedException(Exception e) {
        super(e);
    }

    public void rethrow() throws JFleetException, SQLException {
        if (getCause() instanceof SQLException) {
            throw (SQLException) this.getCause();
        }
        throw new JFleetException(this.getCause());
    }

}