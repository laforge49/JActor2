package org.agilewiki.jactor2.core.impl.mtPlant;

import org.agilewiki.jactor2.core.util.MetricsTimer;

public class DummyMetricsTimer implements MetricsTimer {
    public final static MetricsTimer DEFAULT = new DummyMetricsTimer();

    private DummyMetricsTimer() {}

    @Override
    public long nanos() {
        return 0;
    }

    @Override
    public void updateNanos(long nanos, boolean success) {

    }
}
