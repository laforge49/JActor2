package org.agilewiki.jactor2.metrics;

import org.agilewiki.jactor2.core.impl.mtPlant.PlantConfiguration;

/**
 * Metrics class for configuring the Plant.
 */
public class MetricsPlantConfiguration extends PlantConfiguration {
    /**
     * Create a plant configuration with a reactor thread pool size of 20.
     */
    public MetricsPlantConfiguration() {
        super();
    }

    /**
     * Create a plant configuration.
     *
     * @param _reactorThreadPoolSize The size of the reactor thread pool.
     */
    public MetricsPlantConfiguration(final int _reactorThreadPoolSize) {
        super(_reactorThreadPoolSize);
    }

    /**
     * Create the default Recovery instance.
     *
     * @return The default Recovery instance.
     */
    @Override
    protected MetricsRecovery createRecovery() {
        return new MetricsRecovery();
    }
}
