package org.agilewiki.jactor2.core.plant;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DefaultScheduler implements Scheduler {

    private static final long HEARTBEAT_MILLIS = 1000;

    private static final int SCHEDULER_POOL_SIZE = 2;

    private volatile long currentTimeMillis;

    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    public DefaultScheduler() {
        scheduledThreadPoolExecutor =
                new ScheduledThreadPoolExecutor(getSchedulerPoolSize());
        currentTimeMillis = System.currentTimeMillis();
        scheduledThreadPoolExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                currentTimeMillis = System.currentTimeMillis();
            }
        }, getHeartbeatMillis(), getHeartbeatMillis(), TimeUnit.MILLISECONDS);
    }

    protected long getHeartbeatMillis() { return HEARTBEAT_MILLIS; }

    protected int getSchedulerPoolSize() { return SCHEDULER_POOL_SIZE; }

    @Override
    public long currentTimeMillis() { return currentTimeMillis; }

    @Override
    public void schedule(Runnable runnable, long _millisecondDelay) {
        scheduledThreadPoolExecutor.schedule(runnable, _millisecondDelay, TimeUnit.MILLISECONDS);
    }

    @Override
    public void scheduleAtFixedRate(Runnable runnable, long _millisecondDelay) {
        scheduledThreadPoolExecutor.scheduleAtFixedRate(runnable, _millisecondDelay,
                _millisecondDelay, TimeUnit.MILLISECONDS);
    }

    @Override
    public void close() {
        scheduledThreadPoolExecutor.shutdown();
    }
}
