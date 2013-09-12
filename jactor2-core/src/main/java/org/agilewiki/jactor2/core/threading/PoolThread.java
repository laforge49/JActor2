package org.agilewiki.jactor2.core.threading;

/**
 * Base class for all threads in a module context thread pool.
 * The RequestBase.call method should not be invoked from a PoolThread.
 */
public class PoolThread extends Thread {
    public PoolThread(final Runnable _runnable) {
        super(_runnable);
    }
}
