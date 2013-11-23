package org.agilewiki.jactor2.core.facilities;

import java.util.concurrent.Semaphore;

public class SchedulableSemaphore extends Semaphore implements Runnable {
    private boolean timeout;

    public SchedulableSemaphore() {
        super(0);
    }

    @Override
    public void run() {
        timeout = true;
        release();
    }

    public boolean isTimeout() {
        return timeout;
    }
}
