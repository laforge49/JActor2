package org.agilewiki.jactor2.core;

/**
 * ActorBase implements an Actor bean, i.e. it no constructor arguments.
 * Initialization is not thread-safe, so it should be done
 * before a reference to the actor is shared.
 * <p/>
 * Use of this class is entirely optional, as actors need only a reference to a mailbox
 * to be able to exchange messages with other actors.
 */
public class ActorBase implements Actor {
    /**
     * The actor's mailbox.
     */
    private Mailbox mailbox;

    /**
     * True when initialized, this flag prevents duplicate initialization.
     */
    private boolean initialized;

    /**
     * Returns true when the actor has been initialized.
     *
     * @return True when the actor has been initialized.
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Initialize an actor.
     *
     * @param _mailbox The actor's mailbox.
     */
    public void initialize(final Mailbox _mailbox) throws Exception {
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
