package org.agilewiki.jactor2.core.exceptions;

import org.agilewiki.jactor2.core.messaging.AsyncRequest;
import org.agilewiki.jactor2.core.processing.MessageProcessor;

public class ActorA {
    private final MessageProcessor messageProcessor;
    public final AsyncRequest<Void> throwRequest;

    public ActorA(final MessageProcessor mbox) {
        this.messageProcessor = mbox;

        throwRequest = new AsyncRequest<Void>(messageProcessor) {
            @Override
            public void processRequest()
                    throws Exception {
                throw new SecurityException("thrown on request");
            }
        };
    }
}
