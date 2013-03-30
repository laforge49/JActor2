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

    ExceptionHandler setExceptionHandler(final ExceptionHandler exceptionHandler);
}
