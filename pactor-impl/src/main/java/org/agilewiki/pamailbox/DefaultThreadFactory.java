package org.agilewiki.pamailbox;

import java.util.concurrent.ThreadFactory;

/**
 * JAThreadFactory is used to create threads.
 */
final public class DefaultThreadFactory implements ThreadFactory {
    /**
     * The newThread method returns a newly created Thread.
     *
     * @param runnable The run method is called when the thread is started.
     */
    @Override
    public Thread newThread(Runnable runnable) {
        return new Thread(runnable);
    }
}
