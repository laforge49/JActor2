package org.agilewiki.jactor2.core.misc;

import org.agilewiki.jactor2.core.ActorBase;
import org.agilewiki.jactor2.core.messaging.SyncRequest;
import org.agilewiki.jactor2.core.processing.IsolationReactor;
import org.agilewiki.jactor2.core.threading.Facility;

/**
 * Simulates I/O.
 */
public class Delay extends ActorBase {

    /**
     * Create a Delay actor.
     *
     * @param _facility The actor's facility.
     */
    public Delay(final Facility _facility) throws Exception {
        initialize(new IsolationReactor(_facility));
    }

    /**
     * Returns a delay request.
     *
     * @param _delay The duration of the delay in milliseconds.
     * @return The delay request.
     */
    public SyncRequest<Void> sleepSReq(final long _delay) {
        return new SyncRequest<Void>(getReactor()) {
            @Override
            public Void processSyncRequest()
                    throws Exception {
                Thread.sleep(_delay);
                return null;
            }
        };
    }
}
