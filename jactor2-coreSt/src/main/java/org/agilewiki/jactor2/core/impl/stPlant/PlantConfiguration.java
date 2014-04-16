package org.agilewiki.jactor2.core.impl.stPlant;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.agilewiki.jactor2.core.plant.PlantScheduler;

/**
 * Base class for configuring the Plant.
 * Used as the default configuration when none is specified.
 */
abstract public class PlantConfiguration {
    /** The JActor2-St Logger. */
    private static final Logger LOGGER = Logger.getLogger("JActor2St");

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
    abstract protected PlantScheduler createPlantScheduler();

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
     * Log a message at the WARN level.
     *
     * @param msg the message string to be logged
     */
    public void warn(final String msg) {
        LOGGER.warning(msg);
    }

    /**
     * Log an exception (throwable) at the WARN level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t the exception (throwable) to log
     */
    public void warn(final String msg, final Throwable t) {
        LOGGER.log(Level.WARNING, msg, t);
    }

    /**
     * Log a message at the ERROR level.
     *
     * @param msg the message string to be logged
     */
    public void error(final String msg) {
        LOGGER.severe(msg);
    }

    /**
     * Log an exception (throwable) at the ERROR level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t the exception (throwable) to log
     */
    public void error(final String msg, final Throwable t) {
        LOGGER.log(Level.SEVERE, msg, t);
    }
}
