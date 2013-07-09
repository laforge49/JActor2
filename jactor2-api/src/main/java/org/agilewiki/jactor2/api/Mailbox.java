package org.agilewiki.jactor2.api;

/**
 * A mailbox is a light-weight thread. Requests/responses passed to a mailbox are enqueued and subsequently processed
 * when a thread is allocated to the mailbox. And when the queue is empty, the thread is released.
 */
public interface Mailbox extends Runnable, _Mailbox {

    /**
     * Returns the mailbox factory.
     *
     * @return The mailbox factory.
     */
    MailboxFactory getMailboxFactory();

    /**
     * Returns true when there no requests or responses enqueued for processing.
     *
     * @return True when there no requests or responses enqueued for processing.
     */
    boolean isEmpty();

    /**
     * The flush method forwards all buffered requests to their target mailbox for
     * processing and all buffered results/exceptions to their source
     * mailbox. For results/exceptions originating from a call, the calling thread
     * is unblocked and the results returned or the exception thrown.
     * <p>
     * The flush method is called when the last enqueued request/result is processed.
     * However, there may be special cases where an explicit flush may be needed.
     * </p>
     *
     * @return True when one or more buffered request/result was delivered.
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
