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
        while (count < retries && !done)
        try {
            this.wrapped.writeContent(stringContent);
            done = true;
        } catch (SQLException exception) {
            logger.error(exception.getMessage());
            if (exception.getMessage().contains("Lock wait timeout exceeded")) {
                count ++;
            } else {
                throw exception;
            }
        }
    }
    
    public void waitForWrite() throws SQLException, JFleetException {
        this.wrapped.waitForWrite();
    }

}
