package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.reactors.ThreadBoundReactor;

/**
 * <p>
 *     All the requests and responses of a thread-bound blade
 *     are processed on the single thread to which the reactor is bound.
 * </p>
 */
public interface ThreadBoundBlade extends Blade {
    /**
     * Returns the thread-bound reactor used by this blade.
     *
     * @return The ThreadBoundReactor.
     */
    @Override
    ThreadBoundReactor getReactor();
}
