package org.agilewiki.pactor.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

/**
 * Wraps an ExecutorService in a ThreadManager, so that an ExecutorService
 * can be used instead of a default ThreadManager if desired.
 *
 * @author monster
 */
public class ExecutorServiceWrapper implements ThreadManager {

    /**
     * The executor service.
     */
    private final ExecutorService executorService;

    /**
     * Creates a new ThreadManager out of an ExecutorService.
     */
    public ExecutorServiceWrapper(final ExecutorService theExecutorService) {
        executorService = theExecutorService;
    }

    /* (non-Javadoc)
     * @see org.agilewiki.pactor.impl.ThreadManager#start(int, java.util.concurrent.ThreadFactory)
     */
    @Override
    public void start(final int threadCount, final ThreadFactory threadFactory) {
        throw new IllegalStateException("Already started!");
    }

    /* (non-Javadoc)
     * @see org.agilewiki.pactor.impl.ThreadManager#process(java.lang.Runnable)
     */
    @Override
    public void execute(final Runnable runnable) {
        executorService.execute(runnable);
    }

    /* (non-Javadoc)
     * @see org.agilewiki.pactor.impl.ThreadManager#close()
     */
    @Override
    public void close() {
        executorService.shutdownNow();
    }
}
