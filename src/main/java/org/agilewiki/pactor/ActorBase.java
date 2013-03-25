package org.agilewiki.pactor;

/**
 * Use of this class is entirely optional.
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
