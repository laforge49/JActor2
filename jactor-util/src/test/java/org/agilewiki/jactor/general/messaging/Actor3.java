package org.agilewiki.jactor.general.messaging;

import org.agilewiki.jactor.api.Mailbox;
import org.agilewiki.jactor.api.Request;
import org.agilewiki.jactor.api.RequestBase;
import org.agilewiki.jactor.api.Transport;

/**
 * Test code.
 */
public class Actor3 {
    private final Mailbox mailbox;
    public final Request<Void> hi3;

    public Actor3(final Mailbox mbox) {
        this.mailbox = mbox;

        hi3 = new RequestBase<Void>(mailbox) {
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
