package org.agilewiki.jactor2.core.exceptions;

import org.agilewiki.jactor2.core.messaging.AsyncRequest;
import org.agilewiki.jactor2.core.processing.Reactor;

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
