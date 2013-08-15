package org.agilewiki.core.messaging;

import org.agilewiki.jactor2.core.messaging.Request;
import org.agilewiki.jactor2.core.messaging.Transport;
import org.agilewiki.jactor2.core.processing.MessageProcessor;

/**
 * Test code.
 */
public class Actor2 {
    private final MessageProcessor messageProcessor;

    public Actor2(final MessageProcessor mbox) {
        this.messageProcessor = mbox;
    }

    public Request<String> hi2(final Actor1 actor1) {
        return new Request<String>(messageProcessor) {
            @Override
            public void processRequest(
                    final Transport<String> responseProcessor)
                    throws Exception {
                actor1.hi.send(messageProcessor, responseProcessor);
            }
        };
    }
}
