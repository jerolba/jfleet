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
package org.jfleet.mysql.error;

import java.sql.SQLException;

import org.jfleet.JFleetException;
import org.jfleet.common.ContentWriter;
import org.jfleet.common.StringContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LockTimeoutErrorManager implements ContentWriter {

    private static Logger logger = LoggerFactory.getLogger(LockTimeoutErrorManager.class);

    private final int retries;
    private final ContentWriter wrapped;

    public LockTimeoutErrorManager(ContentWriter wrapped, int retries) {
        this.wrapped = wrapped;
        this.retries = retries;
    }

    @Override
    public void writeContent(StringContent stringContent) throws SQLException, JFleetException {
        int count = 0;
        boolean done = false;
        while (count < retries && !done) {
            try {
                this.wrapped.writeContent(stringContent);
                done = true;
            } catch (SQLException exception) {
                logger.error(exception.getMessage());
                if (exception.getMessage().contains("Lock wait timeout exceeded")) {
                    count++;
                } else {
                    throw exception;
                }
            }
        }
    }

    @Override
    public void waitForWrite() throws SQLException, JFleetException {
        this.wrapped.waitForWrite();
    }

}
