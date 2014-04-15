package org.agilewiki.jactor2.core.impl;

import org.agilewiki.jactor2.core.impl.stPlant.PlantConfiguration;
import org.agilewiki.jactor2.core.plant.PlantScheduler;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.logging.Logger;

public class TestPlantConfiguration extends PlantConfiguration {
    Logger logger = Logger.getLogger("JActor2St");
    /**
     * Create the plant scheduler.
     *
     * @return The plant scheduler.
     */
    @Override
    protected PlantScheduler createPlantScheduler() {
        return new TestPlantScheduler();
    }

    private String asString(String msg, Throwable t) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos);
        printStream.print(msg);
        printStream.print(System.lineSeparator());
        t.printStackTrace(printStream);
        printStream.flush();
        return baos.toString();
    }

    /**
     * Log a message at the WARN level.
     *
     * @param msg the message string to be logged
     */
    public void warn(String msg) {
        logger.warning(msg);
    }

    /**
     * Log an exception (throwable) at the WARN level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t the exception (throwable) to log
     */
    public void warn(String msg, Throwable t) {
        logger.warning(asString(msg, t));
    }

    /**
     * Log a message at the ERROR level.
     *
     * @param msg the message string to be logged
     */
    public void error(String msg) {
        logger.severe(msg);
    }

    /**
     * Log an exception (throwable) at the ERROR level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t the exception (throwable) to log
     */
    public void error(String msg, Throwable t) {
        logger.severe(asString(msg, t));
    }
}
