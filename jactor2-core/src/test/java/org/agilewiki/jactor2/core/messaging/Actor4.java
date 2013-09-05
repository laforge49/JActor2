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
            Request<Void> dis = this;

            @Override
            public void processRequest()
                    throws Exception {
                actor1.hi.send(messageProcessor, new ResponseProcessor<String>() {
                    @Override
                    public void processResponse(final String response)
                            throws Exception {
                        System.out.println(response);
                        dis.processResponse(null);
                    }
                });
            }
        };
    }
}
