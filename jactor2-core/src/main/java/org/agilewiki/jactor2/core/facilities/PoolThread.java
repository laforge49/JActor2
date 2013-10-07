package org.agilewiki.jactor2.core.facilities;

import org.agilewiki.jactor2.core.reactors.UnboundReactor;

/**
 * Base class for all threads in a facility thread pool.
 * The RequestBase.call method should not be invoked from a PoolThread.
 */
public class PoolThread extends Thread {

    private volatile UnboundReactor currentReactor;

    public PoolThread(final Runnable _runnable) {
        super(_runnable);
    }

    /**
     * Returns the current reactor.
     *
     * @return The current reactor, or null.
     */
    public UnboundReactor getCurrentReactor() {
        return currentReactor;
    }

    /**
     * Assigns the current reactor.
     *
     * @param _reactor The current reactor.
     */
    public void setCurrentReactor(final UnboundReactor _reactor) {
        currentReactor = _reactor;
    }
}
