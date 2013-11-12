package org.agilewiki.jactor2.core.facilities;

import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.blades.pubSub.RequestBus;
import org.agilewiki.jactor2.core.blades.pubSub.SubscribeAReq;
import org.agilewiki.jactor2.core.blades.transactions.properties.ImmutablePropertyChanges;
import org.agilewiki.jactor2.core.blades.transactions.properties.PropertiesProcessor;
import org.agilewiki.jactor2.core.blades.transactions.properties.PropertyChange;
import org.agilewiki.jactor2.core.blades.transactions.properties.PropertyChangesFilter;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.RequestBase;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.util.AutoCloseableSet;
import org.agilewiki.jactor2.core.util.immutable.ImmutableProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
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

    public static final String NAME_PROPERTY = "core.facilityName";

    public static final String PLANT_NAME = "Plant";

    public static final String DEPENDENCY_PROPERTY_PREFIX = "core.dependency_";

    public static final String FACILITY_PROPERTY_PREFIX = "core.facility_";

    /**
     * The facility's internal reactor for managing the auto closeable set and for closing itself.
     */
    private final InternalReactor internalReactor;

    /**
     * A set of AutoCloseable objects.
     * Can only be accessed via a request to the facility.
     */
    private AutoCloseableSet closeables;

    /**
     * Set when the facility reaches end-of-life.
     * Can only be updated via a request to the facility.
     */
    private volatile boolean shuttingDown;

    /**
     * When DEBUG, pendingRequests holds the active requests ordered by timestamp.
     */
    @SuppressWarnings("rawtypes")
    public final ConcurrentSkipListMap<Long, Set<RequestBase>> pendingRequests = Plant.DEBUG ? new ConcurrentSkipListMap<Long, Set<RequestBase>>()
            : null;

    /**
     * The logger used by targetReactor.
     */
    private final Logger messageProcessorLogger = LoggerFactory
            .getLogger(Reactor.class);

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

    public final PropertiesProcessor propertiesProcessor;

    /**
     * Create a Facility.
     *
     * @param _name                         The name of the facility.
     * @param _initialLocalMessageQueueSize How big should the initial inbox doLocal queue size be?
     * @param _initialBufferSize            How big should the initial outbox (per target Reactor) buffer size be?
     * @param _threadCount                  The thread pool size.
     * @param _threadFactory                The factory used to create threads for the threadpool.
     */
    protected Facility(final String _name,
                       final int _initialLocalMessageQueueSize,
                       final int _initialBufferSize, final int _threadCount,
                       final ThreadFactory _threadFactory) throws Exception {
        validateName(_name);
        threadManager = new ThreadManager(_threadCount, _threadFactory);
        initialLocalMessageQueueSize = _initialLocalMessageQueueSize;
        initialBufferSize = _initialBufferSize;
        internalReactor = new InternalReactor();
        initialize(internalReactor);
        final TreeMap<String, Object> initialState = new TreeMap<String, Object>();
        initialState.put(NAME_PROPERTY, _name);
        propertiesProcessor = new PropertiesProcessor(new IsolationReactor(this), internalReactor, initialState);
        RequestBus<ImmutablePropertyChanges> validationBus = propertiesProcessor.validationBus;
        new SubscribeAReq<ImmutablePropertyChanges>(
                validationBus,
                (NonBlockingReactor) getReactor(),
                new PropertyChangesFilter("immutable.")){
            protected void processContent(final ImmutablePropertyChanges _content)
                    throws Exception {
                SortedMap<String, PropertyChange> readOnlyChanges = _content.readOnlyChanges;
                PropertyChange pc = readOnlyChanges.get(NAME_PROPERTY);
                if (pc != null && pc.oldValue != null)
                    throw new IllegalStateException(
                            "once set, this property can not be changed: "
                                    + NAME_PROPERTY);

                final Iterator<PropertyChange> it = readOnlyChanges.values().iterator();
                while (it.hasNext()) {
                    pc = it.next();
                    String name = pc.name;
                    Object oldValue = pc.oldValue;
                    Object newValue = pc.newValue;
                    if (name.startsWith(FACILITY_PROPERTY_PREFIX)) {
                        if (!(Facility.this instanceof Plant))
                            throw new UnsupportedOperationException("only a plant can have a facility");
                        if (oldValue != null)
                            throw new IllegalStateException(
                                    "once set, this property can not be changed: "
                                            + name);
                        if (!(newValue instanceof Facility))
                            throw new IllegalArgumentException(name
                                    + " not set to a Facility " + newValue);
                    }
                    if (name.startsWith(DEPENDENCY_PROPERTY_PREFIX)) {
                        if (Facility.this instanceof Plant)
                            throw new UnsupportedOperationException("a plant can not have a dependency");
                        if (oldValue != null)
                            throw new IllegalStateException(
                                    "once set, this property can not be changed: "
                                            + name);
                        if (!(newValue instanceof Facility))
                            throw new IllegalArgumentException(name
                                    + " not set to a Facility " + newValue);
                        Facility facility = (Facility) newValue;
                        if (facility.hasDependency(Facility.this.getName()))
                            throw new IllegalArgumentException(
                                    "Would create a dependency cycle.");
                    }
                }
            }
        }.signal();
    }

    /**
     * Returns the AutoCloseableSet. Creates it if needed.
     */
    protected final AutoCloseableSet getAutoCloseableSet() {
        if (closeables == null) {
            closeables = new AutoCloseableSet();
        }
        return closeables;
    }

    protected void validateName(final String _name) throws Exception {
        if (_name == null) {
            throw new IllegalArgumentException("name may not be null");
        }
        if (_name.length() == 0) {
            throw new IllegalArgumentException("name may not be empty");
        }
        if (_name.contains(" ")) {
            throw new IllegalArgumentException("name may not contain spaces: "
                    + _name);
        }
        if (_name.equals(PLANT_NAME)) {
            throw new IllegalArgumentException("name may be " + PLANT_NAME);
        }
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
    public final void submit(final Reactor _reactor) throws Exception {
        try {
            threadManager.execute(_reactor);
        } catch (final Exception e) {
            if (!isClosing()) {
                throw e;
            }
        } catch (final Error e) {
            if (!isClosing()) {
                throw e;
            }
        }
    }

    /**
     * Returns a request to add an auto closeable, to be closed when the Facility closes.
     * This request returns true if the AutoClosable was added.
     *
     * @param _closeable The autoclosable to be added to the list.
     * @return The request.
     */
    public SyncRequest<Boolean> addAutoClosableSReq(
            final AutoCloseable _closeable) {
        return new SyncBladeRequest<Boolean>() {
            @Override
            protected Boolean processSyncRequest() throws Exception {
                if (!isClosing()) {
                    return getAutoCloseableSet().add(_closeable);
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
    public SyncRequest<Boolean> removeAutoClosableSReq(
            final AutoCloseable _closeable) {
        return new SyncBladeRequest<Boolean>() {
            @Override
            protected Boolean processSyncRequest() throws Exception {
                if (!isClosing()) {
                    return (closeables == null) ? false : closeables
                            .remove(_closeable);
                }
                return false;
            }
        };
    }

    @Override
    public void close() throws Exception {
        closeSReq().signal();
    }

    /**
     * Returns a Request to perform a close().
     */
    public SyncRequest<Void> closeSReq() {
        return new SyncBladeRequest<Void>() {
            @Override
            protected Void processSyncRequest() throws Exception {
                if (shuttingDown) {
                    return null;
                }
                shuttingDown = true;
                final Plant plant = getPlant();
                if ((plant != null) && (plant != Facility.this)) {
                    plant.removeAutoClosableSReq(this).signal();
                    plant.putPropertyAReq(FACILITY_PROPERTY_PREFIX + getName(),
                            null).signal();
                }
                threadManager.close();
                if (closeables != null) {
                    closeables.close();
                }
                return null;
            }
        };
    }

    /**
     * Returns true if close() has been called already.
     * Can be called from anywhere.
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
        return propertiesProcessor.getImmutableState().get(propertyName);
    }

    public String getName() {
        return (String) getProperty(NAME_PROPERTY);
    }

    public Plant getPlant() {
        return (Plant) getProperty(DEPENDENCY_PROPERTY_PREFIX + PLANT_NAME);
    }

    public AsyncRequest<Void> putPropertyAReq(final String _propertyName,
                                              final Object _propertyValue) {
        return propertiesProcessor.putAReq(_propertyName, _propertyValue);
    }

    public AsyncRequest<Void> putPropertyAReq(final String _propertyName,
                                              final Object _expectedValue,
                                              final Object _propertyValue) {
        return propertiesProcessor.compareAndSetAReq(_propertyName, _expectedValue, _propertyValue);
    }

    public boolean hasDependency(final String _name) throws Exception {
        if (getProperty(DEPENDENCY_PROPERTY_PREFIX + _name) != null)
            return true;
        final ImmutableProperties<Object> immutableProperties = propertiesProcessor.getImmutableState();
        final ImmutableProperties<Object> subMap = immutableProperties.subMap(DEPENDENCY_PROPERTY_PREFIX);
        final Collection<Object> values = subMap.values();
        if (values.size() == 0)
            return false;
        final Iterator<Object> it = values.iterator();
        while (it.hasNext()) {
            final Facility dependency = (Facility) it.next();
            if (dependency.hasDependency(_name))
                return false;
        }
        return true;
    }

    public AsyncRequest<Void> dependencyAReq(final Facility _dependency) {
        return new AsyncBladeRequest<Void>() {

            AsyncResponseProcessor<Void> dis = this;

            @Override
            protected void processAsyncRequest() throws Exception {
                final String myName = getName();
                if (myName == null) {
                    throw new IllegalStateException(
                            "assign a name before adding a dependency");
                }
                final String name = _dependency.getName();
                if (name == null) {
                    throw new IllegalArgumentException(
                            "the dependency has no name");
                }
                final String propertyName = DEPENDENCY_PROPERTY_PREFIX + name;
                if (getProperty(propertyName) != null) {
                    throw new IllegalStateException(
                            "the dependency was already present");
                }
                if (_dependency.hasDependency(myName))
                    throw new IllegalArgumentException(
                            "this would create a cyclic dependency");
                send(propertiesProcessor.putAReq(propertyName, _dependency),
                        new AsyncResponseProcessor<Void>() {
                            @Override
                            public void processAsyncResponse(Void _response) throws Exception {
                                send(_dependency.addAutoClosableSReq(Facility.this), dis, null);
                            }
                        });
            }
        };
    }

    protected ClassLoader getClassLoader() throws Exception {
        return getClass().getClassLoader();
    }

    public AsyncRequest<ClassLoader> getClassLoaderAReq() {
        return new AsyncBladeRequest<ClassLoader>() {
            @Override
            protected void processAsyncRequest() throws Exception {
                processAsyncResponse(getClassLoader());
            }
        };
    }

    public AsyncRequest<Void> initiateAReq(final String _initiatorClassName) {
        return new AsyncBladeRequest<Void>() {
            @Override
            protected void processAsyncRequest() throws Exception {
                final Class<?> initiatorClass = getClassLoader().loadClass(
                        _initiatorClassName);
                final Initiator initiator = (Initiator) initiatorClass
                        .newInstance();
                initiator.initialize(getReactor());
                send(initiator.startAReq(), this);
            }
        };
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
