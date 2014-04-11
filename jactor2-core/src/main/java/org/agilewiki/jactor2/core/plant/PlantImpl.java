package org.agilewiki.jactor2.core.plant;

import org.agilewiki.jactor2.core.reactors.PoolThreadReactorImpl;
import org.agilewiki.jactor2.core.reactors.ReactorImpl;
import org.agilewiki.jactor2.core.requests.AsyncRequestImpl;
import org.agilewiki.jactor2.core.requests.RequestImpl;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.SyncRequest;

/**
 * Internal implementation for Plant.
 */
abstract public class PlantImpl {

    /**
     * System property flag, jactor.debug, to turn on debug;
     */
    public static final boolean DEBUG = "true".equals(System
            .getProperty("jactor.debug"));

    private static volatile PlantImpl singleton;

    /**
     * Returns this singleton.
     *
     * @return This singleton.
     */
    public static PlantImpl getSingleton() {
        return singleton;
    }

    private PlantConfiguration plantConfiguration;

    private boolean exitOnClose;

    private ReactorPoolThreadManager reactorPoolThreadManager;

    private NonBlockingReactor internalReactor;

    /**
     * Create the singleton with the given configuration.
     *
     * @param _plantConfiguration The configuration to be used by the singleton.
     */
    public PlantImpl(final PlantConfiguration _plantConfiguration) {
        if (singleton != null) {
            throw new IllegalStateException("the singleton already exists");
        }
        removeThreadBoundReactor();
        singleton = this;
        if (DEBUG) {
            System.out.println("\n*** jactor.debug = true ***\n");
        }
        String configurationClassName = System.getProperty("jactor.configurationClass");
        if (configurationClassName != null) {
            ClassLoader classLoader = getClass().getClassLoader();
            Class configurationClass = null;
            try {
                configurationClass = classLoader.loadClass(configurationClassName);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("unable to load class "+configurationClassName, e);
            }
            try {
                plantConfiguration = (PlantConfiguration) configurationClass.newInstance();
            } catch (InstantiationException e) {
                throw new IllegalArgumentException("unable to instantiate "+configurationClassName, e);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("unable to instantiate "+configurationClassName, e);
            }
        } else
            plantConfiguration = _plantConfiguration;
        reactorPoolThreadManager = plantConfiguration.createReactorPoolThreadManager();
        long reactorPollMillis = _plantConfiguration.getRecovery().getReactorPollMillis();
        internalReactor = createInternalReactor();
        _plantConfiguration.getPlantScheduler().scheduleAtFixedRate(plantPoll(),
                reactorPollMillis);
    }

    /**
     * Create the Plant's internal reactor.
     *
     * @return The reactor belonging to the singleton.
     */
    protected NonBlockingReactor createInternalReactor() {
        return new NonBlockingReactor(null, plantConfiguration.getInitialBufferSize(),
                plantConfiguration.getInitialLocalMessageQueueSize());
    }

    /**
     * Returns the Plant's internal reactor.
     *
     * @return The reactor belonging to the singleton.
     */
    public NonBlockingReactor getInternalReactor() {
        return internalReactor;
    }

    /**
     * Returns the Runnable which polls for timed out messages.
     *
     * @return The Runnable which will perform the poll.
     */
    private Runnable plantPoll() {
        return new Runnable() {
            public void run() {
                try {
                    internalReactor.asReactorImpl().reactorPoll();
                } catch (Exception x) {
                    x.printStackTrace();
                }
            }
        };
    }

    /**
     * Returns the Plant's configuration.
     *
     * @return The singleton's configuration.
     */
    public PlantConfiguration getPlantConfiguration() {
        return plantConfiguration;
    }

    /**
     * Return the scheduler that is a part of the Plant's configuration.
     *
     * @return The scheduler.
     */
    public PlantScheduler getPlantScheduler() { return plantConfiguration.getPlantScheduler(); }

    /**
     * Submit a Reactor for subsequent execution.
     *
     * @param _reactor The targetReactor to be run.
     */
    public final void submit(final PoolThreadReactorImpl _reactor) {
        try {
            reactorPoolThreadManager.execute(_reactor);
        } catch (final Exception e) {
            if (!internalReactor.asReactorImpl().isClosing()) {
                throw e;
            }
        } catch (final Error e) {
            if (!internalReactor.asReactorImpl().isClosing()) {
                throw e;
            }
        }
    }

    /**
     * Close the Plant.
     */
    public void close() throws Exception {
        if (singleton == null) {
            return;
        }
        try {
            internalReactor.close();
        } finally {
            singleton = null;
            getPlantScheduler().close();
            if (exitOnClose)
                System.exit(0);
            reactorPoolThreadManager.close();
        }
    }

    /**
     * Create a SchedulableSemaphore.
     * @param _millisecondDelay Milliseconds until the semaphore times out.
     * @return A new SchedulableSemaphore.
     */
    public SchedulableSemaphore schedulableSemaphore(final long _millisecondDelay) {
        SchedulableSemaphore schedulableSemaphore = new SchedulableSemaphore();
        plantConfiguration.getPlantScheduler().schedule(schedulableSemaphore.runnable, _millisecondDelay);
        return schedulableSemaphore;
    }

    abstract public void removeThreadBoundReactor();

    abstract public ReactorImpl getCurrentReactorImpl();

    abstract public void validateCall();

    abstract public ReactorImpl createNonBlockingReactorImpl(final NonBlockingReactor _parentReactor,
                                                          int _initialOutboxSize, int _initialLocalQueueSize);

    abstract public ReactorImpl createBlockingReactorImpl(final NonBlockingReactor _parentReactor,
                                                          int _initialOutboxSize, int _initialLocalQueueSize);

    abstract public ReactorImpl createIsolationReactorImpl(final NonBlockingReactor _parentReactor,
                                                          int _initialOutboxSize, int _initialLocalQueueSize);

    abstract public ReactorImpl createSwingBoundReactorImpl(final NonBlockingReactor _parentReactor,
                                                            int _initialOutboxSize, int _initialLocalQueueSize);

    abstract public ReactorImpl createThreadBoundReactorImpl(final NonBlockingReactor _parentReactor,
                                                            int _initialOutboxSize, int _initialLocalQueueSize,
                                                            Runnable _boundProcessor);

    abstract public <RESPONSE_TYPE> RequestImpl<RESPONSE_TYPE> createSyncRequestImpl(
            SyncRequest<RESPONSE_TYPE> _syncRequest, Reactor _targetReactor);

    abstract public <RESPONSE_TYPE> AsyncRequestImpl<RESPONSE_TYPE> createAsyncRequestImpl(
            AsyncRequest<RESPONSE_TYPE> _asyncRequest, Reactor _targetReactor);
}
