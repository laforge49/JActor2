package org.agilewiki.jactor2.core.impl.mtPlant;

import org.agilewiki.jactor2.core.plant.impl.PlantScheduler;

import java.util.concurrent.ThreadFactory;

/**
 * Base class for configuring the Plant.
 * Used as the default configuration when none is specified.
 */
public class PlantConfiguration {
    /**
     * The size of the thread pool used by reactors.
     */
    public final int reactorThreadPoolSize;

    private Recovery recovery;

    private PlantScheduler plantScheduler;

    /**
     * Create a plant configuration with a reactor thread pool size of 20.
     */
    public PlantConfiguration() {
        reactorThreadPoolSize = 20;
    }

    /**
     * Create a plant configuration.
     *
     * @param _reactorThreadPoolSize    The size of the reactor thread pool.
     */
    public PlantConfiguration(final int _reactorThreadPoolSize) {
        reactorThreadPoolSize = _reactorThreadPoolSize;
    }

    /**
     * Create the default Recovery instance.
     *
     * @return The default Recovery instance.
     */
    protected Recovery createRecovery() {
        return new Recovery();
    }

    /**
     * Returns the default Recovery instance.
     *
     * @return The default Recovery instance.
     */
    public Recovery getRecovery() {
        if (recovery == null) {
            recovery = createRecovery();
        }
        return recovery;
    }

    /**
     * Create the plant scheduler.
     * @return The plant scheduler.
     */
    protected PlantScheduler createPlantScheduler() {
        return new DefaultPlantScheduler();
    }

    /**
     * Returns the plant scheduler.
     * @return The plant scheduler.
     */
    public PlantScheduler getPlantScheduler() {
        if (plantScheduler == null) {
            plantScheduler = createPlantScheduler();
        }
        return plantScheduler;
    }

    /**
     * Create the reactor pool thread factory.
     *
     * @return The thread factory for the reactor pool thread manager.
     */
    protected ThreadFactory createReactorPoolThreadFactory() {
        return new DefaultReactorPoolThreadFactory();
    }

    public int getMaxThreadMigrations() {
        return 1000;
    }

    /**
     * Create the reactor pool thread manager.
     *
     * @return The reactor pool thread manager.
     */
    public ReactorPoolThreadManager createReactorPoolThreadManager() {
        return new ReactorPoolThreadManager(reactorThreadPoolSize,
                getMaxThreadMigrations(),
                createReactorPoolThreadFactory());
    }

    /**
     * Returns 16.
     *
     * @return The reactor default initial local message queue size.
     */
    public int getInitialLocalMessageQueueSize() {
        return 16;
    }

    /**
     * Returns 16.
     *
     * @return The reactor default initial buffer size.
     */
    public int getInitialBufferSize() {
        return 16;
    }
}
