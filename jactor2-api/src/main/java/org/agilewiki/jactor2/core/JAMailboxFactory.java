package org.agilewiki.jactor2.core;

/**
 * The extended MailboxFactory interface for use in the implementation.
 */
public interface JAMailboxFactory extends MailboxFactory {

    /**
     * Submit a mailbox for subsequent execution.
     *
     * @param _mailbox The mailbox to be run.
     */
    void submit(final UnboundMailbox _mailbox)
            throws Exception;
}
