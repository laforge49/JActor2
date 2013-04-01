package org.agilewiki.pactor;

/**
 * A mailbox is a light-weight thread. Requests/responses passed to a mailbox are enqueued and subsequently processed
 * when a thread is allocated to the mailbox. And when the queue is empty, the thread is realeased.
 * <p>
 * Mailboxes buffer outgoing requests/responses for greater throughput, though message buffering can be disabled
 * when a mailbox is created.
 * </p>
 */
public interface Mailbox extends Runnable, _Mailbox {

    /**
     * Returns the mailbox factory.
     */
    MailboxFactory getMailboxFactory();

    /**
     * Returns true when there no requests or responses enqueued for processing.
     */
    boolean isEmpty();

    /**
     * The flush method forwards all buffered requests to their target mailbox for
     * processing and all buffered results/exceptions to their source
     * mailbox. For results/exceptions originating from a call, the calling thread
     * is unblocked and the results returned or the exception thrown.
     */
    void flush() throws Exception;

    /**
     * Replace the current ExceptionHandler with another.
     * @param exceptionHandler The exception handler to be used now.
     *                         May be null if the default exception handler is to be used.
     * @return The exception handler that was previously in effect, or null if the
     * default exception handler was in effect.
     */
    ExceptionHandler setExceptionHandler(final ExceptionHandler exceptionHandler);
}
