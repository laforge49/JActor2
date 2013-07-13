package org.agilewiki.jactor2.impl;

import java.util.concurrent.ThreadFactory;

/**
 * A ThreadManager is used to process a collection of Runnable tasks.
 * ThreadManager is a thread pool, but with a simplified API and
 * assumes that the thread pool has a fixed number of threads.
 * ThreadManager is also responsible for setting the threadReference
 * in the mailbox.
 */
public interface ThreadManager extends AutoCloseable {
    /**
     * Create and start the threads.
     *
     * @param _threadCount   The number of threads to be created.
     * @param _threadFactory Used to create the threads.
     */
    public void start(final int _threadCount, final ThreadFactory _threadFactory);

    /**
     * Begin running a mailbox.
     *
     * @param _mailbox The run method is to be called by the selected thread.
     */
    public void execute(final UnboundMailbox _mailbox);

    /**
     * Stop all the threads as they complete their tasks.
     */
    @Override
    public void close();
}
