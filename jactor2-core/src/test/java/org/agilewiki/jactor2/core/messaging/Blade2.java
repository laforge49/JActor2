package org.agilewiki.jactor2.core.messaging;

import org.agilewiki.jactor2.core.processing.Reactor;

/**
 * Test code.
 */
public class Blade2 {
    private final Reactor reactor;

    public Blade2(final Reactor mbox) {
        this.reactor = mbox;
    }

    public AsyncRequest<String> hi2AReq(final Blade1 blade1) {
        return new AsyncRequest<String>(reactor) {
            @Override
            public void processAsyncRequest()
                    throws Exception {
                blade1.hiSReq().send(messageProcessor, this);
            }
        };
    }
}
