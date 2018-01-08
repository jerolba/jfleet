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

    public LoadDataContentWriter(Statement statement, MySqlTransactionPolicy txPolicy, String mainSql, Charset encoding) {
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
