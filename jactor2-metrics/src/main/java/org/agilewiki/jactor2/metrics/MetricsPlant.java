package org.agilewiki.jactor2.metrics;

import org.agilewiki.jactor2.core.impl.mtPlant.PlantMtImpl;
import org.agilewiki.jactor2.core.plant.PlantBase;

/**
 * A plant with metrics.
 */
public class MetricsPlant extends PlantBase {

    public MetricsPlant() throws Exception {
        new PlantMtImpl(new MetricsPlantConfiguration());
    }

    public MetricsPlant(final int _threadCount) throws Exception {
        new PlantMtImpl(new MetricsPlantConfiguration(_threadCount));
    }

    public MetricsPlant(final MetricsPlantConfiguration _plantConfiguration) throws Exception {
        new PlantMtImpl(_plantConfiguration);
    }
}
