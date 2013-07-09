package org.agilewiki.jactor2.impl;

import org.agilewiki.jactor2.api.Mailbox;
import org.agilewiki.jactor2.api.NonBlockingMailbox;
import org.slf4j.Logger;

public class NonBlockingMailboxImpl extends BaseMailbox implements NonBlockingMailbox {

    public NonBlockingMailboxImpl(Runnable _onIdle, JAMailboxFactory factory, MessageQueue messageQueue, Logger _log, int _initialBufferSize) {
        super(_onIdle, factory, messageQueue, _log, _initialBufferSize);
    }

    @Override
    protected void afterAdd() throws Exception {
        /**
         * The compareAndSet method is a moderately expensive operation,
         * so we use a guard expression to reduce the number of times it is called.
         */
        if (threadReference.get() == null && inbox.isNonEmpty()) {
            mailboxFactory.submit(this, false);
        }
    }

    @Override
    public void addUnbufferedMessage(final Message message, final boolean local)
            throws Exception {
        if (mailboxFactory.isClosing()) {
            if (message.isForeign() && message.isResponsePending())
                try {
                    message.close();
                } catch (final Throwable t) {
                }
            return;
        }
        if (local)
            inbox.offer(true, message);
        else if (message.isForeign() || isRunning())
            inbox.offer(false, message);
        else {
            Mailbox activeMailbox = message.activeMailbox();
            if (activeMailbox == null || !(activeMailbox instanceof NonBlockingMailbox))
                inbox.offer(false, message);
            else {
                Thread activeThread = ((JAMailbox)activeMailbox).getThreadReference().get();
                if (!threadReference.compareAndSet(null, activeThread))
                    inbox.offer(false, message);
                else {
                    try {
                        inbox.offer(true, message);
                        run();
                    } finally {
                        threadReference.set(null);
                    }
                }
            }
        }
        afterAdd();
    }
}
