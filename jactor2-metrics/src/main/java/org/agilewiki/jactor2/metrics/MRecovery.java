package org.agilewiki.jactor2.metrics;

import org.agilewiki.jactor2.core.impl.mtPlant.DummyMetricsTimer;
import org.agilewiki.jactor2.core.impl.mtPlant.Recovery;
import org.agilewiki.jactor2.core.impl.mtReactors.ReactorMtImpl;
import org.agilewiki.jactor2.core.impl.mtRequests.RequestMtImpl;
import org.agilewiki.jactor2.core.plant.PlantBase;
import org.agilewiki.jactor2.core.reactors.impl.ReactorImpl;
import org.agilewiki.jactor2.core.util.MetricsTimer;

/**
 * Metrics class for managing failure detection and recovery.
 * The default Recovery is created by MPlantConfiguration.
 */
public class MRecovery extends Recovery {
    /**
     * Returns the MetricsTimer used to track the performance of this Request instance.
     *
     * @return the DummyMetricsTimer.
     */
    @Override
    public MetricsTimer getMetricsTimer() {
        return MTimer.DEFAULT;
    }
}
