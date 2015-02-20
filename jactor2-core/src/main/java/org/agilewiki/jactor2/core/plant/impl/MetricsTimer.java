package org.agilewiki.jactor2.core.plant.impl;

/**
 * A metrics Timer, that also tracks failures.
 */
public interface MetricsTimer {
    /**
     * Returns the current time tick.
     *
     * @return time tick in nanoseconds
     */
    long nanos();

    /**
     * Adds a recorded duration in nanoseconds.
     *
     * @param nanos the length of the duration in nanoseconds
     * @param success True, if the execution succeeded.
     */
    void updateNanos(final long nanos, final boolean success);
}
