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

    public Request<Void> hi4(final Actor1 actor1) {
        return new Request<Void>(messageProcessor) {
            @Override
            public void processRequest(
                    final Transport<Void> responseProcessor)
                    throws Exception {
                actor1.hi.send(messageProcessor, new ResponseProcessor<String>() {
                    @Override
                    public void processResponse(final String response)
                            throws Exception {
                        System.out.println(response);
                        responseProcessor.processResponse(null);
                    }
                });
            }
        };
    }
}
