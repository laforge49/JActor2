package org.agilewiki.jactor2.core.facilities;

import org.agilewiki.jactor2.core.reactors.ReactorBase;

/**
 * Signals a migration of the current thread to another targetReactor.
 * As this exception is never thrown when a message is being processed,
 * the application should never be exposed to it.
 */
public class MigrationException extends RuntimeException {

    /**
     * The newly active targetReactor.
     */
    public final ReactorBase messageProcessor;

    /**
     * Create a new MigrationException.
     *
     * @param _messageProcessor The newly active processing.
     */
    public MigrationException(final ReactorBase _messageProcessor) {
        messageProcessor = _messageProcessor;
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
