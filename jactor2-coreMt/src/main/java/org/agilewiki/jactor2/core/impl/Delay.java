package org.agilewiki.jactor2.core.impl;

import org.agilewiki.jactor2.core.blades.BlockingBladeBase;
import org.agilewiki.jactor2.core.messages.SOp;
import org.agilewiki.jactor2.core.messages.impl.RequestImpl;
import org.agilewiki.jactor2.core.reactors.BlockingReactor;

/**
 * Simulates I/O through the use of Thread.sleep().
 * For a delay use DelayAOp instead, as it does not block the thread.
 */
public class Delay extends BlockingBladeBase {
    /**
     * Create a Delay blade and a Blocking reactor whose parent is the internal reactor of Plant.
     */
    public Delay() throws Exception {
    }

    /**
     * Create a Delay blade.
     *
     * @param _reactor The blade's facility.
     */
    public Delay(final BlockingReactor _reactor) {
        super(_reactor);
    }

    /**
     * Returns a delay request.
     *
     * @param _delay The duration of the delay in milliseconds.
     * @return The delay request.
     */
    public SOp<Void> sleepSOp(final int _delay) {
        return new SOp<Void>("sleep", getReactor()) {
            @Override
            protected Void processSyncOperation(RequestImpl _requestImpl) throws Exception {
                Thread.sleep(_delay);
                return null;
            }
        };
    }
}
