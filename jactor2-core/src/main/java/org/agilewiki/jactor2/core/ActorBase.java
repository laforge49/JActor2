package org.agilewiki.jactor2.core;

import org.agilewiki.jactor2.core.mailbox.Mailbox;

/**
 * <p>
 * ActorBase is a convenience class that implements an Actor. Initialization is not
 * thread-safe, so it should be done before a reference to the actor is shared.
 * </p>
 * <h3>Sample Usage:</h3>
 * <pre>
 * public class ActorBaseSample extends ActorBase {
 *     public ActorBaseSample(final Mailbox _Mailbox) throws Exception {
 *         initialize(_Mailbox);
 *     }
 * }
 * </pre>
 */
public class ActorBase implements Actor {
    /**
     * The actor's mailbox.
     */
    private Mailbox mailbox;

    /**
     * True when initialized, this flag is used to prevent the mailbox from being changed.
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
     * Initialize an actor. This method can only be called once
     * without raising an illegal state exception, as the mailbox
     * can not be changed.
     *
     * @param _mailbox The actor's mailbox.
     */
    public void initialize(final Mailbox _mailbox) throws Exception {
        if (initialized)
            throw new IllegalStateException("Already initialized");
        if (_mailbox == null)
            throw new IllegalArgumentException("Mailbox may not be null");
        initialized = true;
        mailbox = _mailbox;
    }

    @Override
    public Mailbox getMailbox() {
        return mailbox;
    }
}
