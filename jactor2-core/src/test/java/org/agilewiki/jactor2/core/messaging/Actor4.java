package org.agilewiki.jactor2.core.messaging;

import org.agilewiki.jactor2.core.processing.MessageProcessor;

/**
 * Test code.
 */
public class Actor4 {
    private final MessageProcessor messageProcessor;

    public Actor4(final MessageProcessor mbox) {
        this.messageProcessor = mbox;
    }

    public AsyncRequest<Void> hi4(final Actor1 actor1) {
        return new AsyncRequest<Void>(messageProcessor) {
            AsyncRequest<Void> dis = this;

            @Override
            public void processAsyncRequest()
                    throws Exception {
                actor1.hi.send(messageProcessor, new AsyncResponseProcessor<String>() {
                    @Override
                    public void processAsyncResponse(final String response)
                            throws Exception {
                        System.out.println(response);
                        dis.processAsyncResponse(null);
                    }
                });
            }
        };
    }
}
