package org.agilewiki.jactor2.core.messaging;

import org.agilewiki.jactor2.core.ActorBase;
import org.agilewiki.jactor2.core.processing.MessageProcessor;

/**
 * Test code.
 */
public class Actor1 extends ActorBase {
    public final AsyncRequest<String> hi;

    public Actor1(final MessageProcessor mbox) throws Exception {
        initialize(mbox);
        hi = new AsyncRequest<String>(getMessageProcessor()) {
            @Override
            public void processRequest() throws Exception {
                processResponse("Hello world!");
            }
        };
    }
}
