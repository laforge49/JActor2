package org.agilewiki.jactor2.core.exceptions;

import org.agilewiki.jactor2.core.messaging.Request;
import org.agilewiki.jactor2.core.messaging.Transport;
import org.agilewiki.jactor2.core.processing.MessageProcessor;

public class ActorA {
    private final MessageProcessor messageProcessor;
    public final Request<Void> throwRequest;

    public ActorA(final MessageProcessor mbox) {
        this.messageProcessor = mbox;

        throwRequest = new Request<Void>(messageProcessor) {
            @Override
            public void processRequest(
                    final Transport<Void> responseProcessor)
                    throws Exception {
                throw new SecurityException("thrown on request");
            }
        };
    }
}
