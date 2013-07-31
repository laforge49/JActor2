package org.agilewiki.core.exceptions;

import org.agilewiki.jactor2.core.mailbox.Mailbox;
import org.agilewiki.jactor2.core.messaging.Request;
import org.agilewiki.jactor2.core.messaging.Transport;

public class ActorA {
    private final Mailbox mailbox;
    public final Request<Void> throwRequest;

    public ActorA(final Mailbox mbox) {
        this.mailbox = mbox;

        throwRequest = new Request<Void>(mailbox) {
            @Override
            public void processRequest(
                    final Transport<Void> responseProcessor)
                    throws Exception {
                throw new SecurityException("thrown on request");
            }
        };
    }
}
