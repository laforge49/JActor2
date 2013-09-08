package org.agilewiki.jactor2.core.messaging;

import org.agilewiki.jactor2.core.processing.MessageProcessor;

/**
 * Test code.
 */
public class Actor2 {
    private final MessageProcessor messageProcessor;

    public Actor2(final MessageProcessor mbox) {
        this.messageProcessor = mbox;
    }

    public AsyncRequest<String> hi2(final Actor1 actor1) {
        return new AsyncRequest<String>(messageProcessor) {
            @Override
            public void processAsyncRequest()
                    throws Exception {
                actor1.hi.send(messageProcessor, this);
            }
        };
    }
}
