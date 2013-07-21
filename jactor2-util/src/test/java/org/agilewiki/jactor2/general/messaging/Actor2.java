package org.agilewiki.jactor2.general.messaging;

import org.agilewiki.jactor2.api.Mailbox;
import org.agilewiki.jactor2.api.Request;
import org.agilewiki.jactor2.api.Transport;

/**
 * Test code.
 */
public class Actor2 {
    private final Mailbox mailbox;

    public Actor2(final Mailbox mbox) {
        this.mailbox = mbox;
    }

    public Request<String> hi2(final Actor1 actor1) {
        return new Request<String>(mailbox) {
            @Override
            public void processRequest(
                    final Transport<String> responseProcessor)
                    throws Exception {
                actor1.hi.send(mailbox, responseProcessor);
            }
        };
    }
}
