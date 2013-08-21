package org.agilewiki.jactor2.core;

import org.agilewiki.jactor2.core.context.JAContext;
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
     * @param _context The actor's context.
     */
    public Delay(final JAContext _context) throws Exception {
        initialize(new AtomicMessageProcessor(_context));
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
