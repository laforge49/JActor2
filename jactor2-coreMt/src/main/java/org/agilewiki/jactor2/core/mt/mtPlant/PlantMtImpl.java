package org.agilewiki.jactor2.core.mt.mtPlant;

import org.agilewiki.jactor2.core.plant.*;
import org.agilewiki.jactor2.core.reactors.PoolThreadReactorImpl;
import org.agilewiki.jactor2.core.reactors.ReactorImpl;
import org.agilewiki.jactor2.core.requests.AsyncRequestImpl;
import org.agilewiki.jactor2.core.requests.RequestImpl;
import org.agilewiki.jactor2.core.mt.mtReactors.*;
import org.agilewiki.jactor2.core.mt.mtRequests.AsyncRequestMtImpl;
import org.agilewiki.jactor2.core.mt.mtRequests.SyncRequestMtImpl;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.SyncRequest;

public class PlantMtImpl extends PlantImpl {

    /**
     * Returns this singleton.
     *
     * @return This singleton.
     */
    public static PlantMtImpl getSingleton() {
        return (PlantMtImpl) PlantImpl.getSingleton();
    }

    /**
     * System property flag, jactor.debug, to turn on debug;
     */
    public static final boolean DEBUG = "true".equals(System
            .getProperty("jactor.debug"));

    private PlantConfiguration plantConfiguration;

    private NonBlockingReactor internalReactor;

    private ReactorPoolThreadManager reactorPoolThreadManager;

    /**
     * Create the singleton with a default configuration.
     */
    public PlantMtImpl() {
        this(new PlantConfiguration());
    }

    /**
     * Create the singleton with the given thread pool size.
     *
     * @param _threadCount The size of the thread pool.
     */
    public PlantMtImpl(final int _threadCount) {
        this(new PlantConfiguration(_threadCount));
    }

    /**
     * Create the singleton with the given configuration.
     *
     * @param _plantConfiguration The configuration to be used by the singleton.
     */
    public PlantMtImpl(final PlantConfiguration _plantConfiguration) {
        super(_plantConfiguration);
        removeThreadBoundReactor();
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
                throw new IllegalArgumentException("unable to load class " + configurationClassName, e);
            }
            try {
                plantConfiguration = (PlantConfiguration) configurationClass.newInstance();
            } catch (InstantiationException e) {
                throw new IllegalArgumentException("unable to instantiate " + configurationClassName, e);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("unable to instantiate " + configurationClassName, e);
            }
        } else
            plantConfiguration = _plantConfiguration;
        reactorPoolThreadManager = plantConfiguration.createReactorPoolThreadManager();
        long reactorPollMillis = _plantConfiguration.getRecovery().getReactorPollMillis();
        internalReactor = createInternalReactor();
        _plantConfiguration.getPlantScheduler().scheduleAtFixedRate(plantPoll(),
                reactorPollMillis);
    }

    public void removeThreadBoundReactor() {
        ThreadBoundReactorMtImpl.removeReactor();
    }

    public ReactorImpl getCurrentReactorImpl() {
        Thread thread = Thread.currentThread();
        if (thread instanceof ReactorPoolThread)
            return ((ReactorPoolThread) thread).getCurrentReactorImpl();
        return ThreadBoundReactorMtImpl.threadReactor();
    }

    public void validateCall() {
        if (Thread.currentThread() instanceof ReactorPoolThread) {
            throw new UnsupportedOperationException(
                    "Use of call on a ReactorPoolThread can result in a deadlock");
        } else if (ThreadBoundReactorMtImpl.threadReactor() != null)
            throw new UnsupportedOperationException(
                    "Use of call on a Thread bound to a reactor can result in a deadlock " + ThreadBoundReactorMtImpl.threadReactor());

    }

    public ReactorImpl createNonBlockingReactorImpl(final NonBlockingReactor _parentReactor,
                                                    final int _initialOutboxSize, final int _initialLocalQueueSize) {
        return new NonBlockingReactorMtImpl(_parentReactor, _initialOutboxSize, _initialLocalQueueSize);
    }

    public ReactorImpl createBlockingReactorImpl(final NonBlockingReactor _parentReactor,
                                                 final int _initialOutboxSize, final int _initialLocalQueueSize) {
        return new BlockingReactorMtImpl(_parentReactor, _initialOutboxSize, _initialLocalQueueSize);
    }

    public ReactorImpl createIsolationReactorImpl(final NonBlockingReactor _parentReactor,
                                                  final int _initialOutboxSize, final int _initialLocalQueueSize) {
        return new IsolationReactorMtImpl(_parentReactor, _initialOutboxSize, _initialLocalQueueSize);
    }

    public ReactorImpl createSwingBoundReactorImpl(final NonBlockingReactor _parentReactor,
                                                   final int _initialOutboxSize, final int _initialLocalQueueSize) {
        return new SwingBoundReactorMtImpl(_parentReactor, _initialOutboxSize, _initialLocalQueueSize);
    }

    public ReactorImpl createThreadBoundReactorImpl(final NonBlockingReactor _parentReactor,
                                                    final int _initialOutboxSize, final int _initialLocalQueueSize,
                                                    final Runnable _boundProcessor) {
        return new ThreadBoundReactorMtImpl(_parentReactor, _initialOutboxSize, _initialLocalQueueSize, _boundProcessor);
    }

    @Override
    public <RESPONSE_TYPE> RequestImpl<RESPONSE_TYPE> createSyncRequestImpl(SyncRequest<RESPONSE_TYPE> _syncRequest, Reactor _targetReactor) {
        return new SyncRequestMtImpl<RESPONSE_TYPE>(_syncRequest, _targetReactor);
    }

    @Override
    public <RESPONSE_TYPE> AsyncRequestImpl<RESPONSE_TYPE> createAsyncRequestImpl(AsyncRequest<RESPONSE_TYPE> _asyncRequest, Reactor _targetReactor) {
        return new AsyncRequestMtImpl<RESPONSE_TYPE>(_asyncRequest, _targetReactor);
    }

    /**
     * Close the Plant.
     */
    public void close() throws Exception {
        if (getSingleton() == null) {
            return;
        }
        try {
            getInternalReactor().close();
        } finally {
            getPlantScheduler().close();
            super.close();
            reactorPoolThreadManager.close();
        }
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
    public PlantScheduler getPlantScheduler() {
        return plantConfiguration.getPlantScheduler();
    }

    /**
     * Create a SchedulableSemaphore.
     *
     * @param _millisecondDelay Milliseconds until the semaphore times out.
     * @return A new SchedulableSemaphore.
     */
    public SchedulableSemaphore schedulableSemaphore(final long _millisecondDelay) {
        SchedulableSemaphore schedulableSemaphore = new SchedulableSemaphore();
        plantConfiguration.getPlantScheduler().schedule(schedulableSemaphore.runnable, _millisecondDelay);
        return schedulableSemaphore;
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
     * Returns the Runnable which polls for timed out messages.
     *
     * @return The Runnable which will perform the poll.
     */
    private Runnable plantPoll() {
        return new Runnable() {
            public void run() {
                try {
                    getInternalReactor().asReactorImpl().reactorPoll();
                } catch (Exception x) {
                    x.printStackTrace();
                }
            }
        };
    }

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
     * Returns the Plant's internal reactor.
     *
     * @return The reactor belonging to the singleton.
     */
    public NonBlockingReactor getInternalReactor() {
        return internalReactor;
    }

    /**
     * Returns 16.
     *
     * @return The reactor default initial local message queue size.
     */
    public int getInitialLocalMessageQueueSize() {
        return plantConfiguration.getInitialLocalMessageQueueSize();
    }

    /**
     * Returns 16.
     *
     * @return The reactor default initial buffer size.
     */
    public int getInitialBufferSize() {
        return plantConfiguration.getInitialBufferSize();
    }
}
