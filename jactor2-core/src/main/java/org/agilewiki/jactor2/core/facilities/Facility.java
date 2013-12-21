package org.agilewiki.jactor2.core.facilities;

import org.agilewiki.jactor2.core.blades.NonBlockingBlade;
import org.agilewiki.jactor2.core.blades.transactions.properties.PropertiesProcessor;
import org.agilewiki.jactor2.core.impl.FacilityImpl;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.util.Closer;
import org.slf4j.Logger;

/**
 * Provides a thread pool for
 * non-blocking and isolation targetReactor. Multiple facilities with independent life cycles
 * are also supported.
 * (A ServiceClosedException may be thrown when messages cross facilities and the target facility is closed.)
 * In addition, the facility maintains a set of AutoClosable objects that are closed
 * when the facility is closed, as well as a table of properties.
 */

public interface Facility extends Closer, NonBlockingBlade {
    FacilityImpl asFacilityImpl();

    Plant getPlant();

    boolean startedClosing();

    void close() throws Exception;

    String getName();

    Logger getLog();

    PropertiesProcessor getPropertiesProcessor();
}
