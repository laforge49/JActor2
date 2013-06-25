package org.agilewiki.jactor.util;

import org.agilewiki.jactor.impl.DefaultMailboxFactoryImpl;
import org.agilewiki.jactor.impl.JAMailbox;
import org.agilewiki.jactor.impl.MessageQueue;
import org.slf4j.Logger;

/**
 * A Mailbox factory
 * @param <M>    The type of mailbox.
 */
public final class UtilMailboxFactory<M extends JAMailbox>
        extends DefaultMailboxFactoryImpl<M> {

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
