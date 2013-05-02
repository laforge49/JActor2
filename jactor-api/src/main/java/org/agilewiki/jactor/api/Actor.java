package org.agilewiki.jactor.api;

/**
 * Actors which receive unbound requests must implement this interface, which is otherwise optional.
 */
public interface Actor {
    /**
     * Returns the mailbox associated with this Actor.
     *
     * @return The actor's mailbox.
     */
    public Mailbox getMailbox();
}
