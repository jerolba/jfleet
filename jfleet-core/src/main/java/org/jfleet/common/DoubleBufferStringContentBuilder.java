package org.jfleet.common;

public class DoubleBufferStringContentBuilder {

    private DoubleBufferStringContent doubleBuffer;
    protected StringContent stringContent;

    public DoubleBufferStringContentBuilder(int batchSize, boolean concurrent) {
        this.doubleBuffer = new DoubleBufferStringContent(batchSize, concurrent);
        this.stringContent = doubleBuffer.next();
    }

    public void reset() {
        this.stringContent = doubleBuffer.next();
    }

    public boolean isFilled() {
        return stringContent.isFilled();
    }

    public int getContentSize() {
        return stringContent.getContentSize();
    }

    public int getRecords() {
        return stringContent.getRecords();
    }

    public StringContent getContent() {
        return stringContent;
    }

}
