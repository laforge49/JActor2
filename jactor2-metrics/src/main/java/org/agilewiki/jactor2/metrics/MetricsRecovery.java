package org.agilewiki.jactor2.metrics;

import org.agilewiki.jactor2.core.impl.mtPlant.Recovery;
import org.agilewiki.jactor2.core.util.MetricsTimer;

/**
 * Metrics class for managing failure detection and recovery.
 * The default Recovery is created by MPlantConfiguration.
 */
public class MetricsRecovery extends Recovery {
    /**
     * Returns the MetricsTimer used to track the performance of this Request instance.
     *
     * @return the DummyMetricsTimer.
     */
    @Override
    public MetricsTimer getMetricsTimer() {
        return MetricsTimerImpl.DEFAULT;
    }
}
