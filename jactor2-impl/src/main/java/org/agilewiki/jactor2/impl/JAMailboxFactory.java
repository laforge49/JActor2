package org.agilewiki.jactor2.impl;

import org.agilewiki.jactor2.api.MailboxFactory;

/**
 * The extended MailboxFactory interface for use in the implementation.
 */
public interface JAMailboxFactory extends MailboxFactory {

    /**
     * Submit a mailbox for subsequent execution.
     * The _mayBlock parameter is used to select the appropriate thread pool.
     *
     * @param _mailbox  The mailbox to be run.
     * @param _mayBlock True if the mailbox might block the thread.
     */
    void submit(final UnboundMailbox _mailbox, final boolean _mayBlock)
            throws Exception;
}
