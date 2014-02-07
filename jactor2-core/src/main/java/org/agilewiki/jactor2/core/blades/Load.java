package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.blades.BlockingBladeBase;
import org.agilewiki.jactor2.core.reactors.BlockingReactor;
import org.agilewiki.jactor2.core.requests.SyncRequest;

/**
 * Simulates a load.
 */
public class Load extends BlockingBladeBase {
    private volatile long i;
    private volatile long j;

    /**
     * Create a Load blade and a Blocking reactor whose parent is the internal reactor of Plant.
     */
    public Load() throws Exception {
    }

    /**
     * Create a Load blade.
     *
     * @param _reactor The blade's facility.
     */
    public Load(final BlockingReactor _reactor) throws Exception {
        super(_reactor);
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
            public Void processSyncRequest() throws Exception {
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
}
