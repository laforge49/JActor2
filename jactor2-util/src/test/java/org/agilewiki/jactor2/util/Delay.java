package org.agilewiki.jactor2.util;

import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.messaging.Request;
import org.agilewiki.jactor2.core.messaging.Transport;
import org.agilewiki.jactor2.core.processing.AtomicMessageProcessor;
import org.agilewiki.jactor2.core.processing.MessageProcessor;

public class Delay {
    private final MessageProcessor messageProcessor;

    public Delay(final JAContext jaContext) {
        messageProcessor = new AtomicMessageProcessor(jaContext);
    }

    public Request<Void> sleepReq(final long _delay) {
        return new Request<Void>(messageProcessor) {
            @Override
            public void processRequest(
                    final Transport<Void> responseProcessor)
                    throws Exception {
                Thread.sleep(_delay);
                responseProcessor.processResponse(null);
            }
        };
    }
}
