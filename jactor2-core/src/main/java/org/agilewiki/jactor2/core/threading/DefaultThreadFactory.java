package org.agilewiki.jactor2.core.threading;

import java.util.concurrent.ThreadFactory;

/**
 * DefaultThreadFactory is used to create pool threads.
 */
final public class DefaultThreadFactory implements ThreadFactory {
    /**
     * The newThread method returns a newly created PoolThread.
     *
     * @param _runnable The run method is called when the thread is started.
     */
    @Override
    public Thread newThread(final Runnable _runnable) {
        return new PoolThread(_runnable);
    }
}
