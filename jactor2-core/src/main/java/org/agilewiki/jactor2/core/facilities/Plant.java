package org.agilewiki.jactor2.core.facilities;

import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.Inbox;
import org.agilewiki.jactor2.core.reactors.Outbox;

import java.util.concurrent.ThreadFactory;

public class Plant extends Facility {

    /**
     * System property flag, jactor.debug, to turn on debug;
     */
    public final static boolean DEBUG = "true".equals(System.getProperty("jactor.debug"));

    private static volatile Plant singleton;

    public static Plant getSingleton() {
        if (singleton == null) {
            throw new IllegalStateException("there is no singleton");
        }
        return singleton;
    }

    /**
     * Create a Plant.
     */
    public Plant() throws Exception {
        this(
                Inbox.DEFAULT_INITIAL_LOCAL_QUEUE_SIZE,
                Outbox.DEFAULT_INITIAL_BUFFER_SIZE,
                20,
                new DefaultThreadFactory());
    }

    /**
     * Create a Plant.
     *
     * @param _threadCount The thread pool size.
     */
    public Plant(final int _threadCount) throws Exception {
        this(
                Inbox.DEFAULT_INITIAL_LOCAL_QUEUE_SIZE,
                Outbox.DEFAULT_INITIAL_BUFFER_SIZE,
                _threadCount,
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
                 final int _initialBufferSize,
                 final int _threadCount,
                 final ThreadFactory _threadFactory) throws Exception {
        super(_initialLocalMessageQueueSize, _initialBufferSize, _threadCount, _threadFactory);
        if (singleton != null) {
            throw new IllegalStateException("the singleton already exists");
        }
        singleton = this;
        if (DEBUG)
            System.out.println("\n*** jactor.debug = true ***\n");
        firstSet(NAME_PROPERTY, PLANT_NAME);
    }

    @Override
    public Plant getPlant() {
        return this;
    }

    public AsyncRequest<Void> dependencyAReq(final Facility _dependency) {
        return new AsyncBladeRequest<Void>() {
            @Override
            protected void processAsyncRequest() throws Exception {
                throw new UnsupportedOperationException("Plant can have no dependencies");
            }
        };
    }

    public SyncRequest<Facility> createFacilitySReq(final String _name) throws Exception {
        return new SyncBladeRequest<Facility>() {
            @Override
            protected Facility processSyncRequest() throws Exception {
                Facility facility = _createFacility(
                        _name,
                        Inbox.DEFAULT_INITIAL_LOCAL_QUEUE_SIZE,
                        Outbox.DEFAULT_INITIAL_BUFFER_SIZE,
                        20,
                        new DefaultThreadFactory());
                initFacility(_name, facility);
                return facility;
            }
        };
    }

    public SyncRequest<Facility> createFacilitySReq(final String _name,
                                                    final int _threadCount) throws Exception {
        return new SyncBladeRequest<Facility>() {
            @Override
            protected Facility processSyncRequest() throws Exception {
                Facility facility = _createFacility(
                        _name,
                        Inbox.DEFAULT_INITIAL_LOCAL_QUEUE_SIZE,
                        Outbox.DEFAULT_INITIAL_BUFFER_SIZE,
                        _threadCount,
                        new DefaultThreadFactory());
                initFacility(_name, facility);
                return facility;
            }
        };
    }

    public SyncRequest<Facility> createFacilitySReq(final String _name,
                                                    final int _initialLocalMessageQueueSize,
                                                    final int _initialBufferSize,
                                                    final int _threadCount,
                                                    final ThreadFactory _threadFactory) throws Exception {
        return new SyncBladeRequest<Facility>() {
            @Override
            protected Facility processSyncRequest() throws Exception {
                Facility facility = _createFacility(
                        _name,
                        _initialLocalMessageQueueSize,
                        _initialBufferSize,
                        _threadCount,
                        _threadFactory);
                initFacility(_name, facility);
                return facility;
            }
        };
    }

    private void initFacility(final String _name, final Facility _facility) throws Exception {
        firstSet(FACILITY_PROPERTY_PREFIX + _name, _facility);
        closeables.add(_facility);
    }

    @Override
    public final void close() throws Exception {
        singleton = null;
        super.close();
    }
}
