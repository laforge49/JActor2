package org.agilewiki.jactor2.core.impl.mtPlant;

import org.agilewiki.jactor2.core.plant.impl.MetricsTimer;

public class DummyMetricsTimer implements MetricsTimer {
    private final static DummyMetricsTimer DEFAULT = new DummyMetricsTimer();

    public static final DummyMetricsTimer getMetricsTimer(String name) {
        return DEFAULT;
    }

    private DummyMetricsTimer() {}

    @Override
    public long nanos() {
        return 0;
    }

    @Override
    public void updateNanos(long nanos, boolean success) {

    }
}
