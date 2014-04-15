package org.agilewiki.jactor2.core.impl;

import org.agilewiki.jactor2.core.impl.stPlant.PlantConfiguration;
import org.agilewiki.jactor2.core.impl.stPlant.PlantStImpl;
import org.agilewiki.jactor2.core.plant.PlantBase;

/**
 * Plant is a singleton and is the top-level object.
 * The plant has an internal reactor which is the root of a tree of all reactors,
 * though this tree is made using weak references.
 */
final public class Plant extends PlantBase {
    /**
     * Create a plant with the given configuration.
     *
     * @param _plantConfiguration The configuration to be used by the plant.
     */
    public Plant(final PlantConfiguration _plantConfiguration) {
        new PlantStImpl(_plantConfiguration);
    }
}
