package org.agilewiki.pactor;

/**
 * MailboxFactory creates different types of mailboxes, including thread bound mailboxes.
 * This class is also responsible for closing everything down and managing a list of
 * auto closables to be called when MailboxFactory.close() is called.
 */
public interface MailboxFactory extends AutoCloseable {

    /**
     * Creates a Mailbox with a both commandeering and message buffering enabled.
     *
     * @return A new mailbox.
     */
    Mailbox createMailbox();

    /**
     * Creates a Mailbox with commandeering enabled.
     *
     * @param _disableCommandeering True when commandeering is to be disabled.
     * @return A new mailbox.
     */
    Mailbox createMailbox(final boolean _disableCommandeering);

    /**
     * Creates a Mailbox.
     *
     * @param _disableCommandeering True when commandeering is to be disabled.
     * @param _disableMessageBuffering True when message buffering is to be disabled.
     * @return A new mailbox.
     */
    Mailbox createMailbox(final boolean _disableCommandeering,
                          final boolean _disableMessageBuffering);

    /**
     * Create a mailbox that runs on an existing thread.
     * Sample usage:
     * <pre>
     *     public class CreateUiMailbox{
     *         private static Mailbox uiMailbox;
     *
     *         synchronized public static Mailbox get(MailboxFactory mailboxFactory) {
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
}
/*
class CreateUiMailbox{
         private static Mailbox uiMailbox;

         synchronized public static Mailbox get(MailboxFactory mailboxFactory) {
             if (uiMailbox == null) {
                 uiMailbox = mailboxFactory.createThreadBoundMailbox(new Runnable() {
                     public void run() {
                         SwingUtilities.invokeLater(uiMailbox);
                     }
                 });
             }
             return uiMailbox;
         }
}
*/