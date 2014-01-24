package org.agilewiki.jactor2.core.plant;

import java.util.concurrent.ThreadFactory;

/**
 * Base class for configuring the Plant.
 * Used as the default configuration when none is specified.
 */
public class PlantConfiguration {
    /**
     * The size of the thread pool used by reactors.
     */
    public final int threadPoolSize;

    private Recovery recovery;

    private PlantScheduler plantScheduler;

    public PlantConfiguration() {
        threadPoolSize = 20;
    }

    public PlantConfiguration(final int _threadPoolSize) {
        threadPoolSize = _threadPoolSize;
    }

    public void initialize() {
        recovery = createRecovery();
        plantScheduler = createScheduler();
    }

    protected Recovery createRecovery() {
        return new Recovery();
    }

    public Recovery getRecovery() {
        return recovery;
    }

    protected PlantScheduler createScheduler() {
        return new DefaultPlantScheduler();
    }

    public PlantScheduler getPlantScheduler() { return plantScheduler; }

    public void close() {
        plantScheduler.close();
    }

    protected ThreadFactory createThreadFactory() {
        return new DefaultThreadFactory();
    }

    public ReactorThreadManager createThreadManager() {
        return new ReactorThreadManager(threadPoolSize, createThreadFactory());
    }

    public int getInitialLocalMessageQueueSize() {
        return 16;
    }

    public int getInitialBufferSize() {
        return 16;
    }
}
