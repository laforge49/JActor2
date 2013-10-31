package org.agilewiki.jactor2.core.blades.misc;

import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;

/**
 * Simulates a load.
 */
public class Load extends BladeBase {
    private volatile long i;
    private volatile long j;

    /**
     * Create a Load blades.
     *
     * @param _facility The blades's facility.
     */
    public Load(final Facility _facility) throws Exception {
        initialize(new IsolationReactor(_facility));
    }

    /**
     * Create a Load blades.
     *
     * @param _isolationReactor The blades's facility.
     */
    public Load(final IsolationReactor _isolationReactor) throws Exception {
        initialize(_isolationReactor);
    }

    /**
     * Returns a load request.
     *
     * @param _load The extent of the simulated load.
     * @return The delay request.
     */
    public SyncRequest<Void> loadSReq(final long _load) {
        return new SyncBladeRequest<Void>() {
            @Override
            protected Void processSyncRequest()
                    throws Exception {
                i = 0;
                while (i < _load) {
                    i++;
                    j = 0;
                    while (j < 1000000) {
                        j++;
                    }
                }
                return null;
            }
        };
    }

    public long getI() {
        return i;
    }

    public long getJ() {
        return j;
    }
}
