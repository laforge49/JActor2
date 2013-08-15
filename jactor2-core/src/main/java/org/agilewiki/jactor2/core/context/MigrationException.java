package org.agilewiki.jactor2.core.context;

import org.agilewiki.jactor2.core.processing.MessageProcessorBase;

/**
 * Signals a migration of the current thread to another processing.
 */
public class MigrationException extends RuntimeException {

    /**
     * The newly active processing.
     */
    public MessageProcessorBase mailbox;

    /**
     * Create a new MigrationException.
     *
     * @param _mailbox The newly active processing.
     */
    public MigrationException(final MessageProcessorBase _mailbox) {
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
