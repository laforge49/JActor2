package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.reactors.BlockingReactor;

/**
 * A blocking blade performs long computations, I/O or otherwise may block the thread.
 * </p>
 * This is just a convenience class, as any blades which uses a blocking targetReactor
 * is a blocking blades.
 */
public class BlockingBlade extends BladeBase {

    /**
     * Create a blocking blades.
     *
     * @param _reactor The reactor used by the blocking blade.
     */
    public BlockingBlade(final BlockingReactor _reactor) throws Exception {
        initialize(_reactor);
    }
}
