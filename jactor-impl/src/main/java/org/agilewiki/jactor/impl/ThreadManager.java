package org.agilewiki.jactor.impl;

import java.util.concurrent.ThreadFactory;

/**
 * A ThreadManager is used to process a collection of Runnable tasks.
 * ThreadManager is a thread pool, but with a simplified API and
 * assumes that the thread pool has a fixed number of threads.
 */
public interface ThreadManager extends AutoCloseable {
    /**
     * Create and start the concurrent.
     *
     * @param threadCount   The number of concurrent to be used.
     * @param threadFactory Used to create the concurrent.
     */
    public void start(final int threadCount, final ThreadFactory threadFactory);

    /**
     * Begin running a mailbox.
     *
     * @param mailbox The run method is to be called by another thread.
     */
    public void execute(final JAMailbox mailbox);

    /**
     * Stop all the threads as they complete their tasks.
     */
    @Override
    public void close();
}
