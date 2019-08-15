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
package org.jfleet.mysql;

import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.Optional;

import org.jfleet.JFleetException;
import org.jfleet.common.ContentWriter;
import org.jfleet.common.StringBuilderReader;
import org.jfleet.common.StringContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class LoadDataContentWriter implements ContentWriter {

    private static Logger logger = LoggerFactory.getLogger(LoadDataContentWriter.class);

    private final Statement statement;
    private final MySqlTransactionPolicy txPolicy;
    private final String mainSql;
    private final Charset charset;

    LoadDataContentWriter(Statement statement, MySqlTransactionPolicy txPolicy, String mainSql, Charset charset) {
        this.statement = statement;
        this.txPolicy = txPolicy;
        this.mainSql = mainSql;
        this.charset = charset;
    }

    @Override
    public void writeContent(StringContent stringContent) throws SQLException, JFleetException {
        int contentSize = stringContent.getContentSize();
        if (contentSize > 0) {
            long init = System.nanoTime();
            ReaderInputStream ris = new ReaderInputStream(new StringBuilderReader(stringContent.getContent()),
                    charset);
            statement.setLocalInfileInputStream(ris);
            statement.execute(mainSql);
            logger.debug("{} ms writing {} bytes for {} records", (System.nanoTime() - init) / 1_000_000,
                    contentSize, stringContent.getRecords());
            Optional<Long> updatedInDB = statement.getUpdatedRows();
            int processed = stringContent.getRecords();
            txPolicy.commit(processed, updatedInDB);
        }
    }

}
