package org.agilewiki.jactor.util.firehose;

import org.agilewiki.jactor.impl.MailboxImpl;
import org.agilewiki.jactor.impl.MessageQueue;
import org.agilewiki.jactor.util.UtilMailboxFactory;
import org.slf4j.Logger;

import java.util.concurrent.Semaphore;

public class FirehoseMailbox extends MailboxImpl {

    private final Semaphore semaphore = new Semaphore(1);

    public FirehoseMailbox(
            UtilMailboxFactory factory,
            MessageQueue messageQueue,
            Logger _log,
            int _initialBufferSize) {
        super(true, null, null, factory, messageQueue, _log, _initialBufferSize);
    }
    void acquire() throws InterruptedException {
        semaphore.acquire();
    }

    void release() {
        semaphore.release();
    }

    int availablePermits() {
        return semaphore.availablePermits();
    }

    public void run() {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            return; //closing
        }
        super.run();
        semaphore.release();
    }
}
