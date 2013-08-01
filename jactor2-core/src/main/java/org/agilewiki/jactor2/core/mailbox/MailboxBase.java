package org.agilewiki.jactor2.core.mailbox;

import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.context.MigrationException;
import org.agilewiki.jactor2.core.messaging.ExceptionHandler;
import org.agilewiki.jactor2.core.messaging.Message;
import org.slf4j.Logger;

import java.util.*;
import java.util.Map.Entry;

/**
 * Base class for mailboxes.
 */
abstract public class MailboxBase implements Mailbox {

    /**
     * Mailbox logger.
     */
    protected final Logger log;

    /**
     * The factory of this mailbox.
     */
    protected final JAContext jaContext;

    /**
     * The inbox, implemented as a local queue and a concurrent queue.
     */
    protected final Inbox inbox;

    /**
     * Initial size of the outbox for each unique message destination.
     */
    private final int initialBufferSize;

    /**
     * A table of outboxes, one for each unique message destination.
     */
    protected Map<Mailbox, ArrayDeque<Message>> sendBuffer;

    /**
     * The currently active exception handler.
     */
    private ExceptionHandler exceptionHandler;

    /**
     * The request or signal message being processed.
     */
    private Message currentMessage;

    /**
     * Create a mailbox.
     *
     * @param _jaContext             The factory of this object.
     * @param _initialBufferSize     Initial size of the outbox for each unique message destination.
     * @param _initialLocalQueueSize The initial number of slots in the local queue.
     */
    public MailboxBase(final JAContext _jaContext,
                       final int _initialBufferSize,
                       final int _initialLocalQueueSize) {
        jaContext = _jaContext;
        inbox = createInbox(_initialLocalQueueSize);
        log = _jaContext.getMailboxLogger();
        initialBufferSize = _initialBufferSize;
        _jaContext.addAutoClosable(this);
    }

    abstract protected Inbox createInbox(int _initialLocalQueueSize);

    @Override
    public final Logger getLogger() {
        return log;
    }

    @Override
    public final Message getCurrentMessage() {
        return currentMessage;
    }

    @Override
    public final void setCurrentMessage(Message message) {
        currentMessage = message;
    }

    @Override
    public final boolean hasWork() {
        return inbox.hasWork();
    }

    @Override
    public final boolean isIdle() {
        return inbox.isIdle();
    }

    @Override
    public void close() throws Exception {
        if (sendBuffer == null)
            return;
        final Iterator<Entry<Mailbox, ArrayDeque<Message>>> iter = sendBuffer
                .entrySet().iterator();
        while (iter.hasNext()) {
            final Entry<Mailbox, ArrayDeque<Message>> entry = iter.next();
            final Mailbox target = entry.getKey();
            if (target.getJAContext() != jaContext) {
                final ArrayDeque<Message> messages = entry.getValue();
                iter.remove();
                target.unbufferedAddMessages(messages);
            } else
                iter.remove();
        }
        while (true) {
            final Message message = inbox.poll();
            if (message == null)
                return;
            if (message.isForeign() && message.isResponsePending())
                try {
                    message.close();
                } catch (final Throwable t) {
                }
        }
    }

    @Override
    public final ExceptionHandler setExceptionHandler(
            final ExceptionHandler _handler) {
        if (!isRunning())
            throw new IllegalStateException(
                    "Attempt to set an exception handler on an idle mailbox");
        final ExceptionHandler rv = this.exceptionHandler;
        this.exceptionHandler = _handler;
        return rv;
    }

    @Override
    public final ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public void unbufferedAddMessages(final Message _message, final boolean _local)
            throws Exception {
        if (jaContext.isClosing()) {
            if (_message.isForeign() && _message.isResponsePending())
                try {
                    _message.close();
                } catch (final Throwable t) {
                }
            return;
        }
        inbox.offer(_local, _message);
        afterAdd();
    }

    @Override
    public void unbufferedAddMessages(final Queue<Message> _messages)
            throws Exception {
        if (jaContext.isClosing()) {
            final Iterator<Message> itm = _messages.iterator();
            while (itm.hasNext()) {
                final Message message = itm.next();
                if (message.isForeign() && message.isResponsePending())
                    try {
                        message.close();
                    } catch (final Throwable t) {
                    }
            }
            return;
        }
        inbox.offer(_messages);
        afterAdd();
    }

    /**
     * Called after adding some message(s) to the inbox.
     */
    abstract protected void afterAdd() throws Exception;

    @Override
    public boolean buffer(final Message _message, final Mailbox _target) {
        if (jaContext.isClosing())
            return false;
        ArrayDeque<Message> buffer;
        if (sendBuffer == null) {
            sendBuffer = new IdentityHashMap<Mailbox, ArrayDeque<Message>>();
            buffer = null;
        } else {
            buffer = sendBuffer.get(_target);
        }
        if (buffer == null) {
            buffer = new ArrayDeque<Message>(initialBufferSize);
            sendBuffer.put(_target, buffer);
        }
        buffer.add(_message);
        return true;
    }

    @Override
    public void run() {
        while (true) {
            final Message message = inbox.poll();
            if (message == null) {
                try {
                    notBusy();
                } catch (final MigrationException me) {
                    throw me;
                } catch (Exception e) {
                    log.error("Exception thrown by onIdle", e);
                }
                if (inbox.hasWork())
                    continue;
                return;
            }
            processMessage(message);
        }
    }

    protected void processMessage(final Message message) {
        message.eval(this);
    }

    /**
     * Called when all pending messages have been processed.
     */
    abstract protected void notBusy() throws Exception;

    @Override
    public final void incomingResponse(final Message _message,
                                       final Mailbox _responseSource) {
        try {
            unbufferedAddMessages(_message, this == _responseSource ||
                    (_responseSource != null && this == _responseSource));
        } catch (final Throwable t) {
            log.error("unable to add response message", t);
        }
    }

    @Override
    public JAContext getJAContext() {
        return jaContext;
    }

    @Override
    public void requestBegin() {
        inbox.requestBegin();
    }

    @Override
    public void requestEnd() {
        inbox.requestEnd();
    }
}
