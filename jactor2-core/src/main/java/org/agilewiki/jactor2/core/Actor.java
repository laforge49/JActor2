package org.agilewiki.jactor2.core;

import org.agilewiki.jactor2.core.processing.Reactor;

/**
 * <p>
 * Actors must implement the Actor interface to provide access to their reactor.
 * </p>
 * <h3>Sample Usage:</h3>
 * <pre>
 * public class ActorSample implements Actor {
 *     private final Reactor processor;
 *
 *     ActorSample(final Reactor _processor) {
 *         processor = _processor;
 *     }
 *
 *     {@literal @}Override
 *     public final Reactor getReactor() {
 *         return processor;
 *     }
 * }
 * </pre>
 */
public interface Actor {
    /**
     * Returns the reactor associated with this Actor.
     *
     * @return The actor's reactor.
     */
    public Reactor getReactor();
}
