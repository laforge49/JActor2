package org.agilewiki.jactor.util.firehose;

import java.util.concurrent.Semaphore;

public interface Stage {

    Object process(final Object data);

    void acquire() throws InterruptedException;

    void release();
}
