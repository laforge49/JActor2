package org.agilewiki.jactor2.util;

import org.agilewiki.jactor2.api.*;

public class Delay {
    private final Mailbox mailbox;

    public Delay(final MailboxFactory mailboxFactory) {
        mailbox = mailboxFactory.createMayBlockMailbox();
    }

    public BoundRequest<Void> sleepReq(final long _delay) {
        return new BoundRequestBase<Void>(mailbox) {
            @Override
            public void processRequest(
                    final Transport<Void> responseProcessor)
                    throws Exception {
                Thread.sleep(_delay);
                responseProcessor.processResponse(null);
            }
        };
    }
}
