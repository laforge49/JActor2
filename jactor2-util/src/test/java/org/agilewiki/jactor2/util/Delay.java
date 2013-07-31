package org.agilewiki.jactor2.util;

import org.agilewiki.jactor2.core.mailbox.Mailbox;
import org.agilewiki.jactor2.core.context.MailboxFactory;
import org.agilewiki.jactor2.core.messaging.Request;
import org.agilewiki.jactor2.core.messaging.Transport;

public class Delay {
    private final Mailbox mailbox;

    public Delay(final MailboxFactory mailboxFactory) {
        mailbox = mailboxFactory.createAtomicMailbox();
    }

    public Request<Void> sleepReq(final long _delay) {
        return new Request<Void>(mailbox) {
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
