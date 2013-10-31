package org.agilewiki.jactor2.core.blades.misc;

import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;

/**
 * Simulates I/O.
 */
public class Delay extends BladeBase {

    /**
     * Create a Delay blades.
     *
     * @param _facility The blades's facility.
     */
    public Delay(final Facility _facility) throws Exception {
        initialize(new IsolationReactor(_facility));
    }

    /**
     * Create a Delay blades.
     *
     * @param _isolationReactor The blades's facility.
     */
    public Delay(final IsolationReactor _isolationReactor) throws Exception {
        initialize(_isolationReactor);
    }

    /**
     * Returns a delay request.
     *
     * @param _delay The duration of the delay in milliseconds.
     * @return The delay request.
     */
    public SyncRequest<Void> sleepSReq(final long _delay) {
        return new SyncBladeRequest<Void>() {
            @Override
            protected Void processSyncRequest()
                    throws Exception {
                Thread.sleep(_delay);
                return null;
            }
        };
    }
}
