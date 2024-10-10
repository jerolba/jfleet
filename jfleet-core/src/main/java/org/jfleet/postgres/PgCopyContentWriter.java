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

import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;

import org.jfleet.WrappedException;
import org.jfleet.common.ContentWriter;
import org.jfleet.common.StringBuilderReader;
import org.jfleet.common.StringContent;
import org.jfleet.common.TransactionPolicy;
import org.postgresql.copy.CopyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PgCopyContentWriter implements ContentWriter {

    private static Logger logger = LoggerFactory.getLogger(PgCopyContentWriter.class);

    private final TransactionPolicy txPolicy;
    private final CopyManager copyManager;
    private final String mainSql;

    PgCopyContentWriter(TransactionPolicy txPolicy, CopyManager copyManager, String mainSql) {
        this.txPolicy = txPolicy;
        this.copyManager = copyManager;
        this.mainSql = mainSql;
    }

    @Override
    public void writeContent(StringContent stringContent) throws SQLException {
        int contentSize = stringContent.getContentSize();
        if (contentSize > 0) {
            try {
                long init = System.nanoTime();
                Reader reader = new StringBuilderReader(stringContent.getContent());
                copyManager.copyIn(mainSql, reader);
                logger.debug("{} ms writing {} bytes for {} records", (System.nanoTime() - init) / 1_000_000,
                        contentSize, stringContent.getRecords());
                stringContent.reset();
                txPolicy.commit();
            } catch (IOException e) {
                throw new WrappedException(e);
            }
        }
    }

}
