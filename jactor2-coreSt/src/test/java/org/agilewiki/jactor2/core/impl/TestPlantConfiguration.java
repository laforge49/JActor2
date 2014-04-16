package org.agilewiki.jactor2.core.impl;

import org.agilewiki.jactor2.core.impl.stPlant.PlantConfiguration;
import org.agilewiki.jactor2.core.plant.PlantScheduler;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class TestPlantConfiguration extends PlantConfiguration {
    /**
     * Create the plant scheduler.
     *
     * @return The plant scheduler.
     */
    @Override
    protected PlantScheduler createPlantScheduler() {
        return new TestPlantScheduler();
    }

    @Override
    protected String asString(final String msg, final Throwable t) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintStream printStream = new PrintStream(baos);
        printStream.print(msg);
        printStream.print(System.lineSeparator());
        t.printStackTrace(printStream);
        printStream.flush();
        return baos.toString();
    }
}
