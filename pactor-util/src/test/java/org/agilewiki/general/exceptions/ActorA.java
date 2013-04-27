package org.agilewiki.general.exceptions;

import org.agilewiki.pactor.api.Mailbox;
import org.agilewiki.pactor.api.Request;
import org.agilewiki.pactor.api.RequestBase;
import org.agilewiki.pactor.api.Transport;

public class ActorA {
    private final Mailbox mailbox;
    public final Request<Void> throwRequest;

    public ActorA(final Mailbox mbox) {
        this.mailbox = mbox;

        throwRequest = new RequestBase<Void>(mailbox) {
            @Override
            public void processRequest(
                    final Transport<Void> responseProcessor)
                    throws Exception {
                throw new SecurityException("thrown on request");
            }
        };
    }
}
