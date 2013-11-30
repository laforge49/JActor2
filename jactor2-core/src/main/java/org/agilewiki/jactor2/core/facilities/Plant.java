package org.agilewiki.jactor2.core.facilities;

import org.agilewiki.jactor2.core.blades.transactions.properties.PropertiesChangeManager;
import org.agilewiki.jactor2.core.blades.transactions.properties.PropertiesTransactionAReq;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.reactors.Inbox;
import org.agilewiki.jactor2.core.reactors.Outbox;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.util.DefaultRecovery;
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
        String configurationClassName = System.getProperty("jactor.configurationClass");
        if (configurationClassName != null) {
            ClassLoader classLoader = getClass().getClassLoader();
            Class configurationClass = classLoader.loadClass(configurationClassName);
            plantConfiguration = (PlantConfiguration) configurationClass.newInstance();
        } else
            plantConfiguration = _plantConfiguration;
        String recoveryClassName = System.getProperty("jactor.recoveryClass");
        if (recoveryClassName != null) {
            ClassLoader classLoader = getClass().getClassLoader();
            Class recoveryClass = classLoader.loadClass(recoveryClassName);
            recovery = (Recovery) recoveryClass.newInstance();
        } else
            recovery = new DefaultRecovery();
        threadManager = plantConfiguration.getThreadManager();
        if (singleton != null) {
            throw new IllegalStateException("the singleton already exists");
        }
        singleton = this;
        if (DEBUG) {
            System.out.println("\n*** jactor.debug = true ***\n");
        }
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

    public AsyncRequest<Facility> createFacilityAReq(final String _name)
            throws Exception {
        return createFacilityAReq(
                _name,
                plantConfiguration.getInitialLocalMessageQueueSize(),
                plantConfiguration.getInitialBufferSize());
    }

    public AsyncRequest<Facility> createFacilityAReq(final String _name,
                                                     final int _initialLocalMessageQueueSize,
                                                     final int _initialBufferSize) throws Exception {
        return new AsyncBladeRequest<Facility>() {
            final AsyncResponseProcessor<Facility> dis = this;

            @Override
            protected void processAsyncRequest() throws Exception {
                final Facility facility = new Facility(_name);
                facility.recovery = (Recovery) getProperty(recoveryKey(_name));
                if (facility.recovery == null)
                    facility.recovery = recovery;
                facility.initialize(Plant.this, _initialLocalMessageQueueSize, _initialBufferSize);
                send(new PropertiesTransactionAReq(getPropertiesProcessor().commonReactor,getPropertiesProcessor()) {
                    protected void update(final PropertiesChangeManager _changeManager) throws Exception {
                        _changeManager.put(FACILITY_PROPERTY_PREFIX + _name, facility);
                        _changeManager.put(failedKey(_name), null);
                        _changeManager.put(stoppedKey(_name), null);
                    }},
                        new AsyncResponseProcessor<Void>() {
                            @Override
                            public void processAsyncResponse(
                                    final Void _response) throws Exception {
                                getCloseableSet().add(facility);
                                dis.processAsyncResponse(facility);
                            }
                        });
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
                dependencyPropertyName = dependencyPrefix(_dependentName)+name;
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
        String prefix = FACILITY_PREFIX+_dependentName+"~"+ FACILITY_DEPENDENCY_INFIX;
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

    public Facility getFacility(String name) {
        return (Facility) getProperty(FACILITY_PROPERTY_PREFIX+name);
    }

    public AsyncRequest<Void> autoStartAReq(final String _facilityName, final boolean _newValue) {
        return propertiesProcessor.putAReq(autoStartKey(_facilityName), _newValue ? true : null);
    }

    public boolean isAutoStart(String name) {
        return (Boolean) getProperty(autoStartKey(name)) != null;
    }

    public AsyncRequest<Void> failedAReq(final String _facilityName, final boolean _newValue) {
        return propertiesProcessor.putAReq(failedKey(_facilityName), _newValue ? true : null);
    }

    public boolean isFailed(String name) {
        return (Boolean) getProperty(failedKey(name)) != null;
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
                        String prefix = FACILITY_PREFIX+_facilityName+".";
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
