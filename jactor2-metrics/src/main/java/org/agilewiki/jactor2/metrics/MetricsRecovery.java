package org.agilewiki.jactor2.metrics;

import org.agilewiki.jactor2.core.impl.mtPlant.Recovery;
import org.agilewiki.jactor2.core.plant.impl.MetricsTimer;

/**
 * Metrics class for managing failure detection and recovery.
 * The default Recovery is created by MPlantConfiguration.
 */
public class MetricsRecovery extends Recovery {
    /**
     * Returns the MetricsTimer used to track the performance of this Request instance.
     *
     * @param _name The name of the timer.
     * @return the DummyMetricsTimer.
     */
    @Override
    public MetricsTimer getMetricsTimer(final String _name) {
        return MetricsTimerImpl.getMetricsTimer(_name);
    }
}
