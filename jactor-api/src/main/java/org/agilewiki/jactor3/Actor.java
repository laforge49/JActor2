package org.agilewiki.jactor3;

import java.util.concurrent.Semaphore;

/**
 * A semaphore-based actor.
 */
public interface Actor {

    /**
     * Returns the semaphore of the actor.
     *
     * @return The semaphore.
     */
    Semaphore getSemaphore();
}
