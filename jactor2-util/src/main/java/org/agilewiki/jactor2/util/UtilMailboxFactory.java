package org.agilewiki.jactor2.util;

import org.agilewiki.jactor2.impl.DefaultMailboxFactory;

/**
 * A Mailbox factory
 */
public final class UtilMailboxFactory
        extends DefaultMailboxFactory {

    /**
     * Create a mailbox factory with the default thread pool size for mailboxes.
     */
    public UtilMailboxFactory() {
        super();
    }

    /**
     * Create a mailbox factory and a threadpools.
     *
     * @param _threadCount The thread pool size for mailboxes.
     */
    public UtilMailboxFactory(final int _threadCount) {
        super(_threadCount);
    }
}
