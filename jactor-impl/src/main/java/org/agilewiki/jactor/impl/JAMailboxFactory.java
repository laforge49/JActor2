package org.agilewiki.jactor.impl;

import org.agilewiki.jactor.api.MailboxFactory;

public interface JAMailboxFactory extends MailboxFactory {
    void submit(final Runnable task, final boolean willBlock) throws Exception;
}
