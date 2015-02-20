package org.agilewiki.jactor2.core.plant.impl;

/**
 * A scheduler for Plant, created by PlantConfiguration.
 */
public interface PlantScheduler {
    /**
     * Schedule a Runnable to be run at a later time.
     * The return value is an opaque task object, that can be cancelled.
     *
     * @param _runnable          The Runnable to be scheduled.
     * @param _millisecondDelay How long to wait before the Runnable is to be run.
     */
    Object schedule(Runnable _runnable, int _millisecondDelay);

    /**
     * Schedule a Runnable to be run repeatedly.
     * The return value is an opaque task object, that can be cancelled.
     *
     * @param _runnable          The Runnable to be run.
     * @param _millisecondDelay The delay between each run.
     *
     * @throws java.lang.NullPointerException If the _runnable is null.
     * @throws java.lang.IllegalArgumentException If the value of _millisecondDelay is invalid.
     */
    Object scheduleAtFixedRate(Runnable _runnable, int _millisecondDelay);

    /**
     * Cancels a task, that was scheduled with either schedule(java.lang.Runnable, long)
     * or scheduleAtFixedRate(java.lang.Runnable, long).
     *
     * @param task The scheduled task.
     *
     * @throws java.lang.NullPointerException If the task is null.
     * @throws java.lang.IllegalArgumentException If the task was not created by either schedule(java.lang.Runnable, long) or scheduleAtFixedRate(java.lang.Runnable, long).
     */
    void cancel(Object task);

    /**
     * Returns the *approximate* time.
     *
     * @return The *approximate* time.
     */
    double currentTimeMillis();

    /**
     * Shut down the plantScheduler thread pool.
     */
    void close();
}
