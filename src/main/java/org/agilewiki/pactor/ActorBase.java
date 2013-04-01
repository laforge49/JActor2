package org.agilewiki.pactor;

/**
 * Use of this class is entirely optional.
 *
 * ActorBase allows the definition of an actor, with deferred initialization
 * of the Mailbox. Bare in mind that the initialization must happen right after
 * the construction, and before the actor is used, or passed to another thread!
 */
public class ActorBase implements Actor {
    private Mailbox mailbox;
    private boolean initialized;

    public void initialize() {
        initialize(null);
    }

    public void initialize(final Mailbox _mailbox) {
        if (initialized)
            throw new IllegalStateException("Already initialized");
        initialized = true;
        mailbox = _mailbox;
    }

    @Override
    public Mailbox getMailbox() {
        return mailbox;
    }
}
