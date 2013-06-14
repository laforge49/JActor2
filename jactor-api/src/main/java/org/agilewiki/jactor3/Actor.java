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

    /**
     * Returns the thread manager associated with the actor.
     *
     * @return A thread manager, or null.
     */
    ThreadManager getThreadManager();
}
