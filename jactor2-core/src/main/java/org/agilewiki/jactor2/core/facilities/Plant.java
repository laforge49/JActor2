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
        String recoveryClassName = System.getProperty("jactor.recoveryClass");
        if (recoveryClassName != null) {
            ClassLoader classLoader = getClass().getClassLoader();
            Class recoveryClass = classLoader.loadClass(recoveryClassName);
            recovery = (Recovery) recoveryClass.newInstance();
        } else
            recovery = new DefaultRecovery();
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
                facility.recovery = recovery;
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

    public AsyncRequest<Void> dependencyAReq(final Facility _dependent, final Facility _dependency) {

        return new AsyncBladeRequest<Void>() {

            AsyncResponseProcessor<Void> dis = this;

            String dependencyPropertyName;

            @Override
            protected void processAsyncRequest() throws Exception {
                final String dependentName = _dependent.name;
                final String name = _dependency.name;
                if (name == null) {
                    throw new IllegalArgumentException(
                            "the dependency has no name");
                }
                if (PLANT_NAME.equals(name))
                    dis.processAsyncResponse(null);
                dependencyPropertyName = FACILITY_PREFIX + dependentName + "." + DEPENDENCY_PROPERTY_PREFIX + name;
                if (getProperty(dependencyPropertyName) != null) {
                    throw new IllegalStateException(
                            "the dependency was already present");
                }
                if (_dependency.hasDependency(dependentName))
                    throw new IllegalArgumentException(
                            "this would create a cyclic dependency");
                _dependency.addCloseable(_dependent);
                send(propertiesProcessor.putAReq(dependencyPropertyName, true), dis);
            }
        };
    }

    public Facility getFacility(String name) {
        return (Facility) getProperty(FACILITY_PROPERTY_PREFIX + name);
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
                        String prefix = FACILITY_PREFIX+_facilityName+"."+DEPENDENCY_PROPERTY_PREFIX;
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
