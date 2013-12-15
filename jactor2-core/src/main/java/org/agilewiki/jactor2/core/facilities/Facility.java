package org.agilewiki.jactor2.core.facilities;

import org.agilewiki.jactor2.core.blades.Activator;
import org.agilewiki.jactor2.core.blades.ExceptionHandler;
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

public class Facility extends CloserBase {

    public static final String CORE_PREFIX = "core.";

    public static final String NAME_PROPERTY = CORE_PREFIX+"facilityName";

    public static final String PLANT_NAME = "Plant";

    public static final String FACILITY_PROPERTY_PREFIX = CORE_PREFIX+"facility_";

    public static final String FACILITY_PREFIX = "facility_";

    public static final String FACILITY_DEPENDENCY_INFIX = CORE_PREFIX+"dependency_";

    public static String dependencyPrefix(final String _facilityName) {
        return FACILITY_PREFIX+_facilityName+"~"+FACILITY_DEPENDENCY_INFIX;
    }

    public static final String FACILITY_RECOVERY_POSTFIX = CORE_PREFIX+"recovery";

    public static String recoveryKey(final String _facilityName) {
        return FACILITY_PREFIX+_facilityName+"~"+FACILITY_RECOVERY_POSTFIX;
    }

    public static final String FACILITY_INITIAL_LOCAL_MESSAGE_QUEUE_SIZE_POSTFIX = CORE_PREFIX+"initialLocalMessageQueueSize";

    public static String initialLocalMessageQueueSizeKey(final String _facilityName) {
        return FACILITY_PREFIX+_facilityName+"~"+FACILITY_INITIAL_LOCAL_MESSAGE_QUEUE_SIZE_POSTFIX;
    }

    public static final String FACILITY_INITIAL_BUFFER_SIZE_POSTFIX = CORE_PREFIX+"initialBufferSize";

    public static String initialBufferSizeKey(final String _facilityName) {
        return FACILITY_PREFIX+_facilityName+"~"+FACILITY_INITIAL_BUFFER_SIZE_POSTFIX;
    }

    public static final String FACILITY_ACTIVATOR_POSTFIX = CORE_PREFIX+"activator";

    public static String activatorKey(final String _facilityName) {
        return FACILITY_PREFIX+_facilityName+"~"+FACILITY_ACTIVATOR_POSTFIX;
    }

    public static String FACILITY_AUTO_START_POSTFIX = CORE_PREFIX+"autoStart";

    public static String autoStartKey(final String _facilityName) {
        return FACILITY_PREFIX+_facilityName+"~"+FACILITY_AUTO_START_POSTFIX;
    }

    public static String FACILITY_FAILED_POSTFIX = CORE_PREFIX+"failed";

    public static String failedKey(final String _facilityName) {
        return FACILITY_PREFIX+_facilityName+"~"+FACILITY_FAILED_POSTFIX;
    }

    public static String FACILITY_STOPPED_POSTFIX = CORE_PREFIX+"stopped";

    public static String stoppedKey(final String _facilityName) {
        return FACILITY_PREFIX+_facilityName+"~"+FACILITY_STOPPED_POSTFIX;
    }

    public Recovery recovery;
    public Scheduler scheduler;

    /**
     * The facility's internal reactor for managing the auto closeable set and for closing itself.
     */
    protected InternalReactor internalReactor;

    /**
     * Set when the facility reaches end-of-life.
     * Can only be updated via a request to the facility.
     */
    protected volatile boolean shuttingDown;

    private volatile boolean startClosing;

    /**
     * When DEBUG, pendingRequests holds the active requests ordered by timestamp.
     */
    @SuppressWarnings("rawtypes")
    public final ConcurrentSkipListMap<Long, Set<RequestBase>> pendingRequests = PlantImpl.DEBUG ? new ConcurrentSkipListMap<Long, Set<RequestBase>>()
            : null;

    /**
     * The logger used by targetReactor.
     */
    protected final Logger log = LoggerFactory
            .getLogger(Reactor.class);

    /**
     * How big should the initial inbox doLocal queue size be?
     */
    public int initialLocalMessageQueueSize;

    /**
     * How big should the initial outbox (per target Reactor) buffer size be?
     */
    public int initialBufferSize;

    protected PropertiesProcessor propertiesProcessor;

    public final String name;

    protected Plant plant;

    private PlantImpl plantImpl;

    /**
     * Create a Facility.
     *
     * @param _name                         The name of the facility.
     */
    public Facility(final String _name) throws Exception {
        validateName(_name);
        name = _name;
    }

    public void initialize(final Plant _plant, final PlantImpl _plantImpl) throws Exception {
        plant = _plant;
        plantImpl = _plantImpl;
        internalReactor = new InternalReactor();
        initialize(internalReactor);
        if (this != plantImpl)
            plantImpl.addCloseable(this);
        final TreeMap<String, Object> initialState = new TreeMap<String, Object>();
        initialState.put(NAME_PROPERTY, name);
        propertiesProcessor = new PropertiesProcessor(new IsolationReactor(this), internalReactor, initialState);
        String dependencyPrefix = dependencyPrefix(name);
        ImmutableProperties<Object> dependencies =
                plantImpl.getPropertiesProcessor().getImmutableState().subMap(dependencyPrefix);
        Iterator<String> dit = dependencies.keySet().iterator();
        while (dit.hasNext()) {
            String d = dit.next();
            String dependencyName = d.substring(dependencyPrefix.length());
            Facility dependency = plantImpl.getFacility(dependencyName);
            if (dependency == null)
                throw new IllegalStateException("dependency not present: "+dependencyName);
            dependency.addCloseable(this);
        }
        tracePropertyChangesAReq().signal();
        RequestBus<ImmutablePropertyChanges> validationBus = propertiesProcessor.validationBus;
        new SubscribeAReq<ImmutablePropertyChanges>(
                validationBus,
                (NonBlockingReactor) internalReactor) {
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
                    String key = pc.name;
                    Object oldValue = pc.oldValue;
                    Object newValue = pc.newValue;
                    if (key.startsWith(FACILITY_PROPERTY_PREFIX)) {
                        if (!(Facility.this instanceof PlantImpl))
                            throw new UnsupportedOperationException("only a plant can have a facility");
                        if (newValue != null && !(newValue instanceof Facility))
                            throw new IllegalArgumentException(key
                                    + " not set to a Facility " + newValue);
                        if (oldValue != null && newValue != null) {
                            Facility facility = (Facility) oldValue;
                            throw new IllegalStateException("Facility already exists: "+facility.name);
                        }
                    } else if (key.startsWith(FACILITY_PREFIX)) {
                        if (!(Facility.this instanceof PlantImpl))
                            throw new UnsupportedOperationException(
                                    "only a plant can have a facility configuration property: "+key);
                        String name1 = key.substring(FACILITY_PREFIX.length());
                        int i = name1.indexOf('~');
                        if (i == -1)
                            throw new UnsupportedOperationException("undeliminated facility");
                        String name2 = name1.substring(i + 1);
                        name1 = name1.substring(0, i);
                        Facility facility0 = plantImpl.getFacility(name1);
                        if (name2.startsWith(FACILITY_DEPENDENCY_INFIX)) {
                            if (facility0 != null) {
                                throw new IllegalStateException(
                                        "the dependency properties can not change while a facility is running ");
                            }
                            name2 = name2.substring(FACILITY_DEPENDENCY_INFIX.length());
                            if (PLANT_NAME.equals(name1))
                                throw new UnsupportedOperationException("a plant can not have a dependency");
                            if (plantImpl.hasDependency(name2, key))
                                throw new IllegalArgumentException(
                                        "Would create a dependency cycle.");
                        } else if (name2.equals(FACILITY_RECOVERY_POSTFIX)) {
                            if (facility0 != null) {
                                throw new IllegalStateException(
                                        "the recovery property can not change while a facility is running ");
                            }
                            if (PLANT_NAME.equals(name1))
                                throw new UnsupportedOperationException("a plant can not have a recovery property");
                            if (newValue != null && !(newValue instanceof Recovery))
                                throw new IllegalArgumentException("recovery value must implement Recovery");
                        } else if (name2.equals(FACILITY_INITIAL_LOCAL_MESSAGE_QUEUE_SIZE_POSTFIX)) {
                            if (facility0 != null) {
                                throw new IllegalStateException(
                                        "the initial local message queue size property can not change while a facility is running ");
                            }
                            if (PLANT_NAME.equals(name1))
                                throw new UnsupportedOperationException(
                                        "a plant can not have an initial local message queue size property");
                            if (newValue != null && !(newValue instanceof Integer))
                                throw new IllegalArgumentException(
                                        "the initial local message queue size property value must be an Integer");
                        } else if (name2.equals(FACILITY_INITIAL_BUFFER_SIZE_POSTFIX)) {
                            if (facility0 != null) {
                                throw new IllegalStateException(
                                        "the initial buffer size property can not change while a facility is running ");
                            }
                            if (PLANT_NAME.equals(name1))
                                throw new UnsupportedOperationException(
                                        "a plant can not have an initial buffer size property");
                            if (newValue != null && !(newValue instanceof Integer))
                                throw new IllegalArgumentException(
                                        "the initial buffer size property value must be an Integer");
                        } else if (name2.equals(FACILITY_ACTIVATOR_POSTFIX)) {
                            if (facility0 != null) {
                                throw new IllegalStateException(
                                        "the activator property can not change while a facility is running ");
                            }
                            if (PLANT_NAME.equals(name1))
                                throw new UnsupportedOperationException(
                                        "a plant can not have an activator property");
                            if (newValue != null && !(newValue instanceof String))
                                throw new IllegalArgumentException(
                                        "the activator property value must be a String");
                        }
                    }
                }
            }
        }.signal();
    }

    public InternalReactor getInternalReactor() {
        return internalReactor;
    }

    public PropertiesProcessor getPropertiesProcessor() {
        return propertiesProcessor;
    }

    @Override
    protected final boolean startedClosing() {
        return startClosing;
    }

    public final boolean isShuttingDown() {
        return shuttingDown;
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
        if (_name.contains("~")) {
            throw new IllegalArgumentException("name may not contain ~: "
                    + _name);
        }
        if (_name.equals(PLANT_NAME)) {
            throw new IllegalArgumentException("name may be " + PLANT_NAME);
        }
    }

    @Override
    public Logger getLog() {
        return log;
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

    @Override
    public void close() throws Exception {
        if (startClosing)
            return;
        startClosing = true;
        final Plant plant = getPlant();
        if ((plant != null) && (plantImpl != Facility.this && !plantImpl.startedClosing())) {
            plantImpl.putPropertyAReq(FACILITY_PROPERTY_PREFIX + name,
                    null).signal();
        }
        closeAll();
    }

    public void stop() throws Exception {
        if (startClosing) {
            plantImpl.putPropertyAReq(stoppedKey(name), true,
                    null).signal();
            return;
        }
        startClosing = true;
        final Plant plant = getPlant();
        if ((plant != null) && (plantImpl != Facility.this && !plantImpl.startedClosing())) {
            new PropertiesTransactionAReq(plantImpl.getPropertiesProcessor().commonReactor,
                    plantImpl.getPropertiesProcessor()){
                protected void update(final PropertiesChangeManager _changeManager) throws Exception {
                    _changeManager.put(FACILITY_PROPERTY_PREFIX + name, null);
                    _changeManager.put(stoppedKey(name), true);
                }}.signal();
            plantImpl.putPropertyAReq(FACILITY_PROPERTY_PREFIX + name,
                    null).signal();
        }
        closeAll();
    }

    public void fail(final Object reason) throws Exception {
        if (startClosing) {
            plantImpl.putPropertyAReq(failedKey(name), reason,
                    null).signal();
            return;
        }
        startClosing = true;
        final Plant plant = getPlant();
        if ((plant != null) && (plantImpl != Facility.this && !plantImpl.startedClosing())) {
            new PropertiesTransactionAReq(plantImpl.getPropertiesProcessor().commonReactor,
                    plantImpl.getPropertiesProcessor()){
                protected void update(final PropertiesChangeManager _changeManager) throws Exception {
                    _changeManager.put(FACILITY_PROPERTY_PREFIX + name, null);
                    _changeManager.put(failedKey(name), reason);
                }}.signal();
            plantImpl.putPropertyAReq(FACILITY_PROPERTY_PREFIX + name,
                    null).signal();
        }
        closeAll();
    }


    protected void close2() throws Exception {
        if (shuttingDown) {
            return;
        }
        internalReactor.close();
        shuttingDown = true;
        super.close();
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

    public Plant getPlant() {
        return plant;
    }

    public PlantImpl getPlantImpl() {
        return plantImpl;
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

    public AsyncRequest<String> activateAReq(final String _activatorClassName) {
        return new AsyncBladeRequest<String>() {
            @Override
            protected void processAsyncRequest() throws Exception {
                setExceptionHandler(new ExceptionHandler<String>() {
                    @Override
                    public String processException(Exception e) throws Exception {
                        getLog().error("activation exception, facility "+name, e);
                        return "activation exception, "+e;
                    }
                });
                final Class<?> initiatorClass = getClassLoader().loadClass(
                        _activatorClassName);
                final Activator activator = (Activator) initiatorClass
                        .newInstance();
                activator.initialize(getReactor());
                send(activator.startAReq(), this, null);
            }
        };
    }

    public AsyncRequest<Subscription<ImmutablePropertyChanges>> tracePropertyChangesAReq() {
        return new SubscribeAReq<ImmutablePropertyChanges>(propertiesProcessor.changeBus, internalReactor) {
            @Override
            protected void processContent(final ImmutablePropertyChanges _content)
                    throws Exception {
                SortedMap<String, PropertyChange> readOnlyChanges = _content.readOnlyChanges;
                final Iterator<PropertyChange> it = readOnlyChanges.values().iterator();
                while (it.hasNext()) {
                    final PropertyChange propertyChange = it.next();
                    String[] args = {
                            name,
                            propertyChange.name,
                            "" + propertyChange.oldValue,
                            "" + propertyChange.newValue
                    };
                    log.info("\n    facility={}\n    key={}\n    old={}\n    new={}", args);
                }
            }
        };
    }

    public void facilityPoll() throws Exception {
                Iterator<Closeable> it = getCloseableSet().iterator();
                while (it.hasNext()) {
                    Closeable closeable = it.next();
                    if (!(closeable instanceof ReactorBase))
                        continue;
                    ReactorBase reactor = (ReactorBase) closeable;
                    reactor.reactorPoll();
                }
                internalReactor.reactorPoll();
    }

    /**
     * The reactor used internally, is not a functional closer.
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
        protected void addClose() throws Exception {
        }

        @Override
        public void addCloser(Closer _closer) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void removeCloser(Closer _closer) {
        }
    }
}
