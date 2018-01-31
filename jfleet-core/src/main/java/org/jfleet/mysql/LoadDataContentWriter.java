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

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.Optional;

import org.jfleet.JFleetException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysql.jdbc.ResultsetInspector;
import com.mysql.jdbc.Statement;

public class LoadDataContentWriter {

    private static Logger logger = LoggerFactory.getLogger(LoadDataContentWriter.class);

    private final Statement statement;
    private final MySqlTransactionPolicy txPolicy;
    private final String mainSql;
    private final Charset encoding;

    public LoadDataContentWriter(Statement statement, MySqlTransactionPolicy txPolicy, String mainSql,
            Charset encoding) {
        this.statement = statement;
        this.txPolicy = txPolicy;
        this.mainSql = mainSql;
        this.encoding = encoding;
    }

    public void writeContent(FileContentBuilder contentBuilder) throws SQLException, JFleetException {
        if (contentBuilder.getContentSize() > 0) {
            long init = System.nanoTime();
            String content = contentBuilder.getContent();
            statement.setLocalInfileInputStream(new ByteArrayInputStream(content.getBytes(encoding)));
            statement.execute(mainSql);
            logger.debug("{} ms writing {} bytes for {} records", (System.nanoTime() - init) / 1_000_000,
                    contentBuilder.getContentSize(), contentBuilder.getRecords());
            Optional<Long> updatedInDB = ResultsetInspector.getUpdatedRows(statement);
            int processed = contentBuilder.getRecords();
            contentBuilder.reset();
            txPolicy.commit(processed, updatedInDB);
        }
    }

}