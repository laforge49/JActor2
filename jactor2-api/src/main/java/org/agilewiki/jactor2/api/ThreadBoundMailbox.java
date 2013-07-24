package org.agilewiki.jactor2.api;

/**
 * A mailbox bound to an external thread.
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
 * </pre>
 * The _messageProcessor.run method typically will call
 * SwingUtilities.invokeLater(mailbox) to process pending messages on the UI thread.
 */
public interface ThreadBoundMailbox extends Mailbox {
}
