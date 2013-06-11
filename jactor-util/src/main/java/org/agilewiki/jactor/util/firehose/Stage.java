package org.agilewiki.jactor.util.firehose;

public interface Stage {

    Object process(final Engine _engine, final Object data);

    void acquire() throws InterruptedException;

    void release();

    int availablePermits();
}
