package org.agilewiki.jactor2.modules.impl;

import org.agilewiki.jactor2.core.blades.ExceptionHandler;
import org.agilewiki.jactor2.core.impl.*;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.plant.Scheduler;
import org.agilewiki.jactor2.core.plant.ServiceClosedException;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.util.Closeable;
import org.agilewiki.jactor2.core.util.Recovery;
import org.agilewiki.jactor2.core.util.immutable.ImmutableProperties;
import org.agilewiki.jactor2.modules.Activator;
import org.agilewiki.jactor2.modules.Facility;
import org.agilewiki.jactor2.modules.MPlant;
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

    protected PropertiesProcessor propertiesProcessor;

    private String name;

    private MPlantImpl plantImpl;

    private FacilityImpl plantFacilityImpl;

    public FacilityImpl(final String _name,
                        final int _initialOutboxSize, final int _initialLocalQueueSize) throws Exception {
        super(PlantImpl.getSingleton().getReactor() == null ? null : PlantImpl.getSingleton().getReactor().asReactorImpl(),
                _initialOutboxSize, _initialLocalQueueSize);
        if (name != null)
            throw new IllegalStateException("name already set");
        validateName(_name);
        name = _name;
        plantImpl = MPlantImpl.getSingleton();
        plantFacilityImpl = plantImpl.getInternalFacility().asFacilityImpl();
        final TreeMap<String, Object> initialState = new TreeMap<String, Object>();
        propertiesProcessor = new PropertiesProcessor(this.getFacility(), initialState);
        String dependencyPrefix = MPlantImpl.dependencyPrefix(name);
        PropertiesProcessor plantProperties = plantFacilityImpl.getPropertiesProcessor();
        ImmutableProperties<Object> dependencies =
                plantProperties.getImmutableState().subMap(dependencyPrefix);
        Iterator<String> dit = dependencies.keySet().iterator();
        while (dit.hasNext()) {
            String d = dit.next();
            String dependencyName = d.substring(dependencyPrefix.length());
            FacilityImpl dependency = plantImpl.getFacilityImpl(dependencyName);
            if (dependency == null)
                throw new IllegalStateException("dependency not present: "+dependencyName);
            dependency.addCloseable(this);
        }
        Integer v = (Integer) plantImpl.getProperty(MPlantImpl.initialLocalMessageQueueSizeKey(_name));
        if (v != null)
            initialLocalQueueSize = v;
        v = (Integer) plantImpl.getProperty(MPlantImpl.initialBufferSizeKey(_name));
        if (v != null)
            initialBufferSize = v;
        tracePropertyChangesAReq().signal();
        registerFacilityAReq().signal();
    }

    private AsyncRequest<Void> registerFacilityAReq() {
        return new PropertiesTransactionAReq(asReactor(), propertiesProcessor) {
            @Override
            protected void update(final PropertiesChangeManager _changeManager) throws Exception {
                _changeManager.put(MPlantImpl.FACILITY_PROPERTY_PREFIX + name, this);
                _changeManager.put(MPlantImpl.failedKey(name), null);
                _changeManager.put(MPlantImpl.stoppedKey(name), null);
            }
        };
    }

    public Facility asFacility() {
        return (Facility) asReactor();
    }

    public Facility getFacility() {
        return (Facility) getReactor();
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
        if (_name.equals(MPlantImpl.PLANT_NAME) && getParentReactor() != null) {
            throw new IllegalArgumentException("name may not be " + MPlantImpl.PLANT_NAME);
        } else if (MPlant.getFacility(_name) != null) {
            throw new IllegalStateException("facility by that name already exists");
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
            plantImpl.getInternalFacility().putPropertyAReq(MPlantImpl.FACILITY_PROPERTY_PREFIX + name,
                    null).signal();
        }
        super.close();
    }

    public void stop() throws Exception {
        if (startedClosing()) {
            plantImpl.getInternalFacility().putPropertyAReq(MPlantImpl.stoppedKey(name), true,
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
                    _changeManager.put(MPlantImpl.FACILITY_PROPERTY_PREFIX + name, null);
                    _changeManager.put(MPlantImpl.stoppedKey(name), true);
                }}.signal();
            plantImpl.getInternalFacility().putPropertyAReq(MPlantImpl.FACILITY_PROPERTY_PREFIX + name,
                    null).signal();
        }
        super.close();
    }

    public void fail(final Object reason) throws Exception {
        if (startedClosing()) {
            plantImpl.getInternalFacility().putPropertyAReq(MPlantImpl.failedKey(name), reason,
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
                    _changeManager.put(MPlantImpl.FACILITY_PROPERTY_PREFIX + name, null);
                    _changeManager.put(MPlantImpl.failedKey(name), reason);
                }}.signal();
            plantImpl.getInternalFacility().putPropertyAReq(MPlantImpl.FACILITY_PROPERTY_PREFIX + name,
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
