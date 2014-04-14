package org.agilewiki.jactor2.core.impl.stPlant;

import org.agilewiki.jactor2.core.plant.PlantImpl;
import org.agilewiki.jactor2.core.plant.PlantScheduler;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.PoolThreadReactorImpl;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.reactors.ReactorImpl;
import org.agilewiki.jactor2.core.requests.*;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class PlantStImpl extends PlantImpl {

    /**
     * Returns this singleton.
     *
     * @return This singleton.
     */
    public static PlantStImpl getSingleton() {
        return (PlantStImpl) PlantImpl.getSingleton();
    }

    /**
     * System property flag, jactor.debug, to turn on debug;
     */
    public static final boolean DEBUG = "true".equals(System
            .getProperty("jactor.debug"));

    private PlantConfiguration plantConfiguration;

    private final NonBlockingReactor internalReactor;

    public ReactorImpl currentReactorImpl;

    private final Queue<PoolThreadReactorImpl> pendingReactors = new LinkedBlockingQueue<PoolThreadReactorImpl>();

    /**
     * Create the singleton with a default configuration.
     */
    public PlantStImpl() {
        this(new PlantConfiguration());
    }

    /**
     * Create the singleton with the given configuration.
     *
     * @param _plantConfiguration The configuration to be used by the singleton.
     */
    public PlantStImpl(final PlantConfiguration _plantConfiguration) {
        if (DEBUG) {
            System.out.println("\n*** jactor.debug = true ***\n");
        }
        plantConfiguration = _plantConfiguration;
        internalReactor = createInternalReactor();
    }

    @Override
    public ReactorImpl getCurrentReactorImpl() {
        return currentReactorImpl;
    }

    @Override
    public ReactorImpl createNonBlockingReactorImpl(
            final NonBlockingReactor _parentReactor,
            final int _initialOutboxSize, final int _initialLocalQueueSize) {
        return null; //todo new NonBlockingReactorMtImpl(_parentReactor, _initialOutboxSize, _initialLocalQueueSize);
    }

    @Override
    public ReactorImpl createBlockingReactorImpl(
            final NonBlockingReactor _parentReactor,
            final int _initialOutboxSize, final int _initialLocalQueueSize) {
        return null; //todo new BlockingReactorMtImpl(_parentReactor, _initialOutboxSize, _initialLocalQueueSize);
    }

    @Override
    public ReactorImpl createIsolationReactorImpl(
            final NonBlockingReactor _parentReactor,
            final int _initialOutboxSize, final int _initialLocalQueueSize) {
        return null; //todo new IsolationReactorMtImpl(_parentReactor, _initialOutboxSize, _initialLocalQueueSize);
    }

    @Override
    public ReactorImpl createSwingBoundReactorImpl(
            final NonBlockingReactor _parentReactor,
            final int _initialOutboxSize, final int _initialLocalQueueSize) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ReactorImpl createThreadBoundReactorImpl(
            final NonBlockingReactor _parentReactor,
            final int _initialOutboxSize, final int _initialLocalQueueSize,
            final Runnable _boundProcessor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <RESPONSE_TYPE> RequestImpl<RESPONSE_TYPE> createSyncRequestImpl(
            final SyncRequest<RESPONSE_TYPE> _syncRequest,
            final Reactor _targetReactor) {
        return null; //todo new SyncRequestMtImpl<RESPONSE_TYPE>(_syncRequest, _targetReactor);
    }

    @Override
    public <RESPONSE_TYPE> AsyncRequestImpl<RESPONSE_TYPE> createAsyncRequestImpl(
            final AsyncRequest<RESPONSE_TYPE> _asyncRequest,
            final Reactor _targetReactor) {
        return null; //todo new AsyncRequestMtImpl<RESPONSE_TYPE>(_asyncRequest, _targetReactor);
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
            //todo getInternalReactor().close();
        } finally {
            getPlantScheduler().close();
            super.close();
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
     * Create the Plant's internal reactor.
     *
     * @return The reactor belonging to the singleton.
     */
    protected NonBlockingReactor createInternalReactor() {
        return null; //todo new NonBlockingReactor(null, 0, plantConfiguration.getInitialLocalMessageQueueSize());
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
                    getInternalReactor().asReactorImpl().reactorPoll();
                } catch (final Exception x) {
                    x.printStackTrace();
                }
            }
        };
    }

    /**
     * Returns the Plant's internal reactor.
     *
     * @return The reactor belonging to the singleton.
     */
    @Override
    public NonBlockingReactor getInternalReactor() {
        return internalReactor;
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
        return 0;
    }

    /**
     * Submit a Reactor for subsequent execution.
     *
     * @param _reactor The targetReactor to be run.
     */
    public final void submit(final PoolThreadReactorImpl _reactor) {
        pendingReactors.add(_reactor);
    }
}
