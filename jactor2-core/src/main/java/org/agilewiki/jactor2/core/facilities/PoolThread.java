package org.agilewiki.jactor2.core.facilities;

/**
 * Base class for all threads in a facility thread pool.
 * The RequestBase.call method should not be invoked from a PoolThread.
 */
public class PoolThread extends Thread {
    public PoolThread(final Runnable _runnable) {
        super(_runnable);
    }
}
