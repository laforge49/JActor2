/**
 *
 */
package org.agilewiki.pactor;

/**
 * <p>
 * The MailboxFactory is the factory as the name suggests for the MailBoxes to be used with the PActor. In addition to
 * creation of the Mailboxes it also encapsulates the threads( threadpool) which would process the Requests added to
 * the mailbox in asynchronous mode.
 * </p>
 */
public interface MailboxFactory extends AutoCloseable {

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
     * Create a mailbox that runs on the current thread.
     * <p>
     * When messageProcessor.run will typically us SwingUtilities.invokeLater(mailbox)
     * to process the pending messages.
     * </p>
     *
     *
     * @param _messageProcessor The run method is called when there are messages t be processed.
     * @return The thread bounded mailbox.
     */
    Mailbox createThreadBoundMailbox(final Runnable _messageProcessor);

    /**
     * Runs a Runnable in the internal executor service.
     * Normally, the runnable is a Mailbox.
     */
    void submit(final Runnable task) throws Exception;

    /** Adds a closeable, to close when the MailboxFactory closes down. */
    void addAutoClosable(final AutoCloseable closeable);

    /** Returns true, if close() has been called already. */
    boolean isShuttingDown();
}