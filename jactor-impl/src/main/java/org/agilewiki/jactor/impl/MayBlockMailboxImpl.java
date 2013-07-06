package org.agilewiki.jactor.impl;

import org.agilewiki.jactor.api.MayBlockMailbox;
import org.slf4j.Logger;

public class MayBlockMailboxImpl extends BaseMailbox implements MayBlockMailbox {

    public MayBlockMailboxImpl(Runnable _onIdle, JAMailboxFactory factory, MessageQueue messageQueue, Logger _log, int _initialBufferSize) {
        super(true, _onIdle, factory, messageQueue, _log, _initialBufferSize);
    }

    @Override
    protected void afterAdd() throws Exception {
        /**
         * The compareAndSet method is a moderately expensive operation,
         * so we use a guard expression to reduce the number of times it is called.
         */
        if (threadReference.get() == null && inbox.isNonEmpty()) {
            mailboxFactory.submit(this, true);
        }
    }
}
