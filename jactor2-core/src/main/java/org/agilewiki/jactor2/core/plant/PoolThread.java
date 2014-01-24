package org.agilewiki.jactor2.core.plant;

import org.agilewiki.jactor2.core.impl.UnboundReactorImpl;

/**
 * Interface for all threads in a facility thread pool.
 * The RequestImplBase.call method should not be invoked from a PoolThread.
 */
public class PoolThread extends Thread {

    private volatile UnboundReactorImpl currentReactor;

    public PoolThread(final Runnable _runnable) {
        super(_runnable);
    }

    /**
     * Returns the current reactor.
     *
     * @return The current reactor, or null.
     */
    public UnboundReactorImpl getCurrentReactor() {
        return currentReactor;
    }

    /**
     * Assigns the current reactor.
     *
     * @param _reactor The current reactor.
     */
    public void setCurrentReactor(final UnboundReactorImpl _reactor) {
        currentReactor = _reactor;
    }
}
