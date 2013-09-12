package org.agilewiki.jactor2.core;

import org.agilewiki.jactor2.core.messaging.SyncRequest;
import org.agilewiki.jactor2.core.processing.IsolationMessageProcessor;
import org.agilewiki.jactor2.core.threading.ModuleContext;

/**
 * Simulates a load.
 */
public class Load extends ActorBase {
    private volatile long i;
    private volatile long j;

    /**
     * Create a Load actor.
     *
     * @param _context The actor's context.
     */
    public Load(final ModuleContext _context) throws Exception {
        initialize(new IsolationMessageProcessor(_context));
    }

    /**
     * Returns a load request.
     *
     * @param _load The extent of the simulated load.
     * @return The delay request.
     */
    public SyncRequest<Void> loadSReq(final long _load) {
        return new SyncRequest<Void>(getMessageProcessor()) {
            @Override
            public Void processSyncRequest()
                    throws Exception {
                i = 0;
                while (i < _load) {
                    i++;
                    j = 0;
                    while (j < 1000000) {
                        j ++;
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
