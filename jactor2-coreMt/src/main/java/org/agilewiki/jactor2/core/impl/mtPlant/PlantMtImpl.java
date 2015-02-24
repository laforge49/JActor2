package org.agilewiki.jactor2.core.impl.mtPlant;

import org.agilewiki.jactor2.core.impl.mtMessages.AsyncRequestMtImpl;
import org.agilewiki.jactor2.core.impl.mtMessages.AsyncRequestMtImplWithData;
import org.agilewiki.jactor2.core.impl.mtMessages.SyncRequestMtImpl;
import org.agilewiki.jactor2.core.impl.mtMessages.SyncRequestMtImplWithData;
import org.agilewiki.jactor2.core.impl.mtReactors.*;
import org.agilewiki.jactor2.core.messages.AsyncOperation;
import org.agilewiki.jactor2.core.messages.alt.AsyncRequestImplWithData;
import org.agilewiki.jactor2.core.messages.SyncOperation;
import org.agilewiki.jactor2.core.messages.impl.AsyncRequestImpl;
import org.agilewiki.jactor2.core.messages.impl.RequestImpl;
import org.agilewiki.jactor2.core.messages.impl.RequestImplWithData;
import org.agilewiki.jactor2.core.plant.impl.PlantImpl;
import org.agilewiki.jactor2.core.plant.impl.PlantScheduler;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.reactors.closeable.Closeable;
import org.agilewiki.jactor2.core.reactors.closeable.impl.CloseableImpl;
import org.agilewiki.jactor2.core.reactors.closeable.impl.CloseableImplImpl;
import org.agilewiki.jactor2.core.reactors.facilities.Facility;
import org.agilewiki.jactor2.core.reactors.impl.ReactorImpl;

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

    private final Facility internalFacility;

    private final ReactorPoolThreadManager reactorPoolThreadManager;

    /**
     * Create the singleton with a default configuration.
     */
    public PlantMtImpl() throws Exception {
        this(new PlantConfiguration());
    }

    /**
     * Create the singleton with the given thread pool size.
     *
     * @param _threadCount The size of the thread pool.
     */
    public PlantMtImpl(final int _threadCount) throws Exception {
        this(new PlantConfiguration(_threadCount));
    }

    /**
     * Create the singleton with the given configuration.
     *
     * @param _plantConfiguration The configuration to be used by the singleton.
     */
    public PlantMtImpl(final PlantConfiguration _plantConfiguration)
            throws Exception {
        removeThreadBoundReactor();
        if (DEBUG) {
            System.out.println("\n*** jactor.debug = true ***\n");
        }
        final String configurationClassName = System
                .getProperty("jactor.configurationClass");
        if (configurationClassName != null) {
            final ClassLoader classLoader = getClass().getClassLoader();
            Class<?> configurationClass = null;
            try {
                configurationClass = classLoader
                        .loadClass(configurationClassName);
            } catch (final ClassNotFoundException e) {
                throw new IllegalArgumentException("unable to load class "
                        + configurationClassName, e);
            }
            try {
                plantConfiguration = (PlantConfiguration) configurationClass
                        .newInstance();
            } catch (final InstantiationException e) {
                throw new IllegalArgumentException("unable to instantiate "
                        + configurationClassName, e);
            } catch (final IllegalAccessException e) {
                throw new IllegalArgumentException("unable to instantiate "
                        + configurationClassName, e);
            }
        } else {
            plantConfiguration = _plantConfiguration;
        }
        reactorPoolThreadManager = plantConfiguration
                .createReactorPoolThreadManager();
        final int reactorPollMillis = _plantConfiguration.getRecovery()
                .getReactorPollMillis();
        internalFacility = createInternalFacility();
        _plantConfiguration.getPlantScheduler().scheduleAtFixedRate(
                plantPoll(), reactorPollMillis);
    }

    public void removeThreadBoundReactor() {
        ThreadBoundReactorMtImpl.removeReactor();
    }

    @Override
    public ReactorImpl getCurrentReactorImpl() {
        final Thread thread = Thread.currentThread();
        if (thread instanceof ReactorPoolThread) {
            return ((ReactorPoolThread) thread).getCurrentReactorImpl();
        }
        return ThreadBoundReactorMtImpl.threadReactor();
    }

    public void validateCall() {
        if (Thread.currentThread() instanceof ReactorPoolThread) {
            throw new UnsupportedOperationException(
                    "Use of call on a ReactorPoolThread can result in a deadlock");
        } else if (ThreadBoundReactorMtImpl.threadReactor() != null) {
            throw new UnsupportedOperationException(
                    "Use of call on a Thread bound to a reactor can result in a deadlock "
                            + ThreadBoundReactorMtImpl.threadReactor());
        }

    }

    @Override
    public ReactorImpl createNonBlockingReactorImpl(
            final IsolationReactor _parentReactor,
            final int _initialOutboxSize, final int _initialLocalQueueSize) {
        return new NonBlockingReactorMtImpl(_parentReactor, _initialOutboxSize,
                _initialLocalQueueSize);
    }

    @Override
    public ReactorImpl createBlockingReactorImpl(
            final IsolationReactor _parentReactor,
            final int _initialOutboxSize, final int _initialLocalQueueSize) {
        return new BlockingReactorMtImpl(_parentReactor, _initialOutboxSize,
                _initialLocalQueueSize);
    }

    @Override
    public ReactorImpl createIsolationReactorImpl(
            final IsolationReactor _parentReactor,
            final int _initialOutboxSize, final int _initialLocalQueueSize) {
        return new IsolationReactorMtImpl(_parentReactor, _initialOutboxSize,
                _initialLocalQueueSize);
    }

    @Override
    public ReactorImpl createSwingBoundReactorImpl(
            final IsolationReactor _parentReactor,
            final int _initialOutboxSize, final int _initialLocalQueueSize) {
        return new SwingBoundReactorMtImpl(_parentReactor, _initialOutboxSize,
                _initialLocalQueueSize);
    }

    @Override
    public ReactorImpl createThreadBoundReactorImpl(
            final IsolationReactor _parentReactor,
            final int _initialOutboxSize, final int _initialLocalQueueSize,
            final Runnable _boundProcessor) {
        return new ThreadBoundReactorMtImpl(_parentReactor, _initialOutboxSize,
                _initialLocalQueueSize, _boundProcessor);
    }

    @Override
    public <RESPONSE_TYPE> RequestImpl<RESPONSE_TYPE> createSyncRequestImpl(
            final SyncOperation<RESPONSE_TYPE> _syncOperation,
            final Reactor _targetReactor) {
        return new SyncRequestMtImpl<RESPONSE_TYPE>(_syncOperation,
                _targetReactor);
    }

    @Override
    public <RESPONSE_TYPE> AsyncRequestImpl<RESPONSE_TYPE> createAsyncRequestImpl(
            final AsyncOperation<RESPONSE_TYPE> _asyncOperation,
            final Reactor _targetReactor) {
        return new AsyncRequestMtImpl<RESPONSE_TYPE>(_asyncOperation,
                _targetReactor);
    }

    @Override
    public <RESPONSE_TYPE> RequestImplWithData<RESPONSE_TYPE> createSyncRequestImplWithData(
            final SyncOperation<RESPONSE_TYPE> _syncOperation,
            final Reactor _targetReactor) {
        return new SyncRequestMtImplWithData<RESPONSE_TYPE>(_syncOperation,
                _targetReactor);
    }

    @Override
    public <RESPONSE_TYPE> AsyncRequestImplWithData<RESPONSE_TYPE> createAsyncRequestImplWithData(
            final AsyncOperation<RESPONSE_TYPE> _asyncOperation,
            final Reactor _targetReactor) {
        return new AsyncRequestMtImplWithData<RESPONSE_TYPE>(_asyncOperation,
                _targetReactor);
    }

    @Override
    public CloseableImpl createCloseableImpl(final Closeable _closeable) {
        return new CloseableImplImpl(_closeable);
    }

    /**
     * Close the Plant.
     */
    @Override
    public void close() throws Exception {
        if (getSingleton() == null) {
            return;
        }
        try {
            getInternalFacility().close();
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
    @Override
    public PlantScheduler getPlantScheduler() {
        return plantConfiguration.getPlantScheduler();
    }

    /**
     * Create a SchedulableSemaphore.
     *
     * @param _millisecondDelay Milliseconds until the semaphore times out.
     * @return A new SchedulableSemaphore.
     */
    public SchedulableSemaphore schedulableSemaphore(final int _millisecondDelay) {
        final SchedulableSemaphore schedulableSemaphore = new SchedulableSemaphore();
        plantConfiguration.getPlantScheduler().schedule(
                schedulableSemaphore.runnable, _millisecondDelay);
        return schedulableSemaphore;
    }

    /**
     * Create the Plant's internal reactor.
     *
     * @return The reactor belonging to the singleton.
     */
    protected Facility createInternalFacility() throws Exception {
        return new Facility(PLANT_INTERNAL_FACILITY_NAME, null,
                plantConfiguration.getInitialBufferSize(),
                plantConfiguration.getInitialLocalMessageQueueSize());
    }

    /**
     * Returns the Runnable which polls for timed out messages.
     *
     * @return The Runnable which will perform the poll.
     */
    private Runnable plantPoll() {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    ((ReactorMtImpl) getInternalFacility().asReactorImpl())
                            .reactorPoll();
                } catch (final Exception x) {
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
    public final void submit(final PoolThreadReactorMtImpl _reactor) {
        final ReactorMtImpl internalFacilityImpl = (ReactorMtImpl) internalFacility
                .asReactorImpl();
        try {
            reactorPoolThreadManager.execute(_reactor);
        } catch (final Exception e) {
            if (!internalFacilityImpl.isClosing()) {
                throw e;
            }
        } catch (final Error e) {
            if (!internalFacilityImpl.isClosing()) {
                throw e;
            }
        }
    }

    /**
     * Returns the Plant's internal reactor.
     *
     * @return The reactor belonging to the singleton.
     */
    @Override
    public Facility getInternalFacility() {
        return internalFacility;
    }

    /**
     * Returns 16.
     *
     * @return The reactor default initial local message queue size.
     */
    @Override
    public int getInitialLocalMessageQueueSize() {
        return plantConfiguration.getInitialLocalMessageQueueSize();
    }

    /**
     * Returns 16.
     *
     * @return The reactor default initial buffer size.
     */
    @Override
    public int getInitialBufferSize() {
        return plantConfiguration.getInitialBufferSize();
    }
}
