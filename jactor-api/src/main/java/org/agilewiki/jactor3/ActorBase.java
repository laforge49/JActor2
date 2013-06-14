package org.agilewiki.jactor3;

import java.util.concurrent.Semaphore;

/**
 * Convenience class for implementing Actors.
 */
public class ActorBase implements Actor {

    /**
     * The semaphore of the actor.
     */
    public final Semaphore semaphore;

    public final ThreadManager threadManager;

    /**
     * Create an ActorBase.
     *
     * @param _semaphore The semaphore of the actor.
     */
    public ActorBase(final Semaphore _semaphore) {
        this(_semaphore, null);
    }

    /**
     * Create an ActorBase.
     *
     * @param _semaphore The semaphore of the actor.
     */
    public ActorBase(final Semaphore _semaphore, final ThreadManager _threadManager) {
        semaphore = _semaphore;
        threadManager = _threadManager;
    }

    @Override
    public Semaphore getSemaphore() {
        return semaphore;
    }

    @Override
    public ThreadManager getThreadManager() {
        return threadManager;
    }
}
