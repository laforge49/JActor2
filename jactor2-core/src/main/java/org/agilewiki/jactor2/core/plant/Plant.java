package org.agilewiki.jactor2.core.plant;

import org.agilewiki.jactor2.core.impl.plantImpl.PlantImpl;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

/**
 * Plant is a singleton and is the top-level object.
 * The plant has an internal reactor which is the root of a tree of all reactors,
 * though this tree is made using weak references.
 */
final public class Plant extends PlantBase {

    /**
     * Create a plant with the default configuration.
     */
    public Plant() {
        new PlantImpl();
    }

    /**
     * Create a plant with the default configuration,
     * but with the given reactor thread pool size.
     *
     * @param _reactorThreadPoolSize    The number of threads to be created for the
     *                                  reactor thread pool.
     */
    public Plant(final int _reactorThreadPoolSize) {
        new PlantImpl(_reactorThreadPoolSize);
    }

    /**
     * Create a plant with the given configuration.
     *
     * @param _plantConfiguration    The configuration to be used by the plant.
     */
    public Plant(final PlantConfiguration _plantConfiguration) {
        new PlantImpl(_plantConfiguration);
    }
}
