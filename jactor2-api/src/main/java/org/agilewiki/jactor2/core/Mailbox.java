package org.agilewiki.jactor2.core;

/**
 * A mailbox implements an inbox for incoming messages (events/requests)
 * and buffers outgoing messages by destination mailbox.
 * <p/>
 * While a mailbox has a non-empty inbox, it has an assigned thread that processes
 * the contents of its inbox. And only one message is processed at a time.
 */
public interface Mailbox extends Runnable, _Mailbox {

    /**
     * Returns the mailbox factory.
     *
     * @return The mailbox factory.
     */
    MailboxFactory getMailboxFactory();

    /**
     * Is there work that can be done?
     *
     * @return True when there is work ready to be done.
     */
    boolean hasWork();

    /**
     * Is nothing pending?
     *
     * @return True when there is no work pending.
     */
    boolean isIdle();

    /**
     * The flush method forwards all buffered message to their target mailbox for
     * processing. For results/exceptions originating from a call, the calling thread
     * is unblocked and the results returned or the exception thrown.
     * <p>
     * The flush method is automatically called either after processing each message or when there are
     * no more messages to be processed, depending on the type of mailbox.
     * However, there may be special cases where an explicit flush is needed.
     * </p>
     *
     * @return True when one or more buffered messages were delivered.
     */
    boolean flush() throws Exception;

    /**
     * Replace the current ExceptionHandler with another.
     *
     * @param exceptionHandler The exception handler to be used now.
     *                         May be null if the default exception handler is to be used.
     * @return The exception handler that was previously in effect, or null if the
     *         default exception handler was in effect.
     */
    ExceptionHandler setExceptionHandler(final ExceptionHandler exceptionHandler);
}
