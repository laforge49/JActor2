package org.agilewiki.jactor2.impl;

import org.agilewiki.jactor2.api.MayBlockMailbox;
import org.slf4j.Logger;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class MayBlockMailboxImpl extends BaseMailbox implements MayBlockMailbox {

    public MayBlockMailboxImpl(Runnable _onIdle, JAMailboxFactory factory, MessageQueue messageQueue, Logger _log, int _initialBufferSize) {
        super(_onIdle, factory, messageQueue, _log, _initialBufferSize);
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

    @Override
    protected void afterProcessMessage(final boolean request,
                                       final Message message) {
        try {
            flush(true);
        } catch (final MigrateException me) {
            throw me;
        } catch (Exception e) {
            log.error("Exception thrown by onIdle", e);
        }
    }

    @Override
    public boolean flush(boolean _mayMigrate) throws Exception {
        boolean result = false;
        if (sendBuffer != null) {
            final Iterator<Map.Entry<JAMailbox, ArrayDeque<Message>>> iter = sendBuffer
                    .entrySet().iterator();
            while (iter.hasNext()) {
                result = true;
                final Map.Entry<JAMailbox, ArrayDeque<Message>> entry = iter.next();
                final JAMailbox target = entry.getKey();
                final ArrayDeque<Message> messages = entry.getValue();
                iter.remove();
                if (!iter.hasNext() &&
                        _mayMigrate &&
                        getMailboxFactory() == target.getMailboxFactory() &&
                        !target.isRunning()) {
                    Thread currentThread = threadReference.get();
                    AtomicReference<Thread> targetThreadReference = target.getThreadReference();
                    if (targetThreadReference.get() == null &&
                            targetThreadReference.compareAndSet(null, currentThread)) {
                        while (!messages.isEmpty()) {
                            Message m = messages.poll();
                            target.addUnbufferedMessage(m, true);
                        }
                        throw new MigrateException(target);
                    }
                }
                target.addUnbufferedMessages(messages);
            }
        }
        return result;
    }
}
