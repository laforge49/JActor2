/**
 *
 */
package org.agilewiki.pactor;

import org.agilewiki.pactor.impl.MessageQueue;

/**
 * <p>
 * The MailboxFactory is the factory as the name suggests for the MailBoxes to be used with the PActor. In addition to
 * creation of the Mailboxes it also encapsulates the threads( threadpool) which would process the Requests added to
 * the mailbox in asynchronous mode.
 * </p>
 */
public interface MailboxFactory extends AutoCloseable {

    /** Creates a Mailbox with a default message queue. */
    Mailbox createMailbox();

    /** Creates a Mailbox with a specific message queue. */
    Mailbox createMailbox(final MessageQueue messageQueue);

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