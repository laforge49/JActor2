package org.agilewiki.jactor2.core.impl.stPlant;

import org.agilewiki.jactor2.core.plant.PlantScheduler;
import org.agilewiki.jactor2.core.requests.Request;

/**
 * Base class for configuring the Plant.
 * Used as the default configuration when none is specified.
 */
public class PlantConfiguration {

    private Recovery recovery;

    private PlantScheduler plantScheduler;

    /**
     * Create a plant configuration with a reactor thread pool size of 20.
     */
    public PlantConfiguration() {
    }

    /**
     * Create the default Recovery instance.
     *
     * @return The default Recovery instance.
     */
    protected Recovery createRecovery() {
        return new Recovery();
    }

    /**
     * Returns the default Recovery instance.
     *
     * @return The default Recovery instance.
     */
    public Recovery getRecovery() {
        if (recovery == null)
            recovery = createRecovery();
        return recovery;
    }

    /**
     * Create the plant scheduler.
     *
     * @return The plant scheduler.
     */
    protected PlantScheduler createPlantScheduler() {
        return null;
    }

    /**
     * Returns the plant scheduler.
     *
     * @return The plant scheduler.
     */
    public PlantScheduler getPlantScheduler() {
        if (plantScheduler == null)
            plantScheduler = createPlantScheduler();
        return plantScheduler;
    }

    /**
     * Waits for asynchronous events.
     *
     * @return Returns a Request.
     */
    public Request<?> getAsynchronousRequest() {
        throw new UnsupportedOperationException("by default, there are no asynchronous requests");
    }
}
