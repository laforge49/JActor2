package org.agilewiki.jactor2.core;

import org.agilewiki.jactor2.core.processing.NonBlockingReactor;

/**
 * An actor which does not perform long computations nor otherwise block the thread.
 * </p>
 * This is just a convenience class, as any actor which uses a non-blocking reactor
 * is a non-blocking actor.
 * </p>
 * <h3>Sample Usage:</h3>
 * <pre>
 * public class NonBlockingActorSample extends NonBlockingActor {
 *     public NonBlockingActorSample(final Facility _facility) throws Exception {
 *         super(new NonBlockingReactor(_facility));
 *     }
 * }
 * </pre>
 */
public class NonBlockingActor extends ActorBase {

    /**
     * Create a non-blocking actor.
     *
     * @param _nonBlockingReactor A reactor for actors which process messages
     *                            quickly and without blocking the thread.
     */
    public NonBlockingActor(final NonBlockingReactor _nonBlockingReactor)
            throws Exception {
        initialize(_nonBlockingReactor);
    }
}
