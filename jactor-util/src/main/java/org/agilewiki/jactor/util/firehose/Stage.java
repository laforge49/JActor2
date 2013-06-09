package org.agilewiki.jactor.util.firehose;

import org.agilewiki.jactor.api.Mailbox;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public interface Stage {

    Object process(final Engine _engine, final Object data);

    void acquire() throws InterruptedException;

    void release();

    int availablePermits();

    void makeReservation(final Engine _engine);

    void clearReservation();
}
