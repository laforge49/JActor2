package org.agilewiki.core.messaging;

import org.agilewiki.jactor2.core.Mailbox;
import org.agilewiki.jactor2.core.Request;
import org.agilewiki.jactor2.core.Transport;

/**
 * Test code.
 */
public class Actor3 {
    private final Mailbox mailbox;
    public final Request<Void> hi3;

    public Actor3(final Mailbox mbox) {
        this.mailbox = mbox;

        hi3 = new Request<Void>(mailbox) {
            @Override
            public void processRequest(
                    final Transport<Void> responseProcessor)
                    throws Exception {
                System.out.println("Hello world!");
                responseProcessor.processResponse(null);
            }
        };
    }
}
