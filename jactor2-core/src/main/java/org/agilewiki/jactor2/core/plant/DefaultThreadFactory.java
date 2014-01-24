package org.agilewiki.jactor2.core.plant;

import java.util.concurrent.ThreadFactory;

/**
 * DefaultThreadFactory is used to create pool threads.
 */
public final class DefaultThreadFactory implements ThreadFactory {
    /**
     * The newThread method returns a newly created PoolThreadx.
     *
     * @param _runnable The run method is called when the thread is started.
     */
    @Override
    public PoolThreadx newThread(final Runnable _runnable) {
        return new PoolThread(_runnable);
    }
}
