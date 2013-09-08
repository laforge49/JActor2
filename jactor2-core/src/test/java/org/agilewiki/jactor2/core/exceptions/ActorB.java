package org.agilewiki.jactor2.core.exceptions;

import org.agilewiki.jactor2.core.messaging.AsyncRequest;
import org.agilewiki.jactor2.core.processing.MessageProcessor;

public class ActorB {
    private final MessageProcessor messageProcessor;

    public ActorB(final MessageProcessor mbox) {
        this.messageProcessor = mbox;
    }

    public AsyncRequest<Void> throwRequest(final ActorA actorA) {
        return new AsyncRequest<Void>(messageProcessor) {
            AsyncRequest<Void> dis = this;

            @Override
            public void processRequest()
                    throws Exception {
                actorA.throwRequest.send(messageProcessor, this);
            }
        };
    }
}
