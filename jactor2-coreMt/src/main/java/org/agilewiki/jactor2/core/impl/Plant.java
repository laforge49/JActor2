package org.agilewiki.jactor2.core.impl;

import org.agilewiki.jactor2.core.impl.mtPlant.PlantConfiguration;
import org.agilewiki.jactor2.core.impl.mtPlant.PlantMtImpl;
import org.agilewiki.jactor2.core.plant.impl.PlantBase;

/**
 * Plant is a singleton and is the top-level object.
 * The plant has an internal reactor which is the root of a tree of all reactors,
 * though this tree is made using weak references.
 */
final public class Plant extends PlantBase {

    /**
     * Create a plant with the default configuration.
     */
    public Plant() throws Exception {
        new PlantMtImpl();
    }

    /**
     * Create a plant with the default configuration,
     * but with the given reactor thread pool size.
     *
     * @param _reactorThreadPoolSize The number of threads to be created for the
     *                               reactor thread pool.
     */
    public Plant(final int _reactorThreadPoolSize) throws Exception {
        new PlantMtImpl(_reactorThreadPoolSize);
    }

    /**
     * Create a plant with the given configuration.
     *
     * @param _plantConfiguration The configuration to be used by the plant.
     */
    public Plant(final PlantConfiguration _plantConfiguration) throws Exception {
        new PlantMtImpl(_plantConfiguration);
    }
}
