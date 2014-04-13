package org.agilewiki.jactor2.core.impl.stPlant;

//import java.util.concurrent.ScheduledFuture;

/**
 * A scheduler for Plant, created by PlantConfiguration.
 */
public class DefaultPlantScheduler {
    /**
     * Schedule a Runnable to be run at a later time.
     *
     * @param _runnable          The Runnable to be scheduled.
     * @param _millisecondDelay How long to wait before the Runnable is to be run.
     */
    /*
    public ScheduledFuture<?> schedule(Runnable _runnable, long _millisecondDelay) {

    }
    */

    /**
     * Schedule a Runnable to be run repeatedly.
     *
     * @param _runnable          The Runnable to be run.
     * @param _millisecondDelay The delay between each run.
     */
    /*
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable _runnable, long _millisecondDelay) {

    }
    */

    /**
     * Returns the approximate time.
     *
     * @return The approximate time.
     */
    public long currentTimeMillis() {
        return 0;
    }

    /**
     * Shut down the plantScheduler thread pool.
     */
    void close() {

    }
}
