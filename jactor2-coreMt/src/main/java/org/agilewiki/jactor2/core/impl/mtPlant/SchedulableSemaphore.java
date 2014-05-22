package org.agilewiki.jactor2.core.impl.mtPlant;

import java.util.concurrent.Semaphore;

/**
 * A semaphore that can be marked as timed out.
 */
public class SchedulableSemaphore {
    private final Semaphore semaphore = new Semaphore(0);
    private boolean timeout;

    /**
     * On timeout, call runable.run().
     */
    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            timeout = true;
            semaphore.release();
        }
    };

    /**
     * Wait for the timer to complete.
     *
     * @return True when the timeout expired, false when release was called.
     */
    public boolean acquire() throws InterruptedException {
        semaphore.acquire();
        return timeout;
    }

    /**
     *  Force the timer to complete early.
     */
    public void release() {
        semaphore.release();
    }
}
