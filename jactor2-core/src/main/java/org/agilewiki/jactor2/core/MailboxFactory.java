package org.agilewiki.jactor2.core;

/**
 * MailboxFactory creates NonBlocking, Atomic and ThreadBound mailboxes.
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
     * @param _initialBufferSize How big should the initial (per target Mailbox) buffer size be?
     * @return A new non-blocking mailbox.
     */
    NonBlockingMailbox createNonBlockingMailbox(final int _initialBufferSize);

    /**
     * Creates a Mailbox which is only used to process non-blocking requests.
     *
     * @param _onIdle The run method is called when the input queue is empty.
     * @return A new non-blocking mailbox.
     */
    NonBlockingMailbox createNonBlockingMailbox(final Runnable _onIdle);

    /**
     * Creates a Mailbox which is only used to process non-blocking requests.
     *
     * @param _initialBufferSize How big should the initial (per target Mailbox) buffer size be?
     * @param _onIdle            The run method is called when the input queue is empty.
     * @return A new non-blocking mailbox.
     */
    NonBlockingMailbox createNonBlockingMailbox(final int _initialBufferSize,
                                                final Runnable _onIdle);

    /**
     * Creates a Mailbox for processing messages that perform long computations
     * or which may block the thread, or when requests must be processed atomically.
     *
     * @return A new atomic mailbox.
     */
    AtomicMailbox createAtomicMailbox();

    /**
     * Creates a Mailbox for processing messages that perform long computations
     * or which may block the thread, or when requests must be processed atomically.
     *
     * @param _initialBufferSize How big should the initial (per target Mailbox) buffer size be?
     * @return A new atomic mailbox.
     */
    AtomicMailbox createAtomicMailbox(final int _initialBufferSize);

    /**
     * Creates a Mailbox for processing messages that perform long computations
     * or which may block the thread, or when requests must be processed atomically.
     *
     * @param _onIdle The run method is called when the input queue is empty.
     * @return A new atomic mailbox.
     */
    AtomicMailbox createAtomicMailbox(final Runnable _onIdle);

    /**
     * Creates a Mailbox for processing messages that perform long computations
     * or which may block the thread, or when requests must be processed atomically.
     *
     * @param _initialBufferSize How big should the initial (per target Mailbox) buffer size be?
     * @param _onIdle            The run method is called when the input queue is empty.
     * @return A new atomic mailbox.
     */
    AtomicMailbox createAtomicMailbox(final int _initialBufferSize,
                                      final Runnable _onIdle);

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
     * @param _closeable The autoclosable to be added to the list.
     * @return True, if the list was updated.
     */
    boolean addAutoClosable(final AutoCloseable _closeable);

    /**
     * Remove an auto closeable from the list of closables.
     *
     * @param _closeable The autoclosable to be removed from the list.
     * @return True, if the list was updated.
     */
    boolean removeAutoClosable(final AutoCloseable _closeable);

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
