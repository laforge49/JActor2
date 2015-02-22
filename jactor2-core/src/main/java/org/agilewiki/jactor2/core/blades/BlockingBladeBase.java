package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.GwtIncompatible;
import org.agilewiki.jactor2.core.reactors.BlockingReactor;

/**
 * <p>
 * A blocking blade performs long computations, I/O or otherwise may block the thread.
 * </p>
 */
@GwtIncompatible
public class BlockingBladeBase extends BladeBase implements BlockingBlade {
    /**
     * Create a Blocking blade and a Blocking reactor whose parent is the internal reactor of Plant.
     */
    public BlockingBladeBase() throws Exception {
        _initialize(new BlockingReactor());
    }

    /**
     * Create a Blocking blade.
     *
     * @param _reactor The blade's facility.
     */
    public BlockingBladeBase(final BlockingReactor _reactor) {
        _initialize(_reactor);
    }

    @Override
    public BlockingReactor getReactor() {
        return (BlockingReactor) super.getReactor();
    }
}
