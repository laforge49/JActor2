package org.agilewiki.jactor2.general.exceptions;

import org.agilewiki.jactor2.api.Mailbox;
import org.agilewiki.jactor2.api.BoundRequest;
import org.agilewiki.jactor2.api.BoundRequestBase;
import org.agilewiki.jactor2.api.Transport;

public class ActorA {
    private final Mailbox mailbox;
    public final BoundRequest<Void> throwBoundRequest;

    public ActorA(final Mailbox mbox) {
        this.mailbox = mbox;

        throwBoundRequest = new BoundRequestBase<Void>(mailbox) {
            @Override
            public void processRequest(
                    final Transport<Void> responseProcessor)
                    throws Exception {
                throw new SecurityException("thrown on boundRequest");
            }
        };
    }
}
