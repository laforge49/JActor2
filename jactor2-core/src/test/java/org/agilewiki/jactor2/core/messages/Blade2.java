package org.agilewiki.jactor2.core.messages;

import org.agilewiki.jactor2.core.reactors.Reactor;

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
            protected void processAsyncRequest()
                    throws Exception {
                blade1.hiSReq().send(messageProcessor, this);
            }
        };
    }
}
