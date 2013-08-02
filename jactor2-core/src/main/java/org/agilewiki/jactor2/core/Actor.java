package org.agilewiki.jactor2.core;

import org.agilewiki.jactor2.core.mailbox.Mailbox;

/**
 * <p>
 * Simply by implementing this interface turns an object into an actor that can
 * exchange messages with other actors.
 * </p>
 * <h3>Sample Usage:</h3>
 * <pre>
 * public class ActorSample implements Actor {
 *     private final Mailbox mailbox;
 *
 *     ActorSample(final Mailbox _mailbox) {
 *         mailbox = _mailbox;
 *     }
 *
 *     {@literal @}Override
 *     public final Mailbox getMailbox() {
 *         return mailbox;
 *     }
 * }
 * </pre>
 */
public interface Actor {
    /**
     * Returns the mailbox associated with this Actor.
     *
     * @return The actor's mailbox.
     */
    public Mailbox getMailbox();
}
