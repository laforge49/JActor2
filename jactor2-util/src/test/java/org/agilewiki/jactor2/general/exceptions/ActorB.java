package org.agilewiki.jactor2.general.exceptions;

import org.agilewiki.jactor2.api.*;
import org.agilewiki.jactor2.api.BoundRequest;
import org.agilewiki.jactor2.api.BoundRequestBase;

public class ActorB {
    private final Mailbox mailbox;

    public ActorB(final Mailbox mbox) {
        this.mailbox = mbox;
    }

    public BoundRequest<Void> throwRequest(final ActorA actorA) {
        return new BoundRequestBase<Void>(mailbox) {
            @Override
            public void processRequest(
                    final Transport<Void> responseProcessor)
                    throws Exception {
                actorA.throwBoundRequest.send(mailbox, responseProcessor);
            }
        };
    }
}
