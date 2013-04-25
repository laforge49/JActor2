package org.agilewiki.pamailbox;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wraps an ExecutorService in a ThreadManager, so that an ExecutorService
 * can be used instead of a default ThreadManager if desired.
 *
 * @author monster
 */
public class ExecutorServiceWrapper implements ThreadManager {
    private static final Logger logger = LoggerFactory
            .getLogger(ThreadManagerImpl.class);

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
     * @see org.agilewiki.pamailbox.ThreadManager#start(int, java.util.concurrent.ThreadFactory)
     */
    @Override
    public void start(final int threadCount, final ThreadFactory threadFactory) {
        throw new IllegalStateException("Already started!");
    }

    /* (non-Javadoc)
     * @see org.agilewiki.pamailbox.ThreadManager#process(java.lang.Runnable)
     */
    @Override
    public void process(final Runnable runnable) {
        executorService.execute(runnable);
    }

    /* (non-Javadoc)
     * @see org.agilewiki.pamailbox.ThreadManager#close()
     */
    @Override
    public void close() {
        executorService.shutdownNow();
    }

    /* (non-Javadoc)
     * @see org.agilewiki.pamailbox.ThreadManager#logException(boolean, java.lang.String, java.lang.Throwable)
     */
    @Override
    public void logException(final boolean fatal, final String msg,
            final Throwable exception) {
        if (fatal)
            logger.error(msg, exception);
        else
            logger.warn(msg, exception);
    }
}
