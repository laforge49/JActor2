package org.agilewiki.jactor2.core.facilities;

import org.agilewiki.jactor2.core.util.DefaultRecovery;
import org.agilewiki.jactor2.core.util.Recovery;

import java.util.concurrent.ThreadFactory;

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

    public PlantConfiguration() {
        threadPoolSize = DEFAULT_THREAD_COUNT;
    }

    public PlantConfiguration(final int _threadPoolSize) {
        threadPoolSize = _threadPoolSize;
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

    public long getHeartbeatMillis() { return HEARTBEAT_MILLIS; }

    public int getSchedulerPoolSize() { return SCHEDULER_POOL_SIZE; }
}
