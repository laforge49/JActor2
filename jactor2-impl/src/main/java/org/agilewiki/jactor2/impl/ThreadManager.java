package org.agilewiki.jactor2.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A ThreadManager is used to process a collection of Runnable tasks.
 * ThreadManager is a thread pool, but with a simplified API and
 * assumes that the thread pool has a fixed number of threads.
 * ThreadManager is also responsible for setting the threadReference
 * in the mailbox.
 */
final public class ThreadManager {
    final Logger logger = LoggerFactory.getLogger(ThreadManager.class);

    /**
     * The taskRequest semaphore is used to wake up a thread
     * when there is a task to process.
     */
    final private Semaphore taskRequest = new Semaphore(0);

    /**
     * The tasks queue holds the tasks waiting to be processed.
     */
    final private ConcurrentLinkedQueue<UnboundMailbox> tasks =
            new ConcurrentLinkedQueue<UnboundMailbox>();

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
     * @param threadFactory Used to create the threads.
     * @return A new JAThreadManager.
     */
    public static ThreadManager newThreadManager(final int threadCount,
                                                 final ThreadFactory threadFactory) {
        final ThreadManager threadManager = new ThreadManager();
        threadManager.start(threadCount, threadFactory);
        return threadManager;
    }

    /**
     * Create and start the threads.
     *
     * @param _threadCount   The number of threads to be created.
     * @param _threadFactory Used to create the threads.
     */
    final public void start(final int _threadCount,
                            final ThreadFactory _threadFactory) {
        this.threadCount = _threadCount;
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Thread currentThread = Thread.currentThread();
                while (true) {
                    try {
                        taskRequest.acquire();
                        UnboundMailbox mailbox = tasks.poll();
                        if (mailbox != null) {
                            AtomicReference<Thread> threadReference = mailbox.getThreadReference();
                            if (threadReference.get() == null &&
                                    threadReference.compareAndSet(null, currentThread)) {
                                while (true) {
                                    try {
                                        mailbox.run();
                                    } catch (final MigrationException me) {
                                        threadReference.set(null);
                                        if (mailbox.isIdler() || !mailbox.isEmpty())
                                            execute(mailbox);
                                        mailbox = me.mailbox;
                                        threadReference = mailbox.getThreadReference();
                                        continue;
                                    } catch (final Throwable e) {
                                        logger.error(
                                                "Exception thrown by a mailbox's run method",
                                                e);
                                    }
                                    threadReference.set(null);
                                    if (!mailbox.isEmpty())
                                        execute(mailbox);
                                    break;
                                }
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
        for (int c = 0; c < _threadCount; c++) {
            final Thread t = _threadFactory.newThread(runnable);
            threads[c] = t;
            t.start();
        }
    }

    /**
     * Begin running a mailbox.
     *
     * @param _mailbox The run method is to be called by the selected thread.
     */
    final public void execute(final UnboundMailbox _mailbox) {
        tasks.add(_mailbox);
        taskRequest.release();
    }

    /**
     * The close method is used to stop all the threads as they become idle.
     * This method sets a flag to indicate that the thread should stop
     * and then wakes up all the threads.
     * This method only returns after all the threads have died.
     */
    final public void close() {
        if (closing)
            return;
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
