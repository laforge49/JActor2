package org.agilewiki.jactor2.core.facilities;

import java.util.concurrent.Semaphore;

/**
 * A timeout timer built on a semaphore, created by calling Plant.schedulableSemaphore(_millisecondDelay).
 */
public class SchedulableSemaphore {
    private Semaphore semaphore = new Semaphore(0);
    private boolean timeout;

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            timeout = true;
            semaphore.release();
        }
    };

    SchedulableSemaphore() {
    }

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
