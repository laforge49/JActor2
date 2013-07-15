package org.agilewiki.jactor2.general.exceptions;

import org.agilewiki.jactor2.api.Mailbox;
import org.agilewiki.jactor2.api.Request;
import org.agilewiki.jactor2.api.RequestBase;
import org.agilewiki.jactor2.api.Transport;

public class ActorB {
    private final Mailbox mailbox;

    public ActorB(final Mailbox mbox) {
        this.mailbox = mbox;
    }

    public Request<Void> throwRequest(final ActorA actorA) {
        return new RequestBase<Void>(mailbox) {
            @Override
            public void processRequest(
                    final Transport<Void> responseProcessor)
                    throws Exception {
                actorA.throwRequest.send(mailbox, responseProcessor);
            }
        };
    }
}
