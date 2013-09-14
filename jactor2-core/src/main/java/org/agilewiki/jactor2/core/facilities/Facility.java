package org.agilewiki.jactor2.core.facilities;

import org.agilewiki.jactor2.core.messages.RequestBase;
import org.agilewiki.jactor2.core.reactors.Inbox;
import org.agilewiki.jactor2.core.reactors.Outbox;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Provides a thread pool for
 * non-blocking and isolation reactor. Multiple facilities with independent life cycles
 * are also supported.
 * (A ServiceClosedException may be thrown when messages cross facilities and the target facility is closed.)
 * In addition, the facility maintains a set of AutoClosable objects that are closed
 * when the facility is closed, as well as a table of properties.
 */

public class Facility implements AutoCloseable {

    /**
     * A "compile-time" flag to turn on debug;
     */
    public final static boolean DEBUG = false;

    /**
     * When DEBUG, pendingRequests holds the active requests ordered by timestamp.
     */
    public final ConcurrentSkipListMap<Long, Set<RequestBase>> pendingRequests =
            DEBUG ? new ConcurrentSkipListMap<Long, Set<RequestBase>>() : null;

    /**
     * The logger used by reactor.
     */
    private final Logger messageProcessorLogger = LoggerFactory.getLogger(Reactor.class);

    /**
     * The thread pool used by Facility.
     */
    private final ThreadManager threadManager;

    /**
     * A hash set of AutoCloseable objects.
     */
    private final Set<AutoCloseable> closeables = Collections
            .newSetFromMap(new ConcurrentHashMap<AutoCloseable, Boolean>());

    /**
     * Set when the facility reaches end-of-life.
     */
    private final AtomicBoolean shuttingDown = new AtomicBoolean();

    /**
     * How big should the initial inbox local queue size be?
     */
    private final int initialLocalMessageQueueSize;

    /**
     * How big should the initial outbox (per target Reactor) buffer size be?
     */
    private final int initialBufferSize;

    /**
     * Facility properties.
     */
    private ConcurrentSkipListMap<String, Object> properties = new ConcurrentSkipListMap<String, Object>();

    /**
     * Create a Facility.
     */
    public Facility() {
        this(
                Inbox.DEFAULT_INITIAL_LOCAL_QUEUE_SIZE,
                Outbox.DEFAULT_INITIAL_BUFFER_SIZE,
                20,
                new DefaultThreadFactory());
    }

    /**
     * Create a Facility.
     *
     * @param _threadCount The thread pool size.
     */
    public Facility(final int _threadCount) {
        this(
                Inbox.DEFAULT_INITIAL_LOCAL_QUEUE_SIZE,
                Outbox.DEFAULT_INITIAL_BUFFER_SIZE,
                _threadCount,
                new DefaultThreadFactory());
    }

    /**
     * Create a Facility.
     *
     * @param _initialLocalMessageQueueSize How big should the initial inbox local queue size be?
     * @param _initialBufferSize            How big should the initial outbox (per target Reactor) buffer size be?
     * @param _threadCount                  The thread pool size.
     * @param _threadFactory                The factory used to create threads for the threadpool.
     */
    public Facility(final int _initialLocalMessageQueueSize,
                    final int _initialBufferSize,
                    final int _threadCount,
                    final ThreadFactory _threadFactory) {
        threadManager = new ThreadManager(
                _threadCount, _threadFactory);
        initialLocalMessageQueueSize = _initialLocalMessageQueueSize;
        initialBufferSize = _initialBufferSize;
    }

    /**
     * Returns the logger to be used by reactor.
     *
     * @return A logger.
     */
    public Logger getMessageProcessorLogger() {
        return messageProcessorLogger;
    }

    /**
     * Returns the initial buffer size to be used by outboxes.
     *
     * @return The initial buffer size.
     */
    public int getInitialBufferSize() {
        return initialBufferSize;
    }

    /**
     * Returns the initial local message queue(s) size.
     *
     * @return The initial local message queue(s) size.
     */
    public int getInitialLocalMessageQueueSize() {
        return initialLocalMessageQueueSize;
    }

    /**
     * Submit a Reactor for subsequent execution.
     *
     * @param _reactor The reactor to be run.
     */
    public final void submit(final Reactor _reactor)
            throws Exception {
        try {
            threadManager.execute(_reactor);
        } catch (final Exception e) {
            if (!isClosing())
                throw e;
        } catch (final Error e) {
            if (!isClosing())
                throw e;
        }
    }

    /**
     * Adds an auto closeable, to be closed when the Facility closes.
     *
     * @param _closeable The autoclosable to be added to the list.
     * @return True, if the list was updated.
     */
    public final boolean addAutoClosable(final AutoCloseable _closeable) {
        if (!isClosing()) {
            return closeables.add(_closeable);
        } else {
            throw new IllegalStateException("Shuting down ...");
        }
    }

    /**
     * Remove an auto closeable from the list of closeables.
     *
     * @param _closeable The autoclosable to be removed from the list.
     * @return True, if the list was updated.
     */
    public final boolean removeAutoClosable(final AutoCloseable _closeable) {
        if (!isClosing()) {
            return closeables.remove(_closeable);
        }
        return false;
    }

    @Override
    public final void close() throws Exception {
        if (shuttingDown.compareAndSet(false, true)) {
            threadManager.close();
            final Iterator<AutoCloseable> it = closeables.iterator();
            while (it.hasNext()) {
                try {
                    it.next().close();
                } catch (final Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }

    /**
     * Returns true if close() has been called already.
     *
     * @return true if close() has already been called.
     */
    public final boolean isClosing() {
        return shuttingDown.get();
    }

    /**
     * Returns the value of a property.
     *
     * @param propertyName The property name.
     * @return The property value, or null.
     */
    public Object getProperty(final String propertyName) {
        return properties.get(propertyName);
    }

    /**
     * Assign a property value.
     * Or removes it if the value is set to null;
     *
     * @param propertyName  The name of the property.
     * @param propertyValue The value of the property, or null.
     * @return The prior value of the property, or null.
     */
    public Object putProperty(final String propertyName,
                              final Object propertyValue) {
        if (propertyValue == null)
            return properties.remove(propertyName);
        return properties.put(propertyName, propertyValue);
    }

    /**
     * Returns a set view of the property names.
     *
     * @return A set view of the property names.
     */
    public Set<String> getPropertyNames() {
        return properties.keySet();
    }
}
