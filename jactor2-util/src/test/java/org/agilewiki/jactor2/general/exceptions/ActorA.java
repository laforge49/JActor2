package org.agilewiki.jactor2.general.exceptions;

import org.agilewiki.jactor2.core.Mailbox;
import org.agilewiki.jactor2.core.Request;
import org.agilewiki.jactor2.core.Transport;

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
