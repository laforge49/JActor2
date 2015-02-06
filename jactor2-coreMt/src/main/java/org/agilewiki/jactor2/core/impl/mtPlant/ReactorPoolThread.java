package org.agilewiki.jactor2.core.impl.mtPlant;

import org.agilewiki.jactor2.core.reactors.impl.PoolThreadReactorImpl;

/**
 * Base class for pool threads used by reactors.
 * Created by DefaultReactorPoolThreadFactory.
 */
public class ReactorPoolThread extends Thread {

    private volatile PoolThreadReactorImpl currentReactor;

    private int maxThreadMigrations;

    private int migrationCount;

    /**
     * Create a pool thread.
     *
     * @param _runnable The runnable to be executed by the thread.
     */
    public ReactorPoolThread(final Runnable _runnable) {
        super(_runnable);
    }

    /**
     * Returns the current reactor.
     *
     * @return The current reactor, or null.
     */
    public PoolThreadReactorImpl getCurrentReactorImpl() {
        return currentReactor;
    }

    /**
     * Assigns the current reactor.
     *
     * @param _reactor The current reactor.
     */
    public void setCurrentReactor(final PoolThreadReactorImpl _reactor) {
        currentReactor = _reactor;
    }

    /**
     * Assigns the max thread migrations.
     *
     * @param _maxThreadMigrations    Limits the number of times a thread will follow a message in succession.
     */
    public void setMaxThreadMigrations(final int _maxThreadMigrations) {
        maxThreadMigrations = _maxThreadMigrations;
    }

    /**
     * Clears the migration count.
     */
    public void clearMigrationCount() {
        migrationCount = 0;
    }

    /**
     * Checks to see if the max migration count is not exceeded.
     *
     * @return True if migration count did not exceed the max migration count.
     */
    public boolean checkMigrationCount() {
        return migrationCount <= maxThreadMigrations;
    }

    /**
     * Add 1 to migration count.
     */
    public void incMigrationCount() {
        migrationCount += 1;
    }
}
