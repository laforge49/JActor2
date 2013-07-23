package org.agilewiki.jactor2.api;

/**
 * Actors which receive events must implement this interface, which is otherwise optional.
 */
public interface Actor {
    /**
     * Returns the mailbox associated with this Actor.
     *
     * @return The actor's mailbox.
     */
    public Mailbox getMailbox();
}
