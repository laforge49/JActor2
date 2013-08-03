package org.agilewiki.jactor2.core.mailbox;

import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.messaging.Message;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class ThreadBoundMailbox extends MailboxBase {

    private final Runnable messageProcessor;

    public ThreadBoundMailbox(JAContext _jaContext, Runnable _messageProcessor) {
        super(_jaContext,
                _jaContext.getInitialBufferSize(),
                _jaContext.getInitialLocalMessageQueueSize());
        messageProcessor = _messageProcessor;
    }

    public ThreadBoundMailbox(JAContext _jaContext,
                              int _initialBufferSize,
                              final int _initialLocalQueueSize,
                              Runnable _messageProcessor) {
        super(_jaContext, _initialBufferSize, _initialLocalQueueSize);
        messageProcessor = _messageProcessor;
    }

    @Override
    protected void notBusy() throws Exception {
        flush();
    }

    /**
     * Returns true, if this message source is currently processing messages.
     */
    @Override
    public final boolean isRunning() {
        return true;
    }

    @Override
    public AtomicReference<Thread> getThreadReference() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isIdler() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Inbox createInbox(int _initialLocalQueueSize) {
        return new NonBlockingInbox(_initialLocalQueueSize);
    }

    @Override
    protected void afterAdd() throws Exception {
        messageProcessor.run();
    }

    /**
     * Flushes buffered messages, if any.
     * Returns true if there was any.
     */
    public final boolean flush() throws Exception {
        boolean result = false;
        if (sendBuffer != null) {
            final Iterator<Map.Entry<MailboxBase, ArrayDeque<Message>>> iter = sendBuffer
                    .entrySet().iterator();
            while (iter.hasNext()) {
                result = true;
                final Map.Entry<MailboxBase, ArrayDeque<Message>> entry = iter.next();
                final MailboxBase target = entry.getKey();
                final ArrayDeque<Message> messages = entry.getValue();
                iter.remove();
                target.unbufferedAddMessages(messages);
            }
        }
        return result;
    }
}
