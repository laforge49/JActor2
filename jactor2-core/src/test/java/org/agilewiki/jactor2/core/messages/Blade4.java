package org.agilewiki.jactor2.core.messages;

import org.agilewiki.jactor2.core.reactors.Reactor;

/**
 * Test code.
 */
public class Blade4 {
    private final Reactor reactor;

    public Blade4(final Reactor mbox) {
        reactor = mbox;
    }

    public <RESPONSE_TYPE> RESPONSE_TYPE local(final SyncRequest<RESPONSE_TYPE> _syncRequest) throws Exception {
        return SyncRequest.doLocal(reactor, _syncRequest);
    }

    public SyncRequest<Void> hi4SReq() {
        return new SyncRequest<Void>(reactor) {
            @Override
            protected Void processSyncRequest()
                    throws Exception {
                local(new Blade1(messageProcessor).hiSReq());
                System.out.println(response);
                return null;
            }
        };
    }
}
