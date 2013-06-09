package org.agilewiki.jactor.util.firehose;

import org.agilewiki.jactor.api.Mailbox;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public interface Stage {

    Object process(final Mailbox _mailbox, final Object data);

    void acquire() throws InterruptedException;

    void release();

    boolean tryAcquire(long timeout, TimeUnit unit)
            throws InterruptedException;
}
