package org.agilewiki.jactor2.core.plant;

import org.agilewiki.jactor2.core.impl.UnboundReactorImpl;

/**
 * Base class for pool threads used by reactors.
 * Created by DefaultThreadFactory.
 */
public class PoolThread extends Thread {

    private volatile UnboundReactorImpl currentReactor;

    /**
     * Create a pool thread.
     *
     * @param _runnable The runnable to be executed by the thread.
     */
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
