package org.jfleet.postgres;

import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;

import org.jfleet.WrappedException;
import org.jfleet.common.TransactionPolicy;
import org.postgresql.copy.CopyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PgCopyContentWriter {

    private static Logger logger = LoggerFactory.getLogger(PgCopyContentWriter.class);

    private final TransactionPolicy txPolicy;
    private final CopyManager copyManager;
    private final String mainSql;

    public PgCopyContentWriter(TransactionPolicy txPolicy, CopyManager copyManager, String mainSql) {
        this.txPolicy = txPolicy;
        this.copyManager = copyManager;
        this.mainSql = mainSql;
    }

    public void writeContent(StdInContentBuilder contentBuilder) throws SQLException {
        if (contentBuilder.getContentSize() > 0) {
            try {
                long init = System.nanoTime();
                Reader reader = new StringBuilderReader(contentBuilder.getContent());
                copyManager.copyIn(mainSql, reader);
                logger.debug("{} ms writing {} bytes for {} records", (System.nanoTime() - init) / 1_000_000,
                        contentBuilder.getContentSize(), contentBuilder.getRecords());
                contentBuilder.reset();
                txPolicy.commit();
            } catch (IOException e) {
                throw new WrappedException(e);
            }
        }
    }

}
