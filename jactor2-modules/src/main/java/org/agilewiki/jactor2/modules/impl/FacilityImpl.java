package org.agilewiki.jactor2.modules.impl;

import org.agilewiki.jactor2.core.blades.ExceptionHandler;
import org.agilewiki.jactor2.core.impl.*;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.plant.Scheduler;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.util.Closeable;
import org.agilewiki.jactor2.core.util.Recovery;
import org.agilewiki.jactor2.core.util.immutable.ImmutableProperties;
import org.agilewiki.jactor2.modules.Activator;
import org.agilewiki.jactor2.modules.Facility;
import org.agilewiki.jactor2.modules.pubSub.RequestBus;
import org.agilewiki.jactor2.modules.pubSub.SubscribeAReq;
import org.agilewiki.jactor2.modules.pubSub.Subscription;
import org.agilewiki.jactor2.modules.transactions.properties.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class FacilityImpl extends NonBlockingReactorImpl {

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

    protected PropertiesProcessor propertiesProcessor;

    private String name;

    private MPlantImpl plantImpl;

    public FacilityImpl(final int _initialOutboxSize, final int _initialLocalQueueSize,
                        final Recovery _recovery, final Scheduler _scheduler) throws Exception {
        super(PlantImpl.getSingleton() == null ? null : PlantImpl.getSingleton().getReactor().asReactorImpl(),
                _initialOutboxSize, _initialLocalQueueSize, _recovery, _scheduler);
    }

    public void setName(final String _name) throws Exception {
        if (name != null)
            throw new IllegalStateException("name already set");
        validateName(_name);
        name = _name;
    }

    public Facility getFacility() {
        return (Facility) getReactor();
    }

    public void initialize() throws Exception {
        plantImpl = MPlantImpl.getSingleton();
        final FacilityImpl plantFacilityImpl = plantImpl.getInternalFacility().asFacilityImpl();
        final TreeMap<String, Object> initialState = new TreeMap<String, Object>();
        initialState.put(NAME_PROPERTY, name);
        propertiesProcessor = new PropertiesProcessor(this.getFacility(), initialState);
        String dependencyPrefix = dependencyPrefix(name);
        ImmutableProperties<Object> dependencies =
                plantFacilityImpl.getPropertiesProcessor().getImmutableState().subMap(dependencyPrefix);
        Iterator<String> dit = dependencies.keySet().iterator();
        while (dit.hasNext()) {
            String d = dit.next();
            String dependencyName = d.substring(dependencyPrefix.length());
            FacilityImpl dependency = plantImpl.getFacilityImpl(dependencyName);
            if (dependency == null)
                throw new IllegalStateException("dependency not present: "+dependencyName);
            dependency.addCloseable(this);
        }
        tracePropertyChangesAReq().signal();
        RequestBus<ImmutablePropertyChanges> validationBus = propertiesProcessor.validationBus;
        new SubscribeAReq<ImmutablePropertyChanges>(
                validationBus,
                this.asReactor()) {
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
                        if (!(FacilityImpl.this == plantFacilityImpl))
                            throw new UnsupportedOperationException("only a plant can have a facility");
                        if (newValue != null && !(newValue instanceof Facility))
                            throw new IllegalArgumentException(key
                                    + " not set to a Facility " + newValue);
                        if (oldValue != null && newValue != null) {
                            FacilityImpl facility = (FacilityImpl) oldValue;
                            throw new IllegalStateException("Facility already exists: "+facility.name);
                        }
                    } else if (key.startsWith(FACILITY_PREFIX)) {
                        if (!(FacilityImpl.this == plantFacilityImpl))
                            throw new UnsupportedOperationException(
                                    "only a plant can have a facility configuration property: "+key);
                        String name1 = key.substring(FACILITY_PREFIX.length());
                        int i = name1.indexOf('~');
                        if (i == -1)
                            throw new UnsupportedOperationException("undeliminated facility");
                        String name2 = name1.substring(i + 1);
                        name1 = name1.substring(0, i);
                        FacilityImpl facility0 = plantImpl.getFacilityImpl(name1);
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

    public Facility asFacility() {
        return (Facility) asReactor();
    }

    public String getName() {
        return name;
    }

    public PropertiesProcessor getPropertiesProcessor() {
        return propertiesProcessor;
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
    public void close() throws Exception {
        if (startedClosing())
            return;
        final MPlantImpl plantImpl = MPlantImpl.getSingleton();
        if ((plantImpl != null) &&
                plantImpl.getInternalFacility().asFacilityImpl() != this &&
                !plantImpl.getInternalFacility().asFacilityImpl().startedClosing()) {
            plantImpl.getInternalFacility().putPropertyAReq(FACILITY_PROPERTY_PREFIX + name,
                    null).signal();
        }
        super.close();
    }

    public void stop() throws Exception {
        if (startedClosing()) {
            plantImpl.getInternalFacility().putPropertyAReq(stoppedKey(name), true,
                    null).signal();
            return;
        }
        final MPlantImpl plantImpl = MPlantImpl.getSingleton();
        if ((plantImpl != null) &&
                plantImpl.getInternalFacility().asFacilityImpl() != this &&
                !plantImpl.getInternalFacility().asFacilityImpl().startedClosing()) {
            new PropertiesTransactionAReq(plantImpl.getInternalFacility(),
                    plantImpl.getInternalFacility().getPropertiesProcessor()){
                protected void update(final PropertiesChangeManager _changeManager) throws Exception {
                    _changeManager.put(FACILITY_PROPERTY_PREFIX + name, null);
                    _changeManager.put(stoppedKey(name), true);
                }}.signal();
            plantImpl.getInternalFacility().putPropertyAReq(FACILITY_PROPERTY_PREFIX + name,
                    null).signal();
        }
        super.close();
    }

    public void fail(final Object reason) throws Exception {
        if (startedClosing()) {
            plantImpl.getInternalFacility().putPropertyAReq(failedKey(name), reason,
                    null).signal();
            return;
        }
        final MPlantImpl plantImpl = MPlantImpl.getSingleton();
        if ((plantImpl != null) &&
                plantImpl.getInternalFacility().asFacilityImpl() != this &&
                !plantImpl.getInternalFacility().asFacilityImpl().startedClosing()) {
            new PropertiesTransactionAReq(plantImpl.getInternalFacility(),
                    plantImpl.getInternalFacility().getPropertiesProcessor()){
                protected void update(final PropertiesChangeManager _changeManager) throws Exception {
                    _changeManager.put(FACILITY_PROPERTY_PREFIX + name, null);
                    _changeManager.put(failedKey(name), reason);
                }}.signal();
            plantImpl.getInternalFacility().putPropertyAReq(FACILITY_PROPERTY_PREFIX + name,
                    null).signal();
        }
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
            public void processAsyncRequest() throws Exception {
                processAsyncResponse(getClassLoader());
            }
        };
    }

    public AsyncRequest<String> activateAReq(final String _activatorClassName) {
        return new AsyncBladeRequest<String>() {
            @Override
            public void processAsyncRequest() throws Exception {
                setExceptionHandler(new ExceptionHandler<String>() {
                    @Override
                    public String processException(Exception e) throws Exception {
                        getLogger().error("activation exception, facility "+name, e);
                        return "activation exception, "+e;
                    }
                });
                final Class<?> initiatorClass = getClassLoader().loadClass(
                        _activatorClassName);
                final Activator activator = (Activator) initiatorClass
                        .newInstance();
                activator.initialize(asReactor());
                send(activator.startAReq(), this, null);
            }
        };
    }

    public AsyncRequest<Subscription<ImmutablePropertyChanges>> tracePropertyChangesAReq() {
        return new SubscribeAReq<ImmutablePropertyChanges>(propertiesProcessor.changeBus, asReactor()) {
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
                    logger.info("\n    facility={}\n    key={}\n    old={}\n    new={}", args);
                }
            }
        };
    }

    public void facilityPoll() throws Exception {
        Iterator<Closeable> it = getCloseableSet().iterator();
        while (it.hasNext()) {
            Closeable closeable = it.next();
            if (!(closeable instanceof ReactorImpl))
                continue;
            ReactorImpl reactor = (ReactorImpl) closeable;
            reactor.reactorPoll();
        }
        reactorPoll();
    }
}
