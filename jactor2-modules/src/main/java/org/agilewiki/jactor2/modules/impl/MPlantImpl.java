package org.agilewiki.jactor2.modules.impl;

import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.blades.ExceptionHandler;
import org.agilewiki.jactor2.core.impl.NonBlockingReactorImpl;
import org.agilewiki.jactor2.core.impl.PlantImpl;
import org.agilewiki.jactor2.core.impl.SchedulableSemaphore;
import org.agilewiki.jactor2.core.impl.UnboundReactorImpl;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.plant.PlantConfiguration;
import org.agilewiki.jactor2.core.plant.ServiceClosedException;
import org.agilewiki.jactor2.core.plant.ThreadManager;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.util.Closeable;
import org.agilewiki.jactor2.core.util.Recovery;
import org.agilewiki.jactor2.core.util.immutable.ImmutableProperties;
import org.agilewiki.jactor2.modules.Facility;
import org.agilewiki.jactor2.modules.pubSub.RequestBus;
import org.agilewiki.jactor2.modules.pubSub.SubscribeAReq;
import org.agilewiki.jactor2.modules.transactions.properties.ImmutablePropertyChanges;
import org.agilewiki.jactor2.modules.transactions.properties.PropertiesChangeManager;
import org.agilewiki.jactor2.modules.transactions.properties.PropertiesTransactionAReq;
import org.agilewiki.jactor2.modules.transactions.properties.PropertyChange;

import java.util.Collection;
import java.util.Iterator;
import java.util.SortedMap;

public class MPlantImpl extends PlantImpl {

    public static MPlantImpl getSingleton() {
        return (MPlantImpl) PlantImpl.getSingleton();
    }

    public MPlantImpl() throws Exception {
        this(new PlantConfiguration());
    }

    public MPlantImpl(final int _threadCount) throws Exception {
        this(new PlantConfiguration(_threadCount));
    }

    public MPlantImpl(final PlantConfiguration _plantConfiguration) throws Exception {
        super(_plantConfiguration);
        RequestBus<ImmutablePropertyChanges> changeBus = propertiesProcessor.changeBus;
        new SubscribeAReq<ImmutablePropertyChanges>(
                changeBus,
                (NonBlockingReactor) internalReactor) {
            protected void processContent(final ImmutablePropertyChanges _content)
                    throws Exception {
                SortedMap<String, PropertyChange> readOnlyChanges = _content.readOnlyChanges;
                final Iterator<PropertyChange> it = readOnlyChanges.values().iterator();
                while (it.hasNext()) {
                    PropertyChange pc = it.next();
                    String key = pc.name;
                    Object newValue = pc.newValue;
                    if (key.startsWith(FACILITY_PROPERTY_PREFIX) && newValue != null) {
                        String facilityName = ((FacilityImpl) newValue).name;
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
        long reactorPollMillis = _plantConfiguration.getRecovery().getReactorPollMillis();
        _plantConfiguration.getScheduler().scheduleAtFixedRate(plantPoll(),
                reactorPollMillis);
    }

    public Facility getInternalFacility() {
        return (Facility) getReactor();
    }

    protected NonBlockingReactor createInternalReactor() throws Exception {
        PlantConfiguration plantConfiguration = getPlantConfiguration();
        return new NonBlockingReactor(null, plantConfiguration.getInitialBufferSize(),
                plantConfiguration.getInitialLocalMessageQueueSize(),
                plantConfiguration.getRecovery(), plantConfiguration.getScheduler());
    }

    private Runnable plantPoll() {
        return new Runnable() {
            public void run() {
                try {
                    Iterator<Closeable> it = getCloseableSet().iterator();
                    while (it.hasNext()) {
                        Closeable closeable = it.next();
                        if (!(closeable instanceof Facility))
                            continue;
                        FacilityImpl facility = (FacilityImpl) closeable;
                        facility.facilityPoll();
                    }
                    facilityPoll();
                } catch (Exception x) {
                    x.printStackTrace();
                }
            }
        };
    }

    public PlantConfiguration getPlantConfiguration() {
        return plantConfiguration;
    }

    /**
     * Submit a Reactor for subsequent execution.
     *
     * @param _reactor The targetReactor to be run.
     */
    public final void submit(final UnboundReactorImpl _reactor) throws Exception {
        try {
            threadManager.execute(_reactor);
        } catch (final Exception e) {
            if (!shuttingDown) {
                throw e;
            }
        } catch (final Error e) {
            if (!shuttingDown) {
                throw e;
            }
        }
    }

    @Override
    protected void validateName(final String _name) throws Exception {
    }

    private AsyncRequest<String> autoStartAReq(final String _facilityName) {
        return new BladeBase.AsyncBladeRequest<String>() {
            @Override
            public void processAsyncRequest() throws Exception {
                if (getFacility(_facilityName) != null) {
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
                        getPropertiesProcessor().getImmutableState().subMap(dependencyPrefix);
                Iterator<String> dit = dependencies.keySet().iterator();
                while (dit.hasNext()) {
                    String d = dit.next();
                    String dependencyName = d.substring(dependencyPrefix.length());
                    Facility dependency = getFacility(dependencyName);
                    if (dependency == null)
                        processAsyncResponse(null);
                }
                setExceptionHandler(new ExceptionHandler<String>() {
                    @Override
                    public String processException(Exception e) throws Exception {
                        if (e instanceof ServiceClosedException)
                            return null;
                        else
                            return "create facility exception: " + e;
                    }
                });
                send(createFacilityAReq(_facilityName), this, null);
            }
        };
    }

    public AsyncRequest<Facility> createFacilityAReq(final String _name)
            throws Exception {
        return new BladeBase.AsyncBladeRequest<Facility>() {
            final AsyncResponseProcessor<Facility> dis = this;

            @Override
            public void processAsyncRequest() throws Exception {
                if (getFacility(_name) != null)
                    processAsyncResponse(getFacility(_name));
                final FacilityImpl facility = new FacilityImpl(_name);

                facility.recovery = (Recovery) getProperty(recoveryKey(_name));
                if (facility.recovery == null)
                    facility.recovery = recovery;
                facility.scheduler = scheduler;

                Integer v = (Integer) getProperty(initialLocalMessageQueueSizeKey(_name));
                if (v == null)
                    facility.initialLocalMessageQueueSize = initialLocalMessageQueueSize;
                else
                    facility.initialLocalMessageQueueSize = v;

                v = (Integer) getProperty(initialBufferSizeKey(_name));
                if (v == null)
                    facility.initialBufferSize = initialBufferSize;
                else
                    facility.initialBufferSize = v;

                facility.initialize();
                send(new PropertiesTransactionAReq(getPropertiesProcessor().parentReactor, getPropertiesProcessor()) {
                         @Override
                         protected void update(final PropertiesChangeManager _changeManager) throws Exception {
                             _changeManager.put(FACILITY_PROPERTY_PREFIX + _name, facility);
                             _changeManager.put(failedKey(_name), null);
                             _changeManager.put(stoppedKey(_name), null);
                         }
                     }, new AsyncResponseProcessor<Void>() {
                         @Override
                         public void processAsyncResponse(
                                 final Void _response) throws Exception {
                             getCloseableSet().add(facility);
                             String activatorClassName = getActivatorClassName(_name);
                             if (activatorClassName == null)
                                 dis.processAsyncResponse(facility);
                             else {
                                 send(facility.activateAReq(activatorClassName), new AsyncResponseProcessor<String>() {
                                     @Override
                                     public void processAsyncResponse(final String _failure) throws Exception {
                                         if (_failure == null) {
                                             dis.processAsyncResponse(facility);
                                             return;
                                         }
                                         send(new PropertiesTransactionAReq(
                                                      getPropertiesProcessor().parentReactor,
                                                      getPropertiesProcessor()) {
                                                  @Override
                                                  protected void update(final PropertiesChangeManager _changeManager)
                                                          throws Exception {
                                                      _changeManager.put(FACILITY_PROPERTY_PREFIX + _name, null);
                                                      _changeManager.put(failedKey(_name), _failure);
                                                  }
                                              }, new AsyncResponseProcessor<Void>() {
                                                  @Override
                                                  public void processAsyncResponse(Void _response) throws Exception {
                                                      throw new ServiceClosedException();
                                                  }
                                              }
                                         );
                                     }
                                 });
                             }
                         }
                     }
                );
            }
        };
    }

    @Override
    public void close() throws Exception {
        if (singleton == null)
            return;
        singleton = null;
        super.close();
        plantConfiguration.close();
    }

    @Override
    public void stop() {
        throw new UnsupportedOperationException();
    }

    public void stopFacility(final String _facilityName) throws Exception {
        FacilityImpl facility = getFacility(_facilityName);
        if (facility == null) {
            putPropertyAReq(stoppedKey(name), true).signal();
            return;
        }
        facility.stop();
    }

    public void failFacility(final String _facilityName, final Object reason) throws Exception {
        FacilityImpl facility = getFacility(_facilityName);
        if (facility == null) {
            putPropertyAReq(failedKey(name), reason).signal();
            return;
        }
        facility.fail(reason);
    }

    @Override
    protected void close2() throws Exception {
        if (shuttingDown) {
            return;
        }
        shuttingDown = true;
        if (exitOnClose)
            System.exit(0);
        threadManager.close();
    }

    public void exit() {
        exitOnClose = true;
        try {
            close();
        } catch (Throwable t) {
            getLogger().error("exception on exit", t);
            System.exit(1);
        }
    }

    public SchedulableSemaphore schedulableSemaphore(final long _millisecondDelay) {
        SchedulableSemaphore schedulableSemaphore = new SchedulableSemaphore();
        plantConfiguration.getScheduler().schedule(schedulableSemaphore.runnable, _millisecondDelay);
        return schedulableSemaphore;
    }

    public AsyncRequest<Void> dependencyPropertyAReq(final String _dependentName, final String _dependencyName) {
        return new BladeBase.AsyncBladeRequest<Void>() {

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
            Facility dependency = getFacility(nm);
            if (hasDependency(nm, _dependencyName))
                return true;
        }
        return false;
    }

    public AsyncRequest<Void> recoveryPropertyAReq(final String _facilityName, final Recovery _recovery) {
        return propertiesProcessor.putAReq(recoveryKey(_facilityName), _recovery);
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
        return (FacilityImpl) getProperty(FACILITY_PROPERTY_PREFIX + name);
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
        return new BladeBase.AsyncBladeRequest<Void>() {
            AsyncResponseProcessor<Void> dis = this;

            @Override
            public void processAsyncRequest() throws Exception {
                FacilityImpl facility = getFacility(_facilityName);
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
