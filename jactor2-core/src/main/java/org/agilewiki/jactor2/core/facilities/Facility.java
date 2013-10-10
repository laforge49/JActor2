package org.agilewiki.jactor2.core.facilities;

import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.messages.RequestBase;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.Inbox;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Outbox;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ThreadFactory;

/**
 * Provides a thread pool for
 * non-blocking and isolation targetReactor. Multiple facilities with independent life cycles
 * are also supported.
 * (A ServiceClosedException may be thrown when messages cross facilities and the target facility is closed.)
 * In addition, the facility maintains a set of AutoClosable objects that are closed
 * when the facility is closed, as well as a table of properties.
 */

public class Facility extends BladeBase implements AutoCloseable {

    /**
     * A "compile-time" flag to turn on debug;
     */
    public final static boolean DEBUG = false;

    /**
     * The facility's internal reactor for managing the auto closeable set and for closing itself.
     */
    private final InternalReactor internalReactor;

    /**
     * A hash set of AutoCloseable objects.
     * Can only be accessed via a request to the facility.
     */
    private final Set<AutoCloseable> closeables = Collections
            .newSetFromMap(new WeakHashMap<AutoCloseable, Boolean>());

    /**
     * Set when the facility reaches end-of-life.
     * Can only be updated via a request to the facility.
     */
    private boolean shuttingDown = false;

    /**
     * When DEBUG, pendingRequests holds the active requests ordered by timestamp.
     */
    public final ConcurrentSkipListMap<Long, Set<RequestBase>> pendingRequests =
            DEBUG ? new ConcurrentSkipListMap<Long, Set<RequestBase>>() : null;

    /**
     * The logger used by targetReactor.
     */
    private final Logger messageProcessorLogger = LoggerFactory.getLogger(Reactor.class);

    /**
     * The thread pool used by Facility.
     */
    private final ThreadManager threadManager;

    /**
     * How big should the initial inbox doLocal queue size be?
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
    public Facility() throws Exception {
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
    public Facility(final int _threadCount) throws Exception {
        this(
                Inbox.DEFAULT_INITIAL_LOCAL_QUEUE_SIZE,
                Outbox.DEFAULT_INITIAL_BUFFER_SIZE,
                _threadCount,
                new DefaultThreadFactory());
    }

    /**
     * Create a Facility.
     *
     * @param _initialLocalMessageQueueSize How big should the initial inbox doLocal queue size be?
     * @param _initialBufferSize            How big should the initial outbox (per target Reactor) buffer size be?
     * @param _threadCount                  The thread pool size.
     * @param _threadFactory                The factory used to create threads for the threadpool.
     */
    public Facility(final int _initialLocalMessageQueueSize,
                    final int _initialBufferSize,
                    final int _threadCount,
                    final ThreadFactory _threadFactory) throws Exception {
        threadManager = new ThreadManager(
                _threadCount, _threadFactory);
        initialLocalMessageQueueSize = _initialLocalMessageQueueSize;
        initialBufferSize = _initialBufferSize;
        internalReactor = new InternalReactor();
        initialize(internalReactor);
    }

    /**
     * Returns the logger to be used by targetReactor.
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
     * Returns the initial doLocal message queue(s) size.
     *
     * @return The initial doLocal message queue(s) size.
     */
    public int getInitialLocalMessageQueueSize() {
        return initialLocalMessageQueueSize;
    }

    /**
     * Submit a Reactor for subsequent execution.
     *
     * @param _reactor The targetReactor to be run.
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
     * Returns a request to add an auto closeable, to be closed when the Facility closes.
     * This request returns true if the AutoClosable was added.
     *
     * @param _closeable The autoclosable to be added to the list.
     * @return The request.
     */
    public SyncRequest<Boolean> addAutoClosableSReq(final AutoCloseable _closeable) {
        return new SyncBladeRequest<Boolean>() {
            @Override
            protected Boolean processSyncRequest() throws Exception {
                if (!isClosing()) {
                    return closeables.add(_closeable);
                } else {
                    return false;
                }
            }
        };
    }

    /**
     * Returns a request to remove an auto closeable.
     * This request returns true if the AutoClosable was removed.
     *
     * @param _closeable The autoclosable to be removed.
     * @return The request.
     */
    public SyncRequest<Boolean> removeAutoClosableSReq(final AutoCloseable _closeable) {
        return new SyncBladeRequest<Boolean>() {
            @Override
            protected Boolean processSyncRequest() throws Exception {
                if (!isClosing()) {
                    return closeables.remove(_closeable);
                }
                return false;
            }
        };
    }

    @Override
    public final void close() throws Exception {
        new SyncBladeRequest<Void>() {
            @Override
            protected Void processSyncRequest() throws Exception {
                if (shuttingDown)
                    return null;
                shuttingDown = true;
                threadManager.close();
                final Iterator<AutoCloseable> it = closeables.iterator();
                while (it.hasNext()) {
                    try {
                        it.next().close();
                    } catch (final Throwable t) {
                        t.printStackTrace();
                    }
                }
                return null;
            }
        }.signal();
    }

    /**
     * Returns true if close() has been called already.
     *
     * @return true if close() has already been called.
     */
    public final boolean isClosing() {
        return shuttingDown;
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
     * @param _propertyName  The name of the property.
     * @param _propertyValue The value of the property, or null.
     * @return The prior value of the property, or null.
     */
    @Deprecated
    public Object putProperty(final String _propertyName,
                              final Object _propertyValue) {
        if (_propertyValue == null)
            return properties.remove(_propertyName);
        return properties.put(_propertyName, _propertyValue);
    }

    public SyncRequest<Object> putPropertySReq(final String _propertyName,
                                          final Object _propertyValue) {
        return new SyncBladeRequest<Object>() {
            @Override
            protected Object processSyncRequest() throws Exception {
                if (_propertyValue == null)
                    return properties.remove(_propertyName);
                return properties.put(_propertyName, _propertyValue);
            }
        };
    }

    private void firstSet(final String _propertyName,
                          final Object _propertyValue) {
        if (_propertyValue == null)
            throw new IllegalArgumentException("value may not be null");
        if (properties.get(_propertyName) != null)
            throw new IllegalStateException("old value must be null");
        properties.put(_propertyName, _propertyValue);
    }

    public SyncRequest<Void> firstSetSReq(final String _propertyName,
                                          final Object _propertyValue) {
        return new SyncBladeRequest<Void>() {
            @Override
            protected Void processSyncRequest() throws Exception {
                firstSet(_propertyName, _propertyValue);
                return null;
            }
        };
    }

    protected void setName(final String _name) {
        firstSet("name", _name);
    }

    public SyncRequest<Void> setNameSReq(final String _name) {
        return new SyncBladeRequest<Void>() {
            @Override
            protected Void processSyncRequest() throws Exception {
                setName(_name);
                return null;
            }
        };
    }

    /**
     * Returns a set view of the property names.
     *
     * @return A set view of the property names.
     */
    public Set<String> getPropertyNames() {
        return properties.keySet();
    }

    /**
     * The reactor used internally.
     */
    private class InternalReactor extends NonBlockingReactor {

        /**
         * Create an internal reactor.
         */
        public InternalReactor() throws Exception {
            super(Facility.this);
        }

        /**
         * No autoclose.
         */
        @Override
        protected void addAutoClose() throws Exception {
        }
    }
}
