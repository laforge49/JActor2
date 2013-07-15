package org.agilewiki.jactor2.general.messaging;

import org.agilewiki.jactor2.api.*;

/**
 * Test code.
 */
public class Actor1 extends ActorBase {
    public final Request<String> hi;

    public Actor1(final Mailbox mbox) throws Exception {
        initialize(mbox);
        hi = new RequestBase<String>(getMailbox()) {
            @Override
            public void processRequest(Transport<String> _transport) throws Exception {
                _transport.processResponse("Hello world!");
            }
        };
    }
}
