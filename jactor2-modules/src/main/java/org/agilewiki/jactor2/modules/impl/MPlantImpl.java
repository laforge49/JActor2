package org.agilewiki.jactor2.modules.impl;

import org.agilewiki.jactor2.core.impl.PlantImpl;
import org.agilewiki.jactor2.core.plant.PlantConfiguration;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.ReactorClosedException;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.ExceptionHandler;
import org.agilewiki.jactor2.modules.Facility;
import org.agilewiki.jactor2.modules.FacilityAlreadyPresentException;
import org.agilewiki.jactor2.modules.immutable.ImmutableProperties;
import org.agilewiki.jactor2.modules.pubSub.RequestBus;
import org.agilewiki.jactor2.modules.pubSub.SubscribeAReq;
import org.agilewiki.jactor2.modules.transactions.properties.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.SortedMap;

public class MPlantImpl extends PlantImpl {

    public static final String CORE_PREFIX = "core.";

    public static final String PLANT_NAME = "Plant";

    public static final String FACILITY_PROPERTY_PREFIX = CORE_PREFIX+"facility_";

    public static final String FACILITY_PREFIX = "facility_";

    public static final String FACILITY_DEPENDENCY_INFIX = CORE_PREFIX+"dependency_";

    public static String dependencyPrefix(final String _facilityName) {
        return FACILITY_PREFIX+_facilityName+"~"+FACILITY_DEPENDENCY_INFIX;
    }

    public static final String FACILITY_INITIAL_LOCAL_MESSAGE_QUEUE_SIZE_POSTFIX =
            CORE_PREFIX+"initialLocalMessageQueueSize";

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

    public static MPlantImpl getSingleton() {
        return (MPlantImpl) PlantImpl.getSingleton();
    }

    private final PropertiesProcessor propertiesProcessor;

    public MPlantImpl() throws Exception {
        this(new PlantConfiguration());
    }

    public MPlantImpl(final int _threadCount) throws Exception {
        this(new PlantConfiguration(_threadCount));
    }

    public MPlantImpl(final PlantConfiguration _plantConfiguration) throws Exception {
        super(_plantConfiguration);
        getInternalFacility().asFacilityImpl().setName(PLANT_NAME);
        propertiesProcessor = getInternalFacility().getPropertiesProcessor();
        validate();
        changes();
        long reactorPollMillis = _plantConfiguration.getRecovery().getReactorPollMillis();
        _plantConfiguration.getPlantScheduler().scheduleAtFixedRate(plantPoll(),
                reactorPollMillis);
    }

    private void validate() throws Exception {
        RequestBus<ImmutablePropertyChanges> validationBus = propertiesProcessor.validationBus;
        new SubscribeAReq<ImmutablePropertyChanges>(
                validationBus,
                getInternalFacility()) {
            protected void processContent(final ImmutablePropertyChanges _content)
                    throws Exception {
                SortedMap<String, PropertyChange> readOnlyChanges = _content.readOnlyChanges;
                PropertyChange pc;
                final Iterator<PropertyChange> it = readOnlyChanges.values().iterator();
                while (it.hasNext()) {
                    pc = it.next();
                    String key = pc.name;
                    Object oldValue = pc.oldValue;
                    Object newValue = pc.newValue;
                    if (key.startsWith(FACILITY_PROPERTY_PREFIX)) {
                        if (newValue != null && !(newValue instanceof Facility))
                            throw new IllegalArgumentException(key
                                    + " not set to a Facility " + newValue);
                        if (oldValue != null && newValue != null) {
                            FacilityImpl facilityImpl = ((Facility) oldValue).asFacilityImpl();
                            throw new FacilityAlreadyPresentException(facilityImpl.getName());
                        }
                    } else if (key.startsWith(FACILITY_PREFIX)) {
                        String name1 = key.substring(FACILITY_PREFIX.length());
                        int i = name1.indexOf('~');
                        if (i == -1)
                            throw new UnsupportedOperationException("undeliminated facility");
                        String name2 = name1.substring(i + 1);
                        name1 = name1.substring(0, i);
                        FacilityImpl facility0 = getFacilityImpl(name1);
                        if (name2.startsWith(FACILITY_DEPENDENCY_INFIX)) {
                            if (facility0 != null) {
                                throw new IllegalStateException(
                                        "the dependency properties can not change while a facility is running ");
                            }
                            name2 = name2.substring(FACILITY_DEPENDENCY_INFIX.length());
                            if (PLANT_NAME.equals(name1))
                                throw new UnsupportedOperationException("a plant can not have a dependency");
                            if (hasDependency(name2, key))
                                throw new IllegalArgumentException(
                                        "Would create a dependency cycle.");
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

    public void changes() throws Exception {
        RequestBus<ImmutablePropertyChanges> changeBus = propertiesProcessor.changeBus;
        new SubscribeAReq<ImmutablePropertyChanges>(
                changeBus,
                getInternalFacility()) {
            protected void processContent(final ImmutablePropertyChanges _content)
                    throws Exception {
                SortedMap<String, PropertyChange> readOnlyChanges = _content.readOnlyChanges;
                final Iterator<PropertyChange> it = readOnlyChanges.values().iterator();
                while (it.hasNext()) {
                    PropertyChange pc = it.next();
                    String key = pc.name;
                    Object newValue = pc.newValue;
                    if (key.startsWith(FACILITY_PROPERTY_PREFIX) && newValue != null) {
                        if (pc.oldValue != null) {
                            throw new IllegalStateException("facility already exists");
                        }
                        String facilityName = ((Facility) newValue).asFacilityImpl().getName();
                        ImmutableProperties<Object> immutableProperties = _content.immutableProperties;
                        ImmutableProperties<Object> facilityProperties = immutableProperties.subMap(FACILITY_PREFIX);
                        Iterator<String> kit = facilityProperties.keySet().iterator();
                        String postfix = "~"+FACILITY_DEPENDENCY_INFIX+facilityName;
                        while (kit.hasNext()) {
                            String pk = kit.next();
                            if (!pk.endsWith(postfix))
                                continue;
                            String dependentName = pk.substring(FACILITY_PREFIX.length(), pk.length()-postfix.length());
                            autoStartAReq(dependentName).signal();
                        }
                    } else if (key.startsWith(FACILITY_PREFIX)) {
                        String name1 = key.substring(FACILITY_PREFIX.length());
                        int i = name1.indexOf('~');
                        if (i == -1)
                            throw new UnsupportedOperationException("undeliminated facility");
                        String name2 = name1.substring(i + 1);
                        name1 = name1.substring(0, i);
                        if (name2.startsWith(FACILITY_AUTO_START_POSTFIX)) {
                            if (newValue != null)
                                autoStartAReq(name1).signal();
                        } else if (name2.startsWith(FACILITY_FAILED_POSTFIX)) {
                            if (newValue == null)
                                autoStartAReq(name1).signal();
                        } else if (name2.startsWith(FACILITY_STOPPED_POSTFIX)) {
                            if (newValue == null)
                                autoStartAReq(name1).signal();
                        }
                    }
                }
            }
        }.signal();
    }

    public Facility getInternalFacility() {
        return (Facility) getInternalReactor();
    }

    public Object getProperty(final String propertyName) {
        return getInternalFacility().getProperty(propertyName);
    }

    protected NonBlockingReactor createInternalReactor() {
        return new Facility();
    }

    private Runnable plantPoll() {
        return new Runnable() {
            public void run() {
                try {
                    getInternalFacility().asFacilityImpl().facilityPoll();
                } catch (Exception x) {
                    x.printStackTrace();
                }
            }
        };
    }

    private AsyncRequest<String> autoStartAReq(final String _facilityName) {
        return new AsyncRequest<String>(getInternalReactor()) {
            @Override
            public void processAsyncRequest() throws Exception {
                if (getFacilityImpl(_facilityName) != null) {
                    processAsyncResponse(null);
                    return;
                }
                if (!isAutoStart(_facilityName)) {
                    processAsyncResponse(null);
                    return;
                }
                if (getFailed(_facilityName) != null) {
                    processAsyncResponse(null);
                    return;
                }
                if (isStopped(_facilityName)) {
                    processAsyncResponse(null);
                    return;
                }
                String dependencyPrefix = dependencyPrefix(_facilityName);
                ImmutableProperties<Object> dependencies =
                        propertiesProcessor.getImmutableState().subMap(dependencyPrefix);
                Iterator<String> dit = dependencies.keySet().iterator();
                while (dit.hasNext()) {
                    String d = dit.next();
                    String dependencyName = d.substring(dependencyPrefix.length());
                    if (getFacilityImpl(dependencyName) == null)
                        processAsyncResponse(null);
                }
                setExceptionHandler(new ExceptionHandler<String>() {
                    @Override
                    public String processException(Exception e) throws Exception {
                        if (e instanceof ReactorClosedException)
                            return null;
                        else
                            return "create facility exception: " + e;
                    }
                });
                send(Facility.createFacilityAReq(_facilityName), this, null);
            }
        };
    }

    public void stopFacility(final String _facilityName) throws Exception {
        FacilityImpl facility = getFacilityImpl(_facilityName);
        if (facility == null) {
            getInternalFacility().putPropertyAReq(stoppedKey(_facilityName), true).signal();
            return;
        }
        facility.stop();
    }

    public void failFacility(final String _facilityName, final Object reason) throws Exception {
        FacilityImpl facility = getFacilityImpl(_facilityName);
        if (facility == null) {
            getInternalFacility().putPropertyAReq(failedKey(_facilityName), reason).signal();
            return;
        }
        facility.fail(reason);
    }

    public AsyncRequest<Void> dependencyPropertyAReq(final String _dependentName, final String _dependencyName) {
        return new AsyncRequest<Void>(getInternalReactor()) {

            AsyncResponseProcessor<Void> dis = this;

            String dependencyPropertyName;

            @Override
            public void processAsyncRequest() throws Exception {
                final String name = _dependencyName;
                if (_dependencyName == null) {
                    throw new IllegalArgumentException(
                            "the dependency name may not be null");
                }
                if (PLANT_NAME.equals(_dependencyName))
                    dis.processAsyncResponse(null);
                if (PLANT_NAME.equals(_dependentName))
                    throw new IllegalArgumentException("Plant may not have a dependency");
                dependencyPropertyName = dependencyPrefix(_dependentName) + name;
                if (getProperty(dependencyPropertyName) != null) {
                    throw new IllegalStateException(
                            "the dependency was already present");
                }
                if (hasDependency(_dependencyName, _dependentName))
                    throw new IllegalArgumentException(
                            "this would create a cyclic dependency");
                send(propertiesProcessor.putAReq(dependencyPropertyName, true), dis);
            }
        };
    }

    public boolean hasDependency(final String _dependentName, final String _dependencyName) throws Exception {
        String prefix = FACILITY_PREFIX + _dependentName + "~" + FACILITY_DEPENDENCY_INFIX;
        if (getProperty(prefix + _dependencyName) != null)
            return true;
        final ImmutableProperties<Object> immutableProperties = propertiesProcessor.getImmutableState();
        final ImmutableProperties<Object> subMap = immutableProperties.subMap(prefix);
        final Collection<String> keys = subMap.keySet();
        if (keys.size() == 0)
            return false;
        final Iterator<String> it = keys.iterator();
        while (it.hasNext()) {
            final String key = it.next();
            String nm = key.substring(prefix.length());
            if (hasDependency(nm, _dependencyName))
                return true;
        }
        return false;
    }

    public AsyncRequest<Void> initialLocalMerssageQueueSizePropertyAReq(final String _facilityName, final Integer _value) {
        return propertiesProcessor.putAReq(initialLocalMessageQueueSizeKey(_facilityName), _value);
    }

    public AsyncRequest<Void> initialBufferSizePropertyAReq(final String _facilityName, final Integer _value) {
        return propertiesProcessor.putAReq(initialBufferSizeKey(_facilityName), _value);
    }

    public AsyncRequest<Void> activatorPropertyAReq(final String _facilityName, final String _className) {
        return propertiesProcessor.putAReq(activatorKey(_facilityName), _className);
    }

    public String getActivatorClassName(final String _facilityName) {
        return (String) getProperty(activatorKey(_facilityName));
    }

    public FacilityImpl getFacilityImpl(String name) {
        Facility facility = ((Facility) getProperty(FACILITY_PROPERTY_PREFIX + name));
        if (facility == null)
            return null;
        return facility.asFacilityImpl();
    }

    public AsyncRequest<Void> autoStartAReq(final String _facilityName, final boolean _newValue) {
        return propertiesProcessor.putAReq(autoStartKey(_facilityName), _newValue ? true : null);
    }

    public boolean isAutoStart(String name) {
        return (Boolean) getProperty(autoStartKey(name)) != null;
    }

    public AsyncRequest<Void> failedAReq(final String _facilityName, final Object _newValue) {
        return propertiesProcessor.putAReq(failedKey(_facilityName), _newValue);
    }

    public Object getFailed(String name) {
        return getProperty(failedKey(name));
    }

    public AsyncRequest<Void> stoppedAReq(final String _facilityName, final boolean _newValue) {
        return propertiesProcessor.putAReq(stoppedKey(_facilityName), _newValue ? true : null);
    }

    public boolean isStopped(String name) {
        return (Boolean) getProperty(stoppedKey(name)) != null;
    }

    public AsyncRequest<Void> purgeFacilitySReq(final String _facilityName) {
        return new AsyncRequest<Void>(getInternalReactor()) {
            AsyncResponseProcessor<Void> dis = this;

            @Override
            public void processAsyncRequest() throws Exception {
                FacilityImpl facility = getFacilityImpl(_facilityName);
                if (facility != null)
                    facility.close();
                send(new PropertiesTransactionAReq(propertiesProcessor.parentReactor,
                        propertiesProcessor) {
                    @Override
                    protected void update(final PropertiesChangeManager _contentManager)
                            throws Exception {
                        ImmutableProperties<Object> immutableProperties =
                                _contentManager.getImmutableProperties();
                        String prefix = FACILITY_PREFIX + _facilityName + ".";
                        final ImmutableProperties<Object> subMap = immutableProperties.subMap(prefix);
                        final Collection<String> keys = subMap.keySet();
                        final Iterator<String> it = keys.iterator();
                        while (it.hasNext()) {
                            final String key = it.next();
                            _contentManager.put(key, null);
                        }
                    }
                }, dis);
            }
        };
    }
}
