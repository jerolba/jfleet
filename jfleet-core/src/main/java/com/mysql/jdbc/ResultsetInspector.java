package com.mysql.jdbc;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResultsetInspector {

    private static Logger logger = LoggerFactory.getLogger(ResultsetInspector.class);

    public static Optional<Long> getUpdatedRows(Statement statement) {
        if (statement instanceof com.mysql.jdbc.StatementImpl) {
            StatementImpl impl = (StatementImpl) statement;
            ResultSetInternalMethods resultSetInternal = impl.getResultSetInternal();
            return Optional.of(resultSetInternal.getUpdateCount());
        }
        logger.warn("Mysql connection does not create com.mysql.jdbc.StatementImpl statements, needed to fetch inserted rows. JFleet can not check if all rows are inserted.");
        return Optional.empty();
    }
}
