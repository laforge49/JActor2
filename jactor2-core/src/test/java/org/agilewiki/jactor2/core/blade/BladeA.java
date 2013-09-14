package org.agilewiki.jactor2.core.blade;

import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.reactors.Reactor;

public class BladeA {
    private final Reactor reactor;
    public final AsyncRequest<Void> throwRequest;

    public BladeA(final Reactor mbox) {
        this.reactor = mbox;

        throwRequest = new AsyncRequest<Void>(reactor) {
            @Override
            public void processAsyncRequest()
                    throws Exception {
                throw new SecurityException("thrown on request");
            }
        };
    }
}
