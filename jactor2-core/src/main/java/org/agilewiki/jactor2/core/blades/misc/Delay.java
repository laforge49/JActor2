package org.agilewiki.jactor2.core.blades.misc;

import org.agilewiki.jactor2.core.blades.BlockingBladeBase;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.reactors.BlockingReactor;

/**
 * Simulates I/O.
 */
public class Delay extends BlockingBladeBase {

    public Delay(final Plant _plant) throws Exception {
        initialize(new BlockingReactor(_plant));
    }

    public Delay(final Facility _facility) throws Exception {
        initialize(new BlockingReactor(_facility));
    }

    /**
     * Create a Delay blades.
     *
     * @param _reactor The blades's facility.
     */
    public Delay(final BlockingReactor _reactor) throws Exception {
        initialize(_reactor);
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
            protected Void processSyncRequest() throws Exception {
                Thread.sleep(_delay);
                return null;
            }
        };
    }
}
