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

    /**
     * Create an ActorBase.
     *
     * @param _semaphore The semaphore of the actor.
     */
    public ActorBase(final Semaphore _semaphore) {
        semaphore = _semaphore;
    }

    @Override
    public Semaphore getSemaphore() {
        return semaphore;
    }
}
