package org.agilewiki.jactor2.core.plant;

import org.agilewiki.jactor2.core.impl.reactorsImpl.PoolThreadReactorImpl;

/**
 * Base class for pool threads used by reactors.
 * Created by DefaultReactorPoolThreadFactory.
 */
public class ReactorPoolThread extends Thread {

    private volatile PoolThreadReactorImpl currentReactor;

    /**
     * Create a pool thread.
     *
     * @param _runnable The runnable to be executed by the thread.
     */
    public ReactorPoolThread(final Runnable _runnable) {
        super(_runnable);
    }

    /**
     * Returns the current reactor.
     *
     * @return The current reactor, or null.
     */
    public PoolThreadReactorImpl getCurrentReactorImpl() {
        return currentReactor;
    }

    /**
     * Assigns the current reactor.
     *
     * @param _reactor The current reactor.
     */
    public void setCurrentReactor(final PoolThreadReactorImpl _reactor) {
        currentReactor = _reactor;
    }
}
