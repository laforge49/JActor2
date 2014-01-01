package org.agilewiki.jactor2.core.impl;

import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.plant.PlantConfiguration;
import org.agilewiki.jactor2.core.plant.ThreadManager;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class PlantImpl {

    /**
     * System property flag, jactor.debug, to turn on debug;
     */
    public static final boolean DEBUG = "true".equals(System
            .getProperty("jactor.debug"));

    private static volatile Plant singleton;

    public static Plant getSingleton() {
        return singleton;
    }

    private PlantConfiguration plantConfiguration;

    private boolean exitOnClose;

    /**
     * The thread pool.
     */
    private ThreadManager threadManager;

    private NonBlockingReactor reactor;

    public void initialize(final Plant _plant) throws Exception {
        initialize(_plant, new PlantConfiguration());
    }

    public void initialize(final Plant _plant, final int _threadCount) throws Exception {
        initialize(_plant, new PlantConfiguration(_threadCount));
    }

    public void initialize(final Plant _plant, final PlantConfiguration _plantConfiguration) throws Exception {
        _plantConfiguration.initialize();
        if (singleton != null) {
            throw new IllegalStateException("the singleton already exists");
        }
        singleton = _plant;
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
        reactor = new NonBlockingReactor((ReactorImpl) null, plantConfiguration.getInitialBufferSize(),
                plantConfiguration.getInitialLocalMessageQueueSize(),
                plantConfiguration.getRecovery(), plantConfiguration.getScheduler(), null);
        threadManager = plantConfiguration.getThreadManager();
        long reactorPollMillis = _plantConfiguration.getRecovery().getReactorPollMillis();
        _plantConfiguration.getScheduler().scheduleAtFixedRate(plantPoll(),
                reactorPollMillis);
    }

    public NonBlockingReactor getReactor() {
        return reactor;
    }

    private Runnable plantPoll() {
        return new Runnable() {
            public void run() {
                try {
                    reactor.asReactorImpl().reactorPoll();
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
            if (!reactor.asReactorImpl().isClosing()) {
                throw e;
            }
        } catch (final Error e) {
            if (!reactor.asReactorImpl().isClosing()) {
                throw e;
            }
        }
    }

    public void close() throws Exception {
        if (singleton == null) {
            return;
        }
        try {
            reactor.close();
        } finally {
            singleton = null;
            plantConfiguration.close();
            if (exitOnClose)
                System.exit(0);
            threadManager.close();
        }
    }

    public void exit() {
        exitOnClose = true;
        try {
            close();
        } catch (Throwable t) {
            reactor.asReactorImpl().getLogger().error("exception on exit", t);
        } finally {
            System.exit(1);
        }
    }

    public SchedulableSemaphore schedulableSemaphore(final long _millisecondDelay) {
        SchedulableSemaphore schedulableSemaphore = new SchedulableSemaphore();
        plantConfiguration.getScheduler().schedule(schedulableSemaphore.runnable, _millisecondDelay);
        return schedulableSemaphore;
    }
}
