package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.reactors.Reactor;

/**
 * <p>
 * A blade is an object with persistent state and a Reactor and, typically, requests.
 * Thread safety is achieved by restricting its access to the requests of the blade
 * and to the requests of other blades which share the same reactor.
 * </p>
 */
public interface Blade {
    /**
     * Returns the reactor used by this blade.
     *
     * @return The Reactor.
     */
    Reactor getReactor();
}
