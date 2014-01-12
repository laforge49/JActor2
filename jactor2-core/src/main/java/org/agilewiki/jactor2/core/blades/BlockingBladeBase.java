package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.reactors.BlockingReactor;

/**
 * <p>
 * A blocking blade performs long computations, I/O or otherwise may block the thread.
 * </p><p>
 * This is just a convenience class, as any blades which uses a blocking targetReactor
 * is a blocking blades.
 * </p>
 */
public class BlockingBladeBase extends BladeBase implements BlockingBlade {
    public BlockingBladeBase() throws Exception {
        _initialize(new BlockingReactor());
    }

    public BlockingBladeBase(final BlockingReactor _reactor) throws Exception {
        _initialize(_reactor);
    }

    @Override
    public BlockingReactor getReactor() {
        return (BlockingReactor) super.getReactor();
    }
}
