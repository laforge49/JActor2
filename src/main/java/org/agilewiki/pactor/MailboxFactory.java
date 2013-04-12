package org.agilewiki.pactor;

/**
 * MailboxFactory creates different types of mailboxes, including thread bound mailboxes.
 * This class is also responsible for closing everything down and managing a list of
 * auto closables to be called when MailboxFactory.close() is called.
 * <p>
 * Mailboxes should not normally process requests that are CPU intensive or block a thread.
 * But when that is the case,
 * the mailbox should be created with mapBlock set to true.
 * </p>
 */
public interface MailboxFactory extends AutoCloseable {

    /**
     * Creates a Mailbox which is only used to process non-blocking requests.
     *
     * @return A new mailbox.
     */
    Mailbox createMailbox();

    /**
     * Creates a Mailbox.
     *
     * @param mayBlock True when requests are CPU intensive or may block the thread.
     * @return A new mailbox.
     */
    Mailbox createMailbox(final boolean mayBlock);

    /**
     * Creates a Mailbox which is only used to process non-blocking requests.
     *
     * @param initialBufferSize How big should the initial (per target Mailbox) buffer size be?
     * @return A new mailbox.
     */
    Mailbox createMailbox(final int initialBufferSize);

    /**
     * Creates a Mailbox.
     *
     * @param mayBlock True when requests are CPU intensive or may block the thread.
     * @param initialBufferSize How big should the initial (per target Mailbox) buffer size be?
     * @return A new mailbox.
     */
    Mailbox createMailbox(final boolean mayBlock, final int initialBufferSize);

    /**
     * Creates a Mailbox.
     *
     * @param mayBlock True when requests are CPU intensive or may block the thread.
     * @param onIdle The run method is called when the input queue is empty.
     * @return A new mailbox.
     */
    Mailbox createMailbox(final boolean mayBlock, final Runnable onIdle);

    /**
     * Creates a Mailbox.
     *
     * @param mayBlock True when requests are CPU intensive or may block the thread.
     * @param initialBufferSize How big should the initial (per target Mailbox) buffer size be?
     * @param onIdle The run method is called when the input queue is empty.
     * @return A new mailbox.
     */
    Mailbox createMailbox(final boolean mayBlock, final int initialBufferSize,
            final Runnable onIdle);

    /**
     * Creates a mailbox that runs on an existing thread.
     * Sample usage:
     * <pre>
     *     public class CreateUiMailbox{
     *         private Mailbox uiMailbox;
     *
     *         synchronized public Mailbox get(MailboxFactory mailboxFactory) {
     *             if (uiMailbox == null) {
     *                 uiMailbox = mailboxFactory.createThreadBoundMailbox(new Runnable() {
     *                     public void run() {
     *                         SwingUtilities.invokeLater(uiMailbox);
     *                     }
     *                 });
     *             }
     *             return uiMailbox;
     *         }
     *     }
     * The _messageProcessor.run method typically will call
     * SwingUtilities.invokeLater(mailbox) to process pending messages on the UI thread.
     * </pre>
     *
     * @param _messageProcessor The run method is called when there are messages
     *                          to be processed.
     * @return The thread bounded mailbox.
     */
    Mailbox createThreadBoundMailbox(final Runnable _messageProcessor);

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
     * @param properties The properties set manager.
     */
    void setProperties(Properties properties);

    /**
     * Returns the property set manager.
     * @return The properties set manager.
     */
    Properties getProperties();
}
