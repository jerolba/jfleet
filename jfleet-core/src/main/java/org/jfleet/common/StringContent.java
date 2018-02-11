package org.jfleet.common;

public class StringContent {
    
    private final StringBuilder sb;
    private final int batchSize;
    private int records;

    public StringContent(int batchSize) {
        this.sb = new StringBuilder(batchSize + Math.min(1024, batchSize / 1000));
        this.batchSize = batchSize;
        this.records = 0;
    }
    
    public void append(char c) {
        sb.append(c);
    }
    
    public void append(String value) {
        sb.append(value);
    }
    
    public void inc() {
        this.records++;
    }
    
    public boolean isFilled() {
        return sb.length() > batchSize;
    }
    
    public void reset() {
        this.sb.setLength(0);
        this.records = 0;
    }

    public int getContentSize() {
        return sb.length();
    }

    public StringBuilder getContent() {
        return sb;
    }
    
    public int getRecords() {
        return records;
    }
 
}
