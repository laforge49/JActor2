package org.agilewiki.jactor2.util;

import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.messaging.Request;
import org.agilewiki.jactor2.core.messaging.Transport;
import org.agilewiki.jactor2.core.processing.AtomicMailbox;
import org.agilewiki.jactor2.core.processing.Mailbox;

public class Delay {
    private final Mailbox mailbox;

    public Delay(final JAContext jaContext) {
        mailbox = new AtomicMailbox(jaContext);
    }

    public Request<Void> sleepReq(final long _delay) {
        return new Request<Void>(mailbox) {
            @Override
            public void processRequest(
                    final Transport<Void> responseProcessor)
                    throws Exception {
                Thread.sleep(_delay);
                responseProcessor.processResponse(null);
            }
        };
    }
}
