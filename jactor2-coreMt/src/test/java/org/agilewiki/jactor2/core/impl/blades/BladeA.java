package org.agilewiki.jactor2.core.impl.blades;

import java.io.IOException;

import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;

public class BladeA {
    private final Reactor reactor;
    public final AsyncRequest<Void> throwRequest;

    public BladeA(final Reactor mbox) {
        this.reactor = mbox;

        throwRequest = new AsyncRequest<Void>(reactor) {
            @Override
            public void processAsyncRequest() throws Exception {
                throw new IOException("thrown on request");
            }
        };
    }
}
