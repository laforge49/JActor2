package org.agilewiki.jactor.impl;

import org.agilewiki.jactor.api.ThreadBoundMailbox;
import org.slf4j.Logger;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Map;

public class ThreadBoundMailboxImpl extends BaseMailbox implements ThreadBoundMailbox {

    private final Runnable messageProcessor;

    public ThreadBoundMailboxImpl(Runnable _messageProcessor,
                                  JAMailboxFactory factory,
                                  MessageQueue messageQueue,
                                  Logger _log,
                                  int _initialBufferSize) {
        super(null, factory, messageQueue, _log, _initialBufferSize);
        messageProcessor = _messageProcessor;
    }

    /**
     * Returns true, if this message source is currently processing messages.
     */
    @Override
    public final boolean isRunning() {
        return true;
    }

    @Override
    protected void afterAdd() throws Exception {
        messageProcessor.run();
    }

    /**
     * Flushes buffered messages, if any.
     * Returns true if there was any.
     */
    public final boolean flush(boolean mayMigrate) throws Exception {
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
                target.addUnbufferedMessages(messages);
            }
        }
        return result;
    }
}
