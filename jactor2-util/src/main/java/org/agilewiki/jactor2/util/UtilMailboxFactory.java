package org.agilewiki.jactor2.util;

import org.agilewiki.jactor2.impl.DefaultMailboxFactoryImpl;

/**
 * A Mailbox factory
 */
public final class UtilMailboxFactory
        extends DefaultMailboxFactoryImpl {

    /**
     * Create a mailbox factory with the default thread pool size for mailboxes that may block.
     */
    public UtilMailboxFactory() {
        super();
    }

    /**
     * Create a mailbox factory.
     *
     * @param maxBlockingThreads    The thread pool size for mailboxes that may block.
     */
    public UtilMailboxFactory(final int maxBlockingThreads) {
        super(maxBlockingThreads);
    }
}
