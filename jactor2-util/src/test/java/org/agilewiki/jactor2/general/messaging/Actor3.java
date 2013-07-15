package org.agilewiki.jactor2.general.messaging;

import org.agilewiki.jactor2.api.BoundRequest;
import org.agilewiki.jactor2.api.Mailbox;
import org.agilewiki.jactor2.api.BoundRequestBase;
import org.agilewiki.jactor2.api.Transport;

/**
 * Test code.
 */
public class Actor3 {
    private final Mailbox mailbox;
    public final BoundRequest<Void> hi3;

    public Actor3(final Mailbox mbox) {
        this.mailbox = mbox;

        hi3 = new BoundRequestBase<Void>(mailbox) {
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
