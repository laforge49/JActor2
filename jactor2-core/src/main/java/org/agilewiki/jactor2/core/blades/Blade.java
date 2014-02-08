package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.reactors.Reactor;

/**
 * <p>
 * A blade is an object with state operated on by requests
 * within the thread context of a single reactor.
 * </p>
 */
public interface Blade {
    /**
     * Returns the reactor used by the requests of this blade.
     *
     * @return The Reactor.
     */
    Reactor getReactor();
}
