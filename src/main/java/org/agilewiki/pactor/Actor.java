package org.agilewiki.pactor;

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

    /**
     * Returns true, if both actor uses the same Mailbox, and therefore can call each other directly.
     */
    public boolean sameMailbox(Actor other);
}
