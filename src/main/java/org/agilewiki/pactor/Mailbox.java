package org.agilewiki.pactor;

/**
 * <p>
 * A mailbox is a container for holding the incoming messages that are signal to a Actor( Called here POJO Actor). Every
 * PActor has associated mailbox, the messages signal to the PActor are processed by mailbox. The mailbox implementation
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

    /** Creates a Mailbox with a default message queue
     * with commandeering and message buffering enabled. */
    Mailbox createMailbox();

    /** Creates a Mailbox with a default message queue
     * with message buffering enabled. */
    Mailbox createMailbox(final boolean _disableCommandeering);

    /** Creates a Mailbox with a default message queue. */
    Mailbox createMailbox(final boolean _disableCommandeering,
                          final boolean _disableMessageBuffering);

    /**
     * Returns true when the inbox is empty.
     */
    boolean isEmpty();

    /**
     * Flush buffered messages.
     */
    void flush() throws Exception;

    /**
     * This should signal the _Request to the associated mailbox's queue in asynchronous
     * mode.
     *
     * @param request _Request Object that should encapsulate the Requested Information
     *                to be processed.
     */
    <A extends Actor> void signal(final _Request<Void, A> request, final A targetActor) throws Exception;

    /**
     * Same as signal(_Request) until buffered message are implemented.
     */
    <A extends Actor> void signal(final _Request<Void, A> request,
                                  final Mailbox source,
                                  final A targetActor) throws Exception;

    /**
     * This should signal the _Request to the associated mailbox's queue with specific return
     * type which is encapsulated in ResponseProcessor. send with VoidResponseProcessor
     * will act same as the signal method.
     *
     * @param request           _Request Object that should encapsulate the Requested Information
     *                          to be processed.
     * @param source            The mailbox reference where the Response Message should be dispatched.
     * @param responseProcessor The response processor implementation.
     */
    <E, A extends Actor> void send(final _Request<E, A> request,
                                   final Mailbox source,
                                   final A targetActor,
                                   final ResponseProcessor<E> responseProcessor) throws Exception;

    /**
     * This should signal the _Request to the associated mailbox's queue in synchronous mode.
     * The thread that invokes this operation will wait for process to be executed and
     * response to be signal back the invoking thread.
     *
     * @param request _Request Object that should encapsulate the Requested Information
     *                to be processed.
     */
    <E, A extends Actor> E call(final _Request<E, A> request, final A targetActor) throws Exception;

    ExceptionHandler setExceptionHandler(final ExceptionHandler exceptionHandler);

    void processMessages();
}
