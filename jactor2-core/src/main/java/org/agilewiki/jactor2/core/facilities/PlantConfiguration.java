package org.agilewiki.jactor2.core.facilities;

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

    public final int threadPoolSize;

    private Recovery recovery;

    private Scheduler scheduler;

    public PlantConfiguration() {
        threadPoolSize = DEFAULT_THREAD_COUNT;
    }

    public PlantConfiguration(final int _threadPoolSize) {
        threadPoolSize = _threadPoolSize;
    }

    public void initialize() {
        recovery = createRecovery();
        scheduler = createScheduler();
        scheduler.initialize();
    }

    protected Recovery createRecovery() {
        return new Recovery();
    }

    public Recovery getRecovery() {
        return recovery;
    }

    protected Scheduler createScheduler() {
        return new DefaultScheduler();
    }

    public Scheduler getScheduler() { return scheduler; }

    public void close() {
        scheduler.close();
    }

    public ThreadFactory getThreadFactory() {
        return new DefaultThreadFactory();
    }

    public ThreadManager getThreadManager() {
        return new ThreadManager(threadPoolSize, getThreadFactory());
    }

    public int getInitialLocalMessageQueueSize() {
        return DEFAULT_INITIAL_LOCAL_QUEUE_SIZE;
    }

    public int getInitialBufferSize() {
        return DEFAULT_INITIAL_BUFFER_SIZE;
    }
}
