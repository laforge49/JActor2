package org.agilewiki.jactor2.core.impl;

import org.agilewiki.jactor2.core.impl.stPlant.PlantConfiguration;
import org.agilewiki.jactor2.core.plant.PlantScheduler;

public class JActorStTestPlantConfiguration extends PlantConfiguration {
    /**
     * Create the plant scheduler.
     *
     * @return The plant scheduler.
     */
    @Override
    protected PlantScheduler createPlantScheduler() {
        return new JActorStTestPlantScheduler();
    }
}
