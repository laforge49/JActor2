package org.agilewiki.jactor2.core.messaging;

import org.agilewiki.jactor2.core.processing.Reactor;

/**
 * Test code.
 */
public class Actor4 {
    private final Reactor reactor;

    public Actor4(final Reactor mbox) {
        this.reactor = mbox;
    }

    public SyncRequest<Void> hi4SReq() {
        return new SyncRequest<Void>(reactor) {
            @Override
            public Void processSyncRequest()
                    throws Exception {
                new Actor1(messageProcessor).hiSReq().local(messageProcessor);
                System.out.println(response);
                return null;
            }
        };
    }
}
