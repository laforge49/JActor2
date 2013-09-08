package org.agilewiki.jactor2.core;

import org.agilewiki.jactor2.core.messaging.AsyncRequest;
import org.agilewiki.jactor2.core.processing.IsolationMessageProcessor;
import org.agilewiki.jactor2.core.threading.ModuleContext;

/**
 * Simulates I/O.
 */
public class Delay extends ActorBase {

    /**
     * Create a Delay actor.
     *
     * @param _context The actor's context.
     */
    public Delay(final ModuleContext _context) throws Exception {
        initialize(new IsolationMessageProcessor(_context));
    }

    /**
     * Returns a delay request.
     *
     * @param _delay The length of the delay in milliseconds.
     * @return The delay request.
     */
    public AsyncRequest<Void> sleepReq(final long _delay) {
        return new AsyncRequest<Void>(getMessageProcessor()) {
            @Override
            public void processRequest()
                    throws Exception {
                Thread.sleep(_delay);
                processResponse(null);
            }
        };
    }
}
