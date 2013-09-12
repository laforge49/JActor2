package org.agilewiki.jactor2.core.exceptions;

import org.agilewiki.jactor2.core.messaging.AsyncRequest;
import org.agilewiki.jactor2.core.processing.Reactor;

public class ActorB {
    private final Reactor reactor;

    public ActorB(final Reactor mbox) {
        this.reactor = mbox;
    }

    public AsyncRequest<Void> throwRequest(final ActorA actorA) {
        return new AsyncRequest<Void>(reactor) {
            AsyncRequest<Void> dis = this;

            @Override
            public void processAsyncRequest()
                    throws Exception {
                actorA.throwRequest.send(messageProcessor, this);
            }
        };
    }
}
