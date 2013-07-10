package org.agilewiki.jactor2.util;

import org.agilewiki.jactor2.impl.DefaultMailboxFactoryImpl;

/**
 * A Mailbox factory
 */
public final class UtilMailboxFactory
        extends DefaultMailboxFactoryImpl {

    /**
     * Create a mailbox factory with the default thread pool size for mailboxes that may block and
     * a second thread pool for the non-blocking mailboxes
     * with a size equal to the number of hardware threads + 1.
     */
    public UtilMailboxFactory() {
        super();
    }

    /**
     * Create a mailbox factory with one or two threadpools.
     * If the mayBlockThreadCount is == 0, then a common thread pool is created
     * with a size equal to the number of hardware threads + 1.
     * If the mayBlockThreadCount is < 0, then a common thread pool is created
     * with a size = - mayBlockThreadCount.
     * Otherwise a non-blocking thread pool is created
     * with a size = - mayBlockThreadCount and a may-block thread pool is created
     * with a size = mayBlockThreadCount.
     *
     * @param mayBlockThreadCount    The thread pool size for mailboxes that may block.
     */
    public UtilMailboxFactory(final int mayBlockThreadCount) {
        super(mayBlockThreadCount);
    }
}
