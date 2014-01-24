package org.agilewiki.jactor2.core.plant;

/**
 * Base class for all threads in a facility thread pool.
 * The RequestImplBase.call method should not be invoked from a PoolThread.
 */
public class PoolThread extends PoolThreadx {

    public PoolThread(final Runnable _runnable) {
        super(_runnable);
    }
}
