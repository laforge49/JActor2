package org.agilewiki.jactor2.impl;

/**
 * Signals a migration of the current thread to another mailbox.
 */
public class MigrationException extends RuntimeException {

    /**
     * The newly active mailbox.
     */
    public UnboundMailbox mailbox;

    /**
     * Create a new MigrationException.
     *
     * @param _mailbox The newly active mailbox.
     */
    public MigrationException(final UnboundMailbox _mailbox) {
        mailbox = _mailbox;
    }

    /**
     * Speeds things up by not filling in the stack trace.
     *
     * @return this
     */
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
