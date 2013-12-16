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

    /**
     * Create a blocking blades.
     *
     * @param _reactor The reactor used by the blocking blade.
     */
    public BlockingBladeBase(final BlockingReactor _reactor) throws Exception {
        initialize(_reactor);
    }

    @Override
    public BlockingReactor getReactor() {
        return (BlockingReactor) super.getReactor();
    }
}
