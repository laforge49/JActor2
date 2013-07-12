package org.agilewiki.jactor2.impl;

public class MigrateException extends RuntimeException {

    public UnboundMailbox mailbox;

    public MigrateException(final UnboundMailbox _mailbox) {
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
