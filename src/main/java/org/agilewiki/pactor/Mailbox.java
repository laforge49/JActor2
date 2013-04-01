package org.agilewiki.pactor;

/**
 * A mailbox is a light-weight thread. Requests/responses passed to a mailbox are enqueued and subsequently processed
 * when a thread is allocated to the mailbox. And when the queue is empty, the thread is realeased.
 * <p>
 * Mailboxes buffer outgoing requests/responses for greater throughput, though message buffering can be disabled
 * when a mailbox is created.
 * </p>
 * <p>
 * A mailbox can also commandeer other mailboxes when passing a request/response to another mailbox for
 * which a thread has not been allocated. This allows the mailbox passing the request/response to process the contents
 * of the other mailbox's queue for greater throughput. This feature must be turned off for mailboxes which receive
 * requests that are CPU intensive or which otherwise block their thread. Failure to disable this feature can result
 * in multiple mailboxes being blocked when a single blocking request is processed.
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
