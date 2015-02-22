package org.agilewiki.jactor2.core.impl.mtPlant;

import org.agilewiki.jactor2.core.impl.mtReactors.MigrationException;
import org.agilewiki.jactor2.core.impl.mtReactors.PoolThreadReactorMtImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The ReactorPoolThreadManager is used to process a queue of Reactor's
 * that have non-empty inboxes.
 * ReactorPoolThreadManager is a thread pool, but it has a simplified API and
 * assumes that the thread pool has a fixed number of threads.
 * ReactorPoolThreadManager is also responsible for assigning the threadReference
 * when a Reactor is run.
 */
public final class ReactorPoolThreadManager {
    final Logger logger = LoggerFactory
            .getLogger(ReactorPoolThreadManager.class);

    /**
     * The taskRequest semaphore is used to wake up a thread
     * when there is a Reactor which hasWork.
     */
    final private Semaphore taskRequest = new Semaphore(0);

    /**
     * The reactors queue holds the reactors which have messages to be processed.
     */
    final private ConcurrentLinkedQueue<PoolThreadReactorMtImpl> reactors = new ConcurrentLinkedQueue<PoolThreadReactorMtImpl>();

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
    private ReactorPoolThread threads[] = null;

    /**
     * Create a ReactorPoolThreadManager
     *
     * @param _threadCount         The number of threads to be created.
     * @param _threadFactory       Used to create the threads.
     * @param _maxThreadMigrations Limits the number of times a thread will follow a message in succession.
     */
    public ReactorPoolThreadManager(final int _threadCount,
                                    final int _maxThreadMigrations,
                                    final ThreadFactory _threadFactory) {
        this.threadCount = _threadCount;
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final ReactorPoolThread currentThread = (ReactorPoolThread) Thread
                        .currentThread();
                while (true) {
                    try {
                        taskRequest.acquire();
                        PoolThreadReactorMtImpl reactor = reactors.poll();
                        if (reactor != null) {
                            AtomicReference<Thread> threadReference = reactor
                                    .getThreadReference();
                            if ((threadReference.get() == null)
                                    && threadReference.compareAndSet(null,
                                    currentThread)) {
                                currentThread.setCurrentReactor(reactor);
                                currentThread.clearMigrationCount();
                                while (true) {
                                    try {
                                        reactor.run();
                                    } catch (final MigrationException me) {
                                        final boolean hasWork = reactor
                                                .hasWork();
                                        threadReference.set(null);
                                        if (reactor.isIdler() || hasWork
                                                || reactor.hasConcurrent()) {
                                            execute(reactor);
                                        }
                                        reactor = me.reactor;
                                        threadReference = reactor
                                                .getThreadReference();
                                        currentThread
                                                .setCurrentReactor(reactor);
                                        continue;
                                    } catch (final Throwable e) {
                                        logger.error(
                                                "Exception thrown by a targetReactor's run method",
                                                e);
                                    }
                                    final boolean hasWork = reactor.hasWork();
                                    threadReference.set(null);
                                    if (hasWork || reactor.hasConcurrent()) {
                                        execute(reactor);
                                    }
                                    break;
                                }
                                currentThread.setCurrentReactor(null);
                            }
                        }
                    } catch (final InterruptedException e) {
                    }
                    currentThread.setCurrentReactor(null);
                    if (closing) {
                        return;
                    }
                }
            }
        };
        threads = new ReactorPoolThread[this.threadCount];
        for (int c = 0; c < _threadCount; c++) {
            final ReactorPoolThread t = (ReactorPoolThread) _threadFactory.newThread(runnable);
            threads[c] = t;
            t.setMaxThreadMigrations(_maxThreadMigrations);
            t.start();
        }
    }

    /**
     * Begin running a targetReactor.
     *
     * @param _reactor The run method is to be called by the selected thread.
     */
    public final void execute(final PoolThreadReactorMtImpl _reactor) {
        if (closing) {
            return;
        }
        reactors.add(_reactor);
        taskRequest.release();
    }

    /**
     * The close method is used to stop all the threads as they become idle.
     * This method sets a flag to indicate that the thread should stop
     * and then wakes up all the threads.
     */
//    * This method only returns after all the threads have died.
    public final void close() {
        if (closing) {
            return;
        }
        closing = true;
        taskRequest.release(threadCount);
        final Thread ct = Thread.currentThread();
        for (final Thread t : threads) {
            if (ct != t) {
                t.interrupt();
            }
        }
        /*
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
        */
    }
}
