package org.agilewiki.jactor2.general.messaging;

import org.agilewiki.jactor2.api.Mailbox;
import org.agilewiki.jactor2.api.Request;
import org.agilewiki.jactor2.api.RequestBase;
import org.agilewiki.jactor2.api.Transport;

/**
 * Test code.
 */
public class Actor1 {
    private final Mailbox mailbox;
    public final Request<String> hi1;

    public Actor1(final Mailbox mbox) {
        this.mailbox = mbox;

        hi1 = new RequestBase<String>(mailbox) {
            @Override
            public void processRequest(
                    final Transport<String> responseProcessor)
                    throws Exception {
                responseProcessor.processResponse("Hello world!");
            }
        };
    }
}
