package org.agilewiki.jactor2.core;

import org.agilewiki.jactor2.core.processing.Mailbox;

/**
 * <p>
 * Actors must implement the Actor interface to provide access to their processing.
 * </p>
 * <h3>Sample Usage:</h3>
 * <pre>
 * public class ActorSample implements Actor {
 *     private final Mailbox processing;
 *
 *     ActorSample(final Mailbox _mailbox) {
 *         processing = _mailbox;
 *     }
 *
 *     {@literal @}Override
 *     public final Mailbox getMailbox() {
 *         return processing;
 *     }
 * }
 * </pre>
 */
public interface Actor {
    /**
     * Returns the processing associated with this Actor.
     *
     * @return The actor's processing.
     */
    public Mailbox getMailbox();
}
