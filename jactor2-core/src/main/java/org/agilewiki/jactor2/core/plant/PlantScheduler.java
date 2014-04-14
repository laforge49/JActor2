package org.agilewiki.jactor2.core.plant;

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
    Object schedule(Runnable _runnable, long _millisecondDelay);

    /**
     * Schedule a Runnable to be run repeatedly.
     * The return value is an opaque task object, that can be cancelled.
     *
     * @param _runnable          The Runnable to be run.
     * @param _millisecondDelay The delay between each run.
     */
    Object scheduleAtFixedRate(Runnable _runnable, long _millisecondDelay);

    /**
     * Cancels a task, that was scheduled with either schedule(java.lang.Runnable, long)
     * or scheduleAtFixedRate(java.lang.Runnable, long).
     *
     * @param _task The scheduled task.
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
    long currentTimeMillis();

    /**
     * Shut down the plantScheduler thread pool.
     */
    void close();
}
