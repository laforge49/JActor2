package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

/**
 * <p>
 *    When the messages passed to a non-blocking blade are processed, control must be returned
 *    quickly, without excessive computation and without blocking the thread. This is the most common
 *    type of blade.
 * </p>
 * <p>
 *     Note that when an asynchronous request is passed to a blade, control is often returned before the
 *     result. And there is no time-limit on how quickly a response must be returned.
 * </p>
 */
public interface NonBlockingBlade extends Blade {
    /**
     * Returns the non-blocking reactor used by this blade.
     *
     * @return The NonBlockingReactor.
     */
    @Override
    NonBlockingReactor getReactor();
}
