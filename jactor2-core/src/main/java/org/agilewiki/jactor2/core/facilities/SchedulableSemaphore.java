package org.agilewiki.jactor2.core.facilities;

import java.util.concurrent.Semaphore;

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

    public boolean acquire() throws InterruptedException {
        semaphore.acquire();
        return timeout;
    }

    public void release() {
        semaphore.release();
    }
}
