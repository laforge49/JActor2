package org.agilewiki.jactor2.core.exceptions;

import org.agilewiki.jactor2.core.messaging.Request;
import org.agilewiki.jactor2.core.processing.MessageProcessor;

public class ActorB {
    private final MessageProcessor messageProcessor;

    public ActorB(final MessageProcessor mbox) {
        this.messageProcessor = mbox;
    }

    public Request<Void> throwRequest(final ActorA actorA) {
        return new Request<Void>(messageProcessor) {
            Request<Void> dis = this;

            @Override
            public void processRequest()
                    throws Exception {
                actorA.throwRequest.send(messageProcessor, this);
            }
        };
    }
}
