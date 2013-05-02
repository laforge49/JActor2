package org.agilewiki.jactor.general.exceptions;

import org.agilewiki.jactor.api.Mailbox;
import org.agilewiki.jactor.api.Request;
import org.agilewiki.jactor.api.RequestBase;
import org.agilewiki.jactor.api.Transport;

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
