package org.agilewiki.jactor2.metrics;

import org.agilewiki.jactor2.core.impl.mtPlant.*;
import org.agilewiki.jactor2.core.plant.PlantScheduler;

import java.util.concurrent.ThreadFactory;

/**
 * Metrics class for configuring the Plant.
 */
public class MPlantConfiguration extends PlantConfiguration {
    /**
     * Create a plant configuration with a reactor thread pool size of 20.
     */
    public MPlantConfiguration() {
        super();
    }

    /**
     * Create a plant configuration.
     *
     * @param _reactorThreadPoolSize    The size of the reactor thread pool.
     */
    public MPlantConfiguration(final int _reactorThreadPoolSize) {
        super(_reactorThreadPoolSize);
    }

    /**
     * Create the default Recovery instance.
     *
     * @return The default Recovery instance.
     */
    @Override
    protected MRecovery createRecovery() {
        return new MRecovery();
    }
}
