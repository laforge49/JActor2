package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.GwtIncompatible;
import org.agilewiki.jactor2.core.reactors.ThreadBoundReactor;

/**
 * <p>
 *     All the requests and responses of a thread-bound blade
 *     are processed on the single thread to which the reactor is bound.
 * </p>
 */
@GwtIncompatible
public class ThreadBoundBladeBase extends BladeBase implements ThreadBoundBlade {
    /**
     * Create a Thread bound blade and a Thread bound reactor whose parent is the internal reactor of Plant.
     */
    public ThreadBoundBladeBase() throws Exception {
        _initialize(new ThreadBoundReactor());
    }

    /**
     * Create a Thread bound blade.
     *
     * @param _reactor The blade's facility.
     */
    public ThreadBoundBladeBase(final ThreadBoundReactor _reactor) {
        _initialize(_reactor);
    }

    @Override
    public ThreadBoundReactor getReactor() {
        return (ThreadBoundReactor) super.getReactor();
    }
}
