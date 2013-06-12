package org.agilewiki.jactor.util.firehose;

import org.agilewiki.jactor.api.Mailbox;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

abstract public class StageBase extends Semaphore implements Stage {
    private FirehoseMailbox mailbox;

    public StageBase(final FirehoseMailbox _mailbox) {
        super(1);
        mailbox = _mailbox;
    }

    @Override
    public FirehoseMailbox getMailbox() {
        return mailbox;
    }
}
