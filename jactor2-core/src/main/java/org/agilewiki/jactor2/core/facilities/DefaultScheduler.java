package org.agilewiki.jactor2.core.facilities;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DefaultScheduler implements Scheduler {

    private volatile long currentTimeMillis;

    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    public DefaultScheduler(final PlantConfiguration _plantConfiguration) {
        scheduledThreadPoolExecutor =
                new ScheduledThreadPoolExecutor(_plantConfiguration.getSchedulerPoolSize());
        currentTimeMillis = System.currentTimeMillis();
        scheduledThreadPoolExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                currentTimeMillis = System.currentTimeMillis();
            }
        }, _plantConfiguration.getHeartbeatMillis(), _plantConfiguration.getHeartbeatMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public long currentTimeMillis() { return currentTimeMillis; }

    @Override
    public void close() {
        scheduledThreadPoolExecutor.shutdown();
    }

    @Override
    public void schedule(Runnable runnable, long _millisecondDelay) {
        scheduledThreadPoolExecutor.schedule(runnable, _millisecondDelay, TimeUnit.MILLISECONDS);
    }


}
