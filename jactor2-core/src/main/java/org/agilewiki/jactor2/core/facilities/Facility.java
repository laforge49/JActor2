package org.agilewiki.jactor2.core.facilities;

import org.agilewiki.jactor2.core.blades.Activator;
import org.agilewiki.jactor2.core.blades.Blade;
import org.agilewiki.jactor2.core.blades.ExceptionHandler;
import org.agilewiki.jactor2.core.blades.NonBlockingBlade;
import org.agilewiki.jactor2.core.blades.pubSub.RequestBus;
import org.agilewiki.jactor2.core.blades.pubSub.SubscribeAReq;
import org.agilewiki.jactor2.core.blades.pubSub.Subscription;
import org.agilewiki.jactor2.core.blades.transactions.properties.*;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.RequestBase;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.plant.PlantImpl;
import org.agilewiki.jactor2.core.plant.Scheduler;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.reactors.ReactorBase;
import org.agilewiki.jactor2.core.util.Closeable;
import org.agilewiki.jactor2.core.util.Closer;
import org.agilewiki.jactor2.core.util.CloserBase;
import org.agilewiki.jactor2.core.util.Recovery;
import org.agilewiki.jactor2.core.util.immutable.ImmutableProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;

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
