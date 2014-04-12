package org.agilewiki.jactor2.core.impl;

import org.agilewiki.jactor2.core.impl.mtPlant.PlantConfiguration;
import org.agilewiki.jactor2.core.impl.mtPlant.PlantMtImpl;
import org.agilewiki.jactor2.core.impl.mtPlant.PlantScheduler;
import org.agilewiki.jactor2.core.plant.PlantBase;

/**
 * Plant is a singleton and is the top-level object.
 * The plant has an internal reactor which is the root of a tree of all reactors,
 * though this tree is made using weak references.
 */
final public class Plant extends PlantBase {

    /**
     * Returns the plant scheduler.
     *
     * @return The plant scheduler.
     */
    public static PlantScheduler getPlantScheduler() {
        return PlantMtImpl.getSingleton().getPlantScheduler();
    }

    /**
     * Create a plant with the default configuration.
     */
    public Plant() {
        new PlantMtImpl();
    }

    /**
     * Create a plant with the default configuration,
     * but with the given reactor thread pool size.
     *
     * @param _reactorThreadPoolSize The number of threads to be created for the
     *                               reactor thread pool.
     */
    public Plant(final int _reactorThreadPoolSize) {
        new PlantMtImpl(_reactorThreadPoolSize);
    }

    /**
     * Create a plant with the given configuration.
     *
     * @param _plantConfiguration The configuration to be used by the plant.
     */
    public Plant(final PlantConfiguration _plantConfiguration) {
        new PlantMtImpl(_plantConfiguration);
    }
}
