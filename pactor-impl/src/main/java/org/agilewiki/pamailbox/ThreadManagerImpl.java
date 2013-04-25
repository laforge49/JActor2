package org.agilewiki.pamailbox;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A high performance implementation of ThreadManager.
 */
final public class ThreadManagerImpl implements ThreadManager {
    final Logger logger = LoggerFactory.getLogger(ThreadManagerImpl.class);

    /**
     * The taskRequest semaphore is used to wake up a thread
     * when there is a task to process.
     */
    final private Semaphore taskRequest = new Semaphore(0);

    /**
     * The tasks queue holds the tasks waiting to be processed.
     */
    final private ConcurrentLinkedQueue<Runnable> tasks = new ConcurrentLinkedQueue<Runnable>();

    /**
     * When closing is true, concurrent exit as they finish their assigned tasks.
     */
    private boolean closing = false;

    /**
     * The threadCount is the number of threads used.
     */
    private int threadCount;

    /**
     * The worker threads.
     */
    private Thread threads[] = null;

    /**
     * Create a JAThreadManager
     *
     * @param threadCount The number of concurrent to be used.
     * @return A new JAThreadManager.
     */
    public static ThreadManager newThreadManager(final int threadCount) {
        final ThreadFactory threadFactory = new DefaultThreadFactory();
        return newThreadManager(threadCount, threadFactory);
    }

    /**
     * Create a JAThreadManager
     *
     * @param threadCount   The number of concurrent to be used.
     * @param threadFactory Used to create the concurrent.
     * @return A new JAThreadManager.
     */
    public static ThreadManager newThreadManager(final int threadCount,
            final ThreadFactory threadFactory) {
        final ThreadManager threadManager = new ThreadManagerImpl();
        threadManager.start(threadCount, threadFactory);
        return threadManager;
    }

    /**
     * Create and start the concurrent.
     *
     * @param threadCount   The number of concurrent to be used.
     * @param threadFactory Used to create the concurrent.
     */
    @Override
    final public void start(final int threadCount,
            final ThreadFactory threadFactory) {
        this.threadCount = threadCount;
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        taskRequest.acquire();
                        if (closing)
                            return;
                        final Runnable task = tasks.poll();
                        if (task != null)
                            try {
                                task.run();
                            } catch (final Throwable e) {
                                logException(
                                        false,
                                        "Exception thrown by a task's run method",
                                        e);
                            }
                    } catch (final InterruptedException e) {
                    }
                }
            }
        };
        threads = new Thread[this.threadCount];
        for (int c = 0; c < threadCount; c++) {
            final Thread t = threadFactory.newThread(runnable);
            threads[c] = t;
            t.start();
        }
    }

    /**
     * Begin running a task.
     *
     * @param task A task to be processed on another thread.
     */
    @Override
    final public void process(final Runnable task) {
        tasks.add(task);
        taskRequest.release();
    }

    /**
     * The close method is used to stop all the threads as they become idle.
     * This method sets a flag to indicate that the concurrent should stop
     * and then wakes up all the concurrent.
     * This method only returns after all the threads have died.
     */
    @Override
    final public void close() {
        closing = true;
        taskRequest.release(threadCount);
        final Thread ct = Thread.currentThread();
        for (final Thread t : threads) {
            if (ct != t) {
                t.interrupt();
            }
        }
        for (final Thread t : threads) {
            if (ct != t) {
                try {
                    t.join();
                } catch (final InterruptedException e) {
                }
            }
        }
        // Release the references to the thread array...
        threads = null;
    }

    @Override
    public void logException(final boolean fatal, final String msg,
            final Throwable exception) {
        if (fatal)
            logger.error(msg, exception);
        else
            logger.warn(msg, exception);
    }
}
