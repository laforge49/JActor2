package org.agilewiki.jactor2.core.plant.impl;

import org.agilewiki.jactor2.core.reactors.facilities.Facility;

/**
 * Plant is a singleton and is the top-level object.
 * The plant has an internal reactor which is the root of a tree of all reactors,
 * though this tree is made using weak references.
 */
abstract public class PlantBase {

    /**
     * Closes all reactors and associated closeables, the plant scheduler and the
     * reactor thread pool.
     */
    public static void close() throws Exception {
        final PlantImpl plantImpl = PlantImpl.getSingleton();
        if (plantImpl != null) {
            plantImpl.close();
        }
    }

    /**
     * Returns the internal reactor.
     *
     * @return The internal reactor.
     */
    public static Facility getInternalFacility() {
        return PlantImpl.getSingleton().getInternalFacility();
    }

    /**
     * Returns the plant scheduler.
     *
     * @return The plant scheduler.
     */
    public static PlantScheduler getPlantScheduler() {
        return PlantImpl.getSingleton().getPlantScheduler();
    }
}
