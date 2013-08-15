package org.agilewiki.core.messaging;

import org.agilewiki.jactor2.core.ActorBase;
import org.agilewiki.jactor2.core.messaging.Request;
import org.agilewiki.jactor2.core.messaging.Transport;
import org.agilewiki.jactor2.core.processing.Mailbox;

/**
 * Test code.
 */
public class Actor1 extends ActorBase {
    public final Request<String> hi;

    public Actor1(final Mailbox mbox) throws Exception {
        initialize(mbox);
        hi = new Request<String>(getMailbox()) {
            @Override
            public void processRequest(Transport<String> _transport) throws Exception {
                _transport.processResponse("Hello world!");
            }
        };
    }
}
