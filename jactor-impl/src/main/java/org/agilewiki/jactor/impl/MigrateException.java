package org.agilewiki.jactor.impl;

public class MigrateException extends RuntimeException {

    public JAMailbox mailbox;

    public MigrateException(final JAMailbox _mailbox) {
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
