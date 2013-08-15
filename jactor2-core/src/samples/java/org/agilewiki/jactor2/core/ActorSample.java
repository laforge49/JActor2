package org.agilewiki.jactor2.core;

import org.agilewiki.jactor2.core.processing.Mailbox;

public class ActorSample implements Actor {
    private final Mailbox mailbox;

    ActorSample(final Mailbox _mailbox) {
        mailbox = _mailbox;
    }

    @Override
    public final Mailbox getMailbox() {
        return mailbox;
    }
}
