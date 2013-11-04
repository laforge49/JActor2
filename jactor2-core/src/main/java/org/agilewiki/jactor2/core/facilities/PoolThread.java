package org.agilewiki.jactor2.core.facilities;

import org.agilewiki.jactor2.core.reactors.UnboundReactor;

/**
 * Interface for all threads in a facility thread pool.
 * The RequestBase.call method should not be invoked from a PoolThread.
 */
public interface PoolThread {

    /**
     * Returns the current reactor.
     *
     * @return The current reactor, or null.
     */
    UnboundReactor getCurrentReactor();

    /**
     * Assigns the current reactor.
     *
     * @param _reactor The current reactor.
     */
    void setCurrentReactor(final UnboundReactor _reactor);
}
