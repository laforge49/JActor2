package org.agilewiki.jactor2.general.messaging;

import org.agilewiki.jactor2.core.ActorBase;
import org.agilewiki.jactor2.core.Mailbox;
import org.agilewiki.jactor2.core.Request;
import org.agilewiki.jactor2.core.Transport;

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
