package org.agilewiki.jactor.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicReference;

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
    final private ConcurrentLinkedQueue<JAMailbox> tasks =
            new ConcurrentLinkedQueue<JAMailbox>();

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
                final Thread currentThread = Thread.currentThread();
                while (true) {
                    try {
                        taskRequest.acquire();
                        JAMailbox mailbox = tasks.poll();
                        if (mailbox != null) {
                            AtomicReference<Thread> threadReference = mailbox.getThreadReference();
                            while (true) {
                                if (threadReference.get() == null &&
                                        threadReference.compareAndSet(null, currentThread)) {
                                    try {
                                        mailbox.run();
                                    } catch (final MigrateException me) {
                                        threadReference.set(null);
                                        if (mailbox.isIdler())
                                            execute(mailbox);
                                        mailbox = me.mailbox;
                                        threadReference = mailbox.getThreadReference();
                                        continue;
                                    } catch (final Throwable e) {
                                        logger.error(
                                                "Exception thrown by a mailbox's run method",
                                                e);
                                    } finally {
                                        threadReference.set(null);
                                    }
                                }
                                break;
                            }
                        }
                    } catch (final InterruptedException e) {
                    }
                    if (closing)
                        return;
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
     * @param mailbox A task to be processed on another thread.
     */
    @Override
    final public void execute(final JAMailbox mailbox) {
        tasks.add(mailbox);
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
}
