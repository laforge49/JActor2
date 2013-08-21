package org.agilewiki.jactor2.core;

import org.agilewiki.jactor2.core.messaging.Request;
import org.agilewiki.jactor2.core.messaging.Transport;
import org.agilewiki.jactor2.core.processing.AtomicMessageProcessor;

/**
 * Simulated I/O
 */
public class Delay extends ActorBase {

    /**
     * Create a Delay actor.
     *
     * @param _messageProcessor The actor's message processor.
     */
    public Delay(final AtomicMessageProcessor _messageProcessor) throws Exception {
        initialize(_messageProcessor);
    }

    /**
     * Returns a delay request.
     *
     * @param _delay The length of the delay in milliseconds.
     * @return The delay request.
     */
    public Request<Void> sleepReq(final long _delay) {
        return new Request<Void>(getMessageProcessor()) {
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
