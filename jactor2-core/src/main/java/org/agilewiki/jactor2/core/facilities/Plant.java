package org.agilewiki.jactor2.core.facilities;

import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.reactors.Inbox;
import org.agilewiki.jactor2.core.reactors.Outbox;
import org.agilewiki.jactor2.core.reactors.Reactor;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class Plant extends Facility {
    public final static int DEFAULT_THREAD_COUNT = 20;

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
        this(Inbox.DEFAULT_INITIAL_LOCAL_QUEUE_SIZE,
                Outbox.DEFAULT_INITIAL_BUFFER_SIZE, DEFAULT_THREAD_COUNT,
                new DefaultThreadFactory());
    }

    /**
     * Create a Plant.
     *
     * @param _threadCount The thread pool size.
     */
    public Plant(final int _threadCount) throws Exception {
        this(Inbox.DEFAULT_INITIAL_LOCAL_QUEUE_SIZE,
                Outbox.DEFAULT_INITIAL_BUFFER_SIZE, _threadCount,
                new DefaultThreadFactory());
    }

    /**
     * Create a Plant.
     *
     * @param _initialLocalMessageQueueSize How big should the initial inbox doLocal queue size be?
     * @param _initialBufferSize            How big should the initial outbox (per target Reactor) buffer size be?
     * @param _threadCount                  The thread pool size.
     * @param _threadFactory                The factory used to create threads for the threadpool.
     */
    public Plant(final int _initialLocalMessageQueueSize,
            final int _initialBufferSize, final int _threadCount,
            final ThreadFactory _threadFactory) throws Exception {
        super(PLANT_NAME, _initialLocalMessageQueueSize, _initialBufferSize);
        threadManager = new ThreadManager(_threadCount, _threadFactory);
        if (singleton != null) {
            throw new IllegalStateException("the singleton already exists");
        }
        singleton = this;
        if (DEBUG) {
            System.out.println("\n*** jactor.debug = true ***\n");
        }
        initialize(this);
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

    @Override
    public AsyncRequest<Void> dependencyAReq(final Facility _dependency) {
        return new AsyncBladeRequest<Void>() {
            @Override
            protected void processAsyncRequest() throws Exception {
                throw new UnsupportedOperationException(
                        "Plant can have no dependencies");
            }
        };
    }

    public AsyncRequest<Facility> createFacilityAReq(final String _name)
            throws Exception {
        return createFacilityAReq(
                _name,
                Inbox.DEFAULT_INITIAL_LOCAL_QUEUE_SIZE,
                Outbox.DEFAULT_INITIAL_BUFFER_SIZE);
    }

    public AsyncRequest<Facility> createFacilityAReq(final String _name,
            final int _initialLocalMessageQueueSize,
            final int _initialBufferSize) throws Exception {
        return new AsyncBladeRequest<Facility>() {
            final AsyncResponseProcessor<Facility> dis = this;

            @Override
            protected void processAsyncRequest() throws Exception {
                final Facility facility = new Facility(_name,
                        _initialLocalMessageQueueSize, _initialBufferSize);
                facility.initialize(Plant.this);
                send(getPropertiesProcessor().putAReq(
                        FACILITY_PROPERTY_PREFIX + _name, facility),
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
        threadManager.close();
        close3();
    }

    @Override
    protected void close3() throws Exception {
        super.close();
        if (exitOnClose)
            System.exit(0);
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

    public boolean isForceExit() {
        return forceExit;
    }

    public void forceExit() {
        forceExit = true;
        exit();
    }

    public SchedulableSemaphore schedulableSemaphore(final long _delay, final TimeUnit _timeUnit) {
        SchedulableSemaphore schedulableSemaphore = new SchedulableSemaphore();
        semaphoreScheduler.schedule(schedulableSemaphore, _delay, _timeUnit);
        return schedulableSemaphore;
    }
}
