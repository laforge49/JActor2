package org.agilewiki.jactor2.core.messaging;

import org.agilewiki.jactor2.core.processing.Reactor;

/**
 * Test code.
 */
public class Actor2 {
    private final Reactor reactor;

    public Actor2(final Reactor mbox) {
        this.reactor = mbox;
    }

    public AsyncRequest<String> hi2AReq(final Actor1 actor1) {
        return new AsyncRequest<String>(reactor) {
            @Override
            public void processAsyncRequest()
                    throws Exception {
                actor1.hiSReq().send(messageProcessor, this);
            }
        };
    }
}
