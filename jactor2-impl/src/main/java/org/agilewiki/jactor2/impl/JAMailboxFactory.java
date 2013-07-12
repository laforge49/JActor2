package org.agilewiki.jactor2.impl;

import org.agilewiki.jactor2.api.MailboxFactory;

public interface JAMailboxFactory extends MailboxFactory {
    void submit(final UnboundMailbox mailbox, final boolean willBlock) throws Exception;
}
