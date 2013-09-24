package org.agilewiki.jactor2.core.facilities;

import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.reactors.ReactorBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The ThreadManager is used to process a queue of Reactor's
 * that have non-empty inboxes.
 * ThreadManager is a thread pool, but it has a simplified API and
 * assumes that the thread pool has a fixed number of threads.
 * ThreadManager is also responsible for assigning the threadReference
 * when a Reactor is run.
 */
final public class ThreadManager {
    final Logger logger = LoggerFactory.getLogger(ThreadManager.class);

    /**
     * The taskRequest semaphore is used to wake up a thread
     * when there is a Reactor which hasWork.
     */
    final private Semaphore taskRequest = new Semaphore(0);

    /**
     * The messageProcessors queue holds the messageProcessors which have messages to be processed.
     */
    final private ConcurrentLinkedQueue<ReactorBase> messageProcessors =
            new ConcurrentLinkedQueue<ReactorBase>();

    /**
     * When closing is true, the threads exit as they finish their current activity.
     */
    private boolean closing = false;

    /**
     * The threadCount is the number of threads in the thread pool.
     */
    private int threadCount;

    /**
     * The threads in the thread pool.
     */
    private Thread threads[] = null;

    /**
     * Create a ThreadManager
     *
     * @param _threadCount   The number of threads to be created.
     * @param _threadFactory Used to create the threads.
     */
    public ThreadManager(final int _threadCount,
                         final ThreadFactory _threadFactory) {
        this.threadCount = _threadCount;
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Thread currentThread = Thread.currentThread();
                while (true) {
                    try {
                        taskRequest.acquire();
                        ReactorBase messageProcessor = messageProcessors.poll();
                        if (messageProcessor != null) {
                            AtomicReference<Thread> threadReference = messageProcessor.getThreadReference();
                            if (threadReference.get() == null &&
                                    threadReference.compareAndSet(null, currentThread)) {
                                while (true) {
                                    try {
                                        messageProcessor.run();
                                    } catch (final MigrationException me) {
                                        boolean hasWork = messageProcessor.hasWork();
                                        threadReference.set(null);
                                        if (messageProcessor.isIdler() || hasWork || messageProcessor.hasConcurrent()) {
                                            execute(messageProcessor);
                                        }
                                        messageProcessor = me.messageProcessor;
                                        threadReference = messageProcessor.getThreadReference();
                                        continue;
                                    } catch (final Throwable e) {
                                        logger.error(
                                                "Exception thrown by a targetReactor's run method",
                                                e);
                                    }
                                    boolean hasWork = messageProcessor.hasWork();
                                    threadReference.set(null);
                                    if (hasWork || messageProcessor.hasConcurrent())
                                        execute(messageProcessor);
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
     * Begin running a targetReactor.
     *
     * @param _reactor The run method is to be called by the selected thread.
     */
    final public void execute(final Reactor _reactor) {
        if (closing)
            return;
        messageProcessors.add((ReactorBase) _reactor);
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
