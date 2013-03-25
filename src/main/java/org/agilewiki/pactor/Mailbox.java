package org.agilewiki.pactor;

/**
 * <p>
 * A mailbox is a container for holding the incoming messages that are send to a Actor( Called here POJO Actor). Every
 * PActor has associated mailbox, the messages send to the PActor are processed by mailbox. The mailbox implementation
 * is a lightweight thread which gets activated(if not running) when the messages are added to the mailbox. The mailbox
 * thread keeps running till all the messages in the mailbox are processed.
 * </p><p>
 * Request are submitted to the MailboxFactory which internally calls the mailbox thread to consume the Request.
 * </p>
 */
public interface Mailbox {

    /**
     * Returns the mailbox factory.
     */
    MailboxFactory getMailboxFactory();

    /**
     * Returns true when the inbox is empty.
     */
    boolean isEmpty();

    /**
     * Flush buffered messages.
     */
    void flush() throws Exception;

    /**
     * This should send the Request to the associated mailbox's queue in asynchronous
     * mode.
     *
     * @param request Request Object that should encapsulate the Requested Information
     * to be processed.
     */
    void send(final Request<?> request) throws Exception;

    /**
     * Same as send(Request) until buffered message are implemented.
     */
    void send(final Request<?> request, final Mailbox source) throws Exception;

    /**
     * This should send the Request to the associated mailbox's queue with specific return
     * type which is encapsulated in ResponseProcessor. reply with VoidResponseProcessor
     * will act same as the send method.
     *
     *
     * @param request Request Object that should encapsulate the Requested Information
     * to be processed.
     * @param source The mailbox reference where the Response Message should be dispatched.
     * @param responseProcessor The response processor implementation.
     */
    <E> void reply(final Request<E> request, final Mailbox source,
            final ResponseProcessor<E> responseProcessor) throws Exception;

    /**
     * This should send the Request to the associated mailbox's queue in synchronous mode.
     * The thread that invokes this operation will wait for process to be executed and
     * response to be send back the invoking thread.
     *
     * @param request Request Object that should encapsulate the Requested Information
     * to be processed.
     */
    <E> E pend(final Request<E> request) throws Exception;

    ExceptionHandler setExceptionHandler(final ExceptionHandler exceptionHandler);

    /** Returns a mailbox wrapper that does not buffer */
    Mailbox autoFlush();
}
