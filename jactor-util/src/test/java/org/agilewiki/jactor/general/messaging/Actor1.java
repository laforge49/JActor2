package org.agilewiki.jactor.general.messaging;

import org.agilewiki.jactor.api.Mailbox;
import org.agilewiki.jactor.api.Request;
import org.agilewiki.jactor.api.RequestBase;
import org.agilewiki.jactor.api.Transport;

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
