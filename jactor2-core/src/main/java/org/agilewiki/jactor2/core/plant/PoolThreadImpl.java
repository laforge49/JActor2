package org.agilewiki.jactor2.core.plant;

import org.agilewiki.jactor2.core.reactors.UnboundReactor;

/**
 * Base class for all threads in a facility thread pool.
 * The RequestBase.call method should not be invoked from a PoolThreadImpl.
 */
public class PoolThreadImpl extends Thread implements PoolThread {

    private volatile UnboundReactor currentReactor;

    public PoolThreadImpl(final Runnable _runnable) {
        super(_runnable);
    }

    /**
     * Returns the current reactor.
     *
     * @return The current reactor, or null.
     */
    @Override
    public UnboundReactor getCurrentReactor() {
        return currentReactor;
    }

    /**
     * Assigns the current reactor.
     *
     * @param _reactor The current reactor.
     */
    @Override
    public void setCurrentReactor(final UnboundReactor _reactor) {
        currentReactor = _reactor;
    }
}
