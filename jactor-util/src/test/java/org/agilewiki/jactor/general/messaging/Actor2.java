package org.agilewiki.jactor.general.messaging;

import org.agilewiki.jactor.api.Mailbox;
import org.agilewiki.jactor.api.Request;
import org.agilewiki.jactor.api.RequestBase;
import org.agilewiki.jactor.api.Transport;

/**
 * Test code.
 */
public class Actor2 {
    private final Mailbox mailbox;

    public Actor2(final Mailbox mbox) {
        this.mailbox = mbox;
    }

    public Request<String> hi2(final Actor1 actor1) {
        return new RequestBase<String>(mailbox) {
            @Override
            public void processRequest(
                    final Transport<String> responseProcessor)
                    throws Exception {
                actor1.hi1.send(mailbox, responseProcessor);
            }
        };
    }
}
