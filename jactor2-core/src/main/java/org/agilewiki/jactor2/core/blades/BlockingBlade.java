package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.GwtIncompatible;
import org.agilewiki.jactor2.core.reactors.BlockingReactor;

/**
 * <p>
 *     Blocking blades are used to perform I/O or long computations. And they rarely share a
 *     reactor, for when a blocking blade blocks its thread, all other blades with the same
 *     reactor are blocked from processing any requests or responses.
 * </p>
 */
@GwtIncompatible
public interface BlockingBlade extends Blade {
    /**
     * Returns the blocking reactor used by this blade.
     *
     * @return The BlockingReactor.
     */
    @Override
    BlockingReactor getReactor();
}
