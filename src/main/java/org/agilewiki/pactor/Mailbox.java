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
public interface Mailbox extends Runnable, _Mailbox {

    /**
     * Returns the mailbox factory.
     */
    MailboxFactory getMailboxFactory();

    /**
     * Creates a Mailbox
     * with both commandeering and message buffering enabled.
     * (This is a convenience method which simply calls the corresponding
     * method on the mailbox factory.)
     */
    Mailbox createMailbox();

    /**
     * Creates a Mailbox
     * with message buffering enabled.
     * (This is a convenience method which simply calls the corresponding
     * method on the mailbox factory.)
     *
     * @param _disableCommandeering Disables commandeering when true.
     */
    Mailbox createMailbox(final boolean _disableCommandeering);

    /**
     * Creates a Mailbox.
     * (This is a convenience method which simply calls the corresponding
     * method on the mailbox factory.)
     *
     * @param _disableCommandeering    Disables commandeering when true.
     * @param _disableMessageBuffering Disables message buffering when true.
     */
    Mailbox createMailbox(final boolean _disableCommandeering,
                          final boolean _disableMessageBuffering);

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
