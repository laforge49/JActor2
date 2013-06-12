package org.agilewiki.jactor.util.firehose;

import org.agilewiki.jactor.api.Actor;
import org.agilewiki.jactor.api.Mailbox;

public interface Stage extends Actor {

    Object process(final Engine _engine, final Object data);

    void acquire() throws InterruptedException;

    void release();

    int availablePermits();

    @Override
    public FirehoseMailbox getMailbox();
}
