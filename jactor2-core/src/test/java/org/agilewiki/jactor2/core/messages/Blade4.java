package org.agilewiki.jactor2.core.messages;

import org.agilewiki.jactor2.core.reactors.Reactor;

/**
 * Test code.
 */
public class Blade4 {
    private final Reactor reactor;

    public Blade4(final Reactor mbox) {
        this.reactor = mbox;
    }

    public SyncRequest<Void> hi4SReq() {
        return new SyncRequest<Void>(reactor) {
            @Override
            public Void processSyncRequest()
                    throws Exception {
                new Blade1(messageProcessor).hiSReq().local(messageProcessor);
                System.out.println(response);
                return null;
            }
        };
    }
}
