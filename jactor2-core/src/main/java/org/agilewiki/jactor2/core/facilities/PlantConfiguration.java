package org.agilewiki.jactor2.core.facilities;

import org.agilewiki.jactor2.core.util.DefaultRecovery;
import org.agilewiki.jactor2.core.util.Recovery;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class PlantConfiguration {
    /**
     * Default initial local queue size.
     */
    private static final int DEFAULT_INITIAL_LOCAL_QUEUE_SIZE = 16;

    /**
     * Default initial (per target Reactor) buffer.
     */
    private static final int DEFAULT_INITIAL_BUFFER_SIZE = 16;

    private static final int DEFAULT_THREAD_COUNT = 20;

    private static final long HEARTBEAT_MILLIS = 1000;

    private static final int SCHEDULER_POOL_SIZE = 1;

    public final int threadPoolSize;

    private ScheduledThreadPoolExecutor scheduler;

    private volatile long currentTimeMillis;

    public PlantConfiguration() {
        threadPoolSize = DEFAULT_THREAD_COUNT;
    }

    public PlantConfiguration(final int _threadPoolSize) {
        threadPoolSize = _threadPoolSize;
    }

    public void initializeScheduler() {
        if (scheduler == null) {
            scheduler = new ScheduledThreadPoolExecutor(getSchedulerPoolSize());
            currentTimeMillis = System.currentTimeMillis();
            scheduler.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    currentTimeMillis = System.currentTimeMillis();
                }
            }, getHeartbeatMillis(), getHeartbeatMillis(), TimeUnit.MILLISECONDS);
        }
    }

    public void schedule(Runnable runnable, long _millisecondDelay) {
        scheduler.schedule(runnable, _millisecondDelay, TimeUnit.MILLISECONDS);
    }

    public long currentTimeMillis() { return currentTimeMillis; }

    public void close() {
        if (scheduler != null)
            scheduler.shutdown();
    }

    public ThreadFactory getThreadFactory() {
        return new DefaultThreadFactory();
    }

    public ThreadManager getThreadManager() {
        return new ThreadManager(threadPoolSize, getThreadFactory());
    }

    public Recovery getRecovery() {
        return new DefaultRecovery();
    }

    public int getInitialLocalMessageQueueSize() {
        return DEFAULT_INITIAL_LOCAL_QUEUE_SIZE;
    }

    public int getInitialBufferSize() {
        return DEFAULT_INITIAL_BUFFER_SIZE;
    }

    protected long getHeartbeatMillis() { return HEARTBEAT_MILLIS; }

    protected int getSchedulerPoolSize() { return SCHEDULER_POOL_SIZE; }

    public long getSystemTimeMillis() { return System.currentTimeMillis(); }
}
