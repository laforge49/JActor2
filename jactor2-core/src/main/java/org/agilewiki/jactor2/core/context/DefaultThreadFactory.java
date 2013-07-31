package org.agilewiki.jactor2.core.context;

import java.util.concurrent.ThreadFactory;

/**
 * DefaultThreadFactory is used to create threads.
 */
final public class DefaultThreadFactory implements ThreadFactory {
    /**
     * The newThread method returns a newly created Thread.
     *
     * @param _runnable The run method is called when the thread is started.
     */
    @Override
    public Thread newThread(Runnable _runnable) {
        return new Thread(_runnable);
    }
}
