package org.agilewiki.jactor2.core.blade;

import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.reactors.Reactor;

public class BladeB {
    private final Reactor reactor;

    public BladeB(final Reactor mbox) {
        this.reactor = mbox;
    }

    public AsyncRequest<Void> throwRequest(final BladeA bladeA) {
        return new AsyncRequest<Void>(reactor) {
            AsyncRequest<Void> dis = this;

            @Override
            protected void processAsyncRequest()
                    throws Exception {
                bladeA.throwRequest.send(messageProcessor, this);
            }
        };
    }
}
