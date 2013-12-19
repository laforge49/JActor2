package org.agilewiki.jactor2.core.plant;

import org.agilewiki.jactor2.core.reactors.UnboundReactorImpl;

/**
 * Interface for all threads in a facility thread pool.
 * The RequestImplBase.call method should not be invoked from a PoolThread.
 */
public interface PoolThread {

    /**
     * Returns the current reactor.
     *
     * @return The current reactor, or null.
     */
    UnboundReactorImpl getCurrentReactor();

    /**
     * Assigns the current reactor.
     *
     * @param _reactor The current reactor.
     */
    void setCurrentReactor(final UnboundReactorImpl _reactor);
}
