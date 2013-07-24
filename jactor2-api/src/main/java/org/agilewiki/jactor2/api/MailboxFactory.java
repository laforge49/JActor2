package org.agilewiki.jactor2.api;

/**
 * MailboxFactory creates NonBlocking, MayBlock and ThreadBound mailboxes.
 * This class is also responsible for closing everything down and managing a list of
 * auto closables to be called when MailboxFactory.close() is called.
 */
public interface MailboxFactory extends AutoCloseable {

    /**
     * Creates a Mailbox which is only used to process non-blocking messages.
     *
     * @return A new non-blocking mailbox.
     */
    NonBlockingMailbox createNonBlockingMailbox();

    /**
     * Creates a Mailbox which is only used to process non-blocking requests.
     *
     * @param initialBufferSize How big should the initial (per target Mailbox) buffer size be?
     * @return A new non-blocking mailbox.
     */
    NonBlockingMailbox createNonBlockingMailbox(final int initialBufferSize);

    /**
     * Creates a Mailbox which is only used to process non-blocking requests.
     *
     * @param onIdle The run method is called when the input queue is empty.
     * @return A new non-blocking mailbox.
     */
    NonBlockingMailbox createNonBlockingMailbox(final Runnable onIdle);

    /**
     * Creates a Mailbox which is only used to process non-blocking requests.
     *
     * @param initialBufferSize How big should the initial (per target Mailbox) buffer size be?
     * @param onIdle            The run method is called when the input queue is empty.
     * @return A new non-blocking mailbox.
     */
    NonBlockingMailbox createNonBlockingMailbox(final int initialBufferSize,
                                                final Runnable onIdle);

    /**
     * Creates a Mailbox for processing messages that perform long computations or which may block the thread.
     *
     * @return A new may block mailbox.
     */
    MayBlockMailbox createMayBlockMailbox();

    /**
     * Creates a Mailbox for processing messages that perform long computations or which may block the thread.
     *
     * @param initialBufferSize How big should the initial (per target Mailbox) buffer size be?
     * @return A new may block mailbox.
     */
    MayBlockMailbox createMayBlockMailbox(final int initialBufferSize);

    /**
     * Creates a Mailbox for processing messages that perform long computations or which may block the thread.
     *
     * @param onIdle The run method is called when the input queue is empty.
     * @return A new may block mailbox.
     */
    MayBlockMailbox createMayBlockMailbox(final Runnable onIdle);

    /**
     * Creates a Mailbox for processing messages that perform long computations or which may block the thread.
     *
     * @param initialBufferSize How big should the initial (per target Mailbox) buffer size be?
     * @param onIdle            The run method is called when the input queue is empty.
     * @return A new may block mailbox.
     */
    MayBlockMailbox createMayBlockMailbox(final int initialBufferSize,
                                          final Runnable onIdle);

    /**
     * Creates a mailbox that runs on an existing thread.
     *
     * @param _messageProcessor The run method is called when there are messages
     *                          to be processed.
     * @return The thread bounded mailbox.
     */
    ThreadBoundMailbox createThreadBoundMailbox(final Runnable _messageProcessor);

    /**
     * Adds an auto closeable, to be closed when the MailboxFactory closes.
     *
     * @param closeable The autoclosable to be added to the list.
     * @return True, if the list was updated.
     */
    boolean addAutoClosable(final AutoCloseable closeable);

    /**
     * Remove an auto closeable from the list of closables.
     *
     * @param closeable The autoclosable to be removed from the list.
     * @return True, if the list was updated.
     */
    boolean removeAutoClosable(final AutoCloseable closeable);

    /**
     * Returns true if close() has been called already.
     *
     * @return true if close() has already been called.
     */
    boolean isClosing();

    /**
     * Assigns the effectively final properties set manager.
     * Once assigned, it can not be updated.
     *
     * @param _properties The properties set manager.
     */
    void setProperties(final Properties _properties);

    /**
     * Returns the property set manager.
     *
     * @return The properties set manager.
     */
    Properties getProperties();
}
