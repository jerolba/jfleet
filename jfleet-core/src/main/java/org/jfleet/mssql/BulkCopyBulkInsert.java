package org.jfleet.mssql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.stream.Stream;

import org.jfleet.BulkInsert;
import org.jfleet.EntityInfo;
import org.jfleet.JFleetException;
import org.jfleet.mssql.BulkInsertConfiguration.BulkInsertConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.sqlserver.jdbc.SQLServerBulkCopy;
import com.microsoft.sqlserver.jdbc.SQLServerBulkCopyOptions;

public class BulkCopyBulkInsert<T> implements BulkInsert<T> {

    private static Logger logger = LoggerFactory.getLogger(BulkCopyBulkInsert.class);
    
    private final BulkInsertConfiguration cfg;
            
    public BulkCopyBulkInsert(Class<?> clazz) {
        this(BulkInsertConfigurationBuilder.from(clazz).build());
    }

    public BulkCopyBulkInsert(EntityInfo entityInfo) {
        this(BulkInsertConfigurationBuilder.from(entityInfo).build());
    }

    public BulkCopyBulkInsert(BulkInsertConfiguration bulkInsertConfiguration) {
        this.cfg = bulkInsertConfiguration;
        logger.debug("Batch size: {} rows", cfg.getBatchSize());
    }

    @Override
    public void insertAll(Connection conn, Stream<T> stream) throws JFleetException, SQLException {
        JFleetSQLServerBulkRecord fileRecord = new JFleetSQLServerBulkRecord(cfg.getEntityInfo(), stream);
        
        SQLServerBulkCopyOptions opt = new SQLServerBulkCopyOptions();
        opt.setBatchSize(cfg.getBatchSize());
        try (SQLServerBulkCopy copy = new SQLServerBulkCopy(conn)){
            copy.setBulkCopyOptions(opt);
            copy.setDestinationTableName(cfg.getEntityInfo().getTableName());
            copy.writeToServer(fileRecord);
        }
        
    }

}
