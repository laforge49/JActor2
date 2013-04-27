package org.agilewiki.pactor.general.messaging;

import org.agilewiki.pactor.api.Mailbox;
import org.agilewiki.pactor.api.Request;
import org.agilewiki.pactor.api.RequestBase;
import org.agilewiki.pactor.api.Transport;

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
