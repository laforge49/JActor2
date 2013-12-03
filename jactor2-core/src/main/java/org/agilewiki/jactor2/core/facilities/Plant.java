package org.agilewiki.jactor2.core.facilities;

import org.agilewiki.jactor2.core.blades.transactions.properties.PropertiesChangeManager;
import org.agilewiki.jactor2.core.blades.transactions.properties.PropertiesTransactionAReq;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.util.Recovery;
import org.agilewiki.jactor2.core.util.immutable.ImmutableProperties;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Plant extends Facility {

    /**
     * System property flag, jactor.debug, to turn on debug;
     */
    public static final boolean DEBUG = "true".equals(System
            .getProperty("jactor.debug"));

    private static volatile Plant singleton;

    public static Plant getSingleton() {
        if (singleton == null) {
            throw new IllegalStateException("there is no singleton");
        }
        return singleton;
    }

    private PlantConfiguration plantConfiguration;

    private ScheduledThreadPoolExecutor semaphoreScheduler = new ScheduledThreadPoolExecutor(1);

    private boolean exitOnClose;

    private boolean forceExit;

    /**
     * The thread pool.
     */
    private final ThreadManager threadManager;

    /**
     * Create a Plant.
     */
    public Plant() throws Exception {
        this(new PlantConfiguration());
    }

    /**
     * Create a Plant.
     *
     * @param _threadCount The thread pool size.
     */
    public Plant(final int _threadCount) throws Exception {
        this(new PlantConfiguration(_threadCount));
    }

    public Plant(final PlantConfiguration _plantConfiguration) throws Exception {
        super(PLANT_NAME);
        if (singleton != null) {
            throw new IllegalStateException("the singleton already exists");
        }
        singleton = this;
        if (DEBUG) {
            System.out.println("\n*** jactor.debug = true ***\n");
        }
        String configurationClassName = System.getProperty("jactor.configurationClass");
        if (configurationClassName != null) {
            ClassLoader classLoader = getClass().getClassLoader();
            Class configurationClass = classLoader.loadClass(configurationClassName);
            plantConfiguration = (PlantConfiguration) configurationClass.newInstance();
        } else
            plantConfiguration = _plantConfiguration;
        recovery = plantConfiguration.getRecovery();
        initialLocalMessageQueueSize = plantConfiguration.getInitialLocalMessageQueueSize();
        initialBufferSize = plantConfiguration.getInitialBufferSize();
        threadManager = plantConfiguration.getThreadManager();
        initialize(this);
    }

    public PlantConfiguration getPlantConfiguration() {
        return plantConfiguration;
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

    public String autoStart(final String _facilityName) {
        if (!isAutoStart(_facilityName))
            return "autoStart not set";
        if (getFailed(_facilityName) != null)
            return "failed: " + getFailed(_facilityName);
        if (isStopped(_facilityName))
            return "stopped";
        String dependencyPrefix = dependencyPrefix(_facilityName);
        ImmutableProperties<Object> dependencies =
                plant.getPropertiesProcessor().getImmutableState().subMap(dependencyPrefix);
        Iterator<String> dit = dependencies.keySet().iterator();
        while (dit.hasNext()) {
            String d = dit.next();
            String dependencyName = d.substring(dependencyPrefix.length());
            Facility dependency = plant.getFacility(dependencyName);
            if (dependency == null)
                return "missing dependency: " + dependencyName;
        }
        try {
            createFacilityAReq(_facilityName).signal();
        } catch (Exception e) {
            return "create facility exception: " + e;
        }
        return null;
    }

    public AsyncRequest<Facility> createFacilityAReq(final String _name)
            throws Exception {
        return new AsyncBladeRequest<Facility>() {
            final AsyncResponseProcessor<Facility> dis = this;

            @Override
            protected void processAsyncRequest() throws Exception {
                final Facility facility = new Facility(_name);

                facility.recovery = (Recovery) getProperty(recoveryKey(_name));
                if (facility.recovery == null)
                    facility.recovery = recovery;

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

                facility.initialize(Plant.this);
                send(new PropertiesTransactionAReq(getPropertiesProcessor().commonReactor, getPropertiesProcessor()) {
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
                                                      getPropertiesProcessor().commonReactor,
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
    }

    @Override
    public void stop() {
        throw new UnsupportedOperationException();
    }

    public void stopFacility(final String _facilityName) throws Exception {
        Facility facility = getFacility(_facilityName);
        if (facility == null) {
            putPropertyAReq(FACILITY_PROPERTY_PREFIX + name, null).signal();
            return;
        }
        facility.stop();
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
            getLog().error("exception on exit", t);
            System.exit(1);
        }
    }

    public boolean isForcedExit() {
        return forceExit;
    }

    public void forceExit() {
        forceExit = true;
        exit();
    }

    public SchedulableSemaphore schedulableSemaphore(final long _millisecondDelay) {
        SchedulableSemaphore schedulableSemaphore = new SchedulableSemaphore();
        semaphoreScheduler.schedule(schedulableSemaphore.runnable, _millisecondDelay, TimeUnit.MILLISECONDS);
        return schedulableSemaphore;
    }

    public AsyncRequest<Void> dependencyPropertyAReq(final String _dependentName, final String _dependencyName) {
        return new AsyncBladeRequest<Void>() {

            AsyncResponseProcessor<Void> dis = this;

            String dependencyPropertyName;

            @Override
            protected void processAsyncRequest() throws Exception {
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
        if (plant.getProperty(prefix + _dependencyName) != null)
            return true;
        final ImmutableProperties<Object> immutableProperties = plant.propertiesProcessor.getImmutableState();
        final ImmutableProperties<Object> subMap = immutableProperties.subMap(prefix);
        final Collection<String> keys = subMap.keySet();
        if (keys.size() == 0)
            return false;
        final Iterator<String> it = keys.iterator();
        while (it.hasNext()) {
            final String key = it.next();
            String nm = key.substring(prefix.length());
            Facility dependency = plant.getFacility(nm);
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

    public Facility getFacility(String name) {
        return (Facility) getProperty(FACILITY_PROPERTY_PREFIX + name);
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
        return new AsyncBladeRequest<Void>() {
            AsyncResponseProcessor<Void> dis = this;

            @Override
            protected void processAsyncRequest() throws Exception {
                Facility facility = getFacility(_facilityName);
                if (facility != null)
                    facility.close();
                send(new PropertiesTransactionAReq(propertiesProcessor.commonReactor,
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
