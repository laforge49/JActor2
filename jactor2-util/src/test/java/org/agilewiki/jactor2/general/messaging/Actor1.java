package org.agilewiki.jactor2.general.messaging;

import org.agilewiki.jactor2.api.ActorBase;
import org.agilewiki.jactor2.api.Mailbox;
import org.agilewiki.jactor2.api.RequestBase;
import org.agilewiki.jactor2.api.Transport;

/**
 * Test code.
 */
public class Actor1 extends ActorBase {

    public Actor1(final Mailbox mbox) throws Exception {
        initialize(mbox);
    }

    public String hi() {
        return "Hello world!";
    }
}
