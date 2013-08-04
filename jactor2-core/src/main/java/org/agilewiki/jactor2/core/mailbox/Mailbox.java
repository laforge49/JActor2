package org.agilewiki.jactor2.core.mailbox;

import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.messaging.ExceptionHandler;
import org.agilewiki.jactor2.core.messaging.MessageSource;

/**
 * A mailbox implements an inbox for incoming messages (events/requests)
 * and buffers outgoing messages by destination mailbox.
 * <p/>
 * While a mailbox has a non-empty inbox, it has an assigned thread that processes
 * the contents of its inbox. And only one message is processed at a time.
 */
public interface Mailbox extends Runnable, MessageSource, AutoCloseable {

    /**
     * Returns the mailbox factory.
     *
     * @return The mailbox factory.
     */
    JAContext getJAContext();

    /**
     * Replace the current ExceptionHandler with another.
     *
     * @param exceptionHandler The exception handler to be used now.
     *                         May be null if the default exception handler is to be used.
     * @return The exception handler that was previously in effect, or null if the
     *         default exception handler was in effect.
     */
    ExceptionHandler setExceptionHandler(final ExceptionHandler exceptionHandler);

    /**
     * Is there work that can be done?
     *
     * @return True when there is work ready to be done.
     */
    boolean hasWork();
}
