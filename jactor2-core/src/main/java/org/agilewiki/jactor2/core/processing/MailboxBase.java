package org.agilewiki.jactor2.core.processing;

import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.context.MigrationException;
import org.agilewiki.jactor2.core.messaging.ExceptionHandler;
import org.agilewiki.jactor2.core.messaging.Message;
import org.agilewiki.jactor2.core.messaging.MessageSource;
import org.slf4j.Logger;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Base class for mailboxes.
 */
abstract public class MailboxBase implements Mailbox, MessageSource, AutoCloseable {

    /**
     * Default initial (per target Mailbox) buffer.
     */
    public final static int INITIAL_OUTBOX_SIZE = 16;

    /**
     * Mailbox logger.
     */
    protected final Logger log;

    /**
     * The context of this processing.
     */
    protected final JAContext jaContext;

    /**
     * The inbox, implemented as a local queue and a concurrent queue.
     */
    protected final Inbox inbox;

    /**
     * Initial size of the outbox for each unique message destination.
     */
    private final int initialOutboxSize;

    /**
     * A table of outboxes, one for each unique message destination.
     */
    protected Map<MailboxBase, ArrayDeque<Message>> sendBuffer;

    /**
     * The currently active exception handler.
     */
    private ExceptionHandler exceptionHandler;

    /**
     * The request or signal message being processed.
     */
    private Message currentMessage;

    /**
     * Create a processing.
     *
     * @param _jaContext             The context of this processing.
     * @param _initialOutboxSize     Initial size of the outbox for each unique message destination.
     * @param _initialLocalQueueSize The initial number of slots in the local queue.
     */
    public MailboxBase(final JAContext _jaContext,
                       final int _initialOutboxSize,
                       final int _initialLocalQueueSize) {
        jaContext = _jaContext;
        inbox = createInbox(_initialLocalQueueSize);
        log = _jaContext.getMailboxLogger();
        initialOutboxSize = _initialOutboxSize;
        _jaContext.addAutoClosable(this);
    }

    /**
     * Create the appropriate type of inbox.
     *
     * @param _initialLocalQueueSize The initial number of slots in the local queue.
     * @return An inbox.
     */
    abstract protected Inbox createInbox(int _initialLocalQueueSize);

    /**
     * Returns the processing logger.
     *
     * @return The processing logger.
     */
    public final Logger getLogger() {
        return log;
    }

    /**
     * Returns the message currently being processed.
     *
     * @return The message currently being processed.
     */
    public final Message getCurrentMessage() {
        return currentMessage;
    }

    /**
     * Identify the message currently being processed.
     *
     * @param _message The message currently being processed.
     */
    public final void setCurrentMessage(Message _message) {
        currentMessage = _message;
    }

    /**
     * Returns true when there is a message in the inbox that can be processed.
     * (This method is not thread safe and must be called on the processing's thread.)
     *
     * @return True if there is a message in the inbox that can be processed.
     */
    public final boolean hasWork() {
        return inbox.hasWork();
    }

    @Override
    public final boolean isInboxEmpty() {
        return inbox.isEmpty();
    }

    @Override
    public void close() throws Exception {
        if (sendBuffer == null)
            return;
        final Iterator<Entry<MailboxBase, ArrayDeque<Message>>> iter = sendBuffer
                .entrySet().iterator();
        while (iter.hasNext()) {
            final Entry<MailboxBase, ArrayDeque<Message>> entry = iter.next();
            final MailboxBase target = entry.getKey();
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
                    "Attempt to set an exception handler on an idle processing");
        final ExceptionHandler rv = this.exceptionHandler;
        this.exceptionHandler = _handler;
        return rv;
    }

    /**
     * The current exception handler.
     *
     * @return The current exception handler, or null.
     */
    public final ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    /**
     * Add a message directly to the input queue of a Mailbox.
     *
     * @param _message A message.
     * @param _local   True when the current thread is bound to the processing.
     */
    public void unbufferedAddMessage(final Message _message, final boolean _local)
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

    /**
     * Adds messages directly to the queue.
     *
     * @param _messages Previously buffered messages.
     */
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

    /**
     * Buffers a message in the sending processing for sending later.
     *
     * @param _message Message to be buffered.
     * @param _target  The processing that should eventually receive this message
     * @return True if the message was buffered.
     */
    public boolean buffer(final Message _message, final Mailbox _target) {
        if (jaContext.isClosing())
            return false;
        ArrayDeque<Message> buffer;
        if (sendBuffer == null) {
            sendBuffer = new IdentityHashMap<MailboxBase, ArrayDeque<Message>>();
            buffer = null;
        } else {
            buffer = sendBuffer.get(_target);
        }
        if (buffer == null) {
            buffer = new ArrayDeque<Message>(initialOutboxSize);
            sendBuffer.put((MailboxBase) _target, buffer);
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
                if (hasWork())
                    continue;
                return;
            }
            processMessage(message);
        }
    }

    /**
     * Process the event/request/response message by calling its eval method.
     *
     * @param _message The message to be processed.
     */
    protected void processMessage(final Message _message) {
        _message.eval(this);
    }

    /**
     * Called when all pending messages have been processed.
     */
    abstract protected void notBusy() throws Exception;

    @Override
    public final void incomingResponse(final Message _message,
                                       final Mailbox _responseSource) {
        try {
            unbufferedAddMessage(_message, this == _responseSource ||
                    (_responseSource != null && this == _responseSource));
        } catch (final Throwable t) {
            log.error("unable to add response message", t);
        }
    }

    @Override
    public JAContext getJAContext() {
        return jaContext;
    }

    /**
     * Signals the start of a request.
     */
    public void requestBegin() {
        inbox.requestBegin();
    }

    /**
     * Signals that a request has completed.
     */
    public void requestEnd() {
        inbox.requestEnd();
    }

    /**
     * Returns the atomic reference to the current thread.
     *
     * @return The atomic reference to the current thread.
     */
    abstract public AtomicReference<Thread> getThreadReference();

    /**
     * Returns true, if this processing is currently processing messages.
     */
    abstract public boolean isRunning();

    /**
     * Returns true when there is code to be executed when the inbox is emptied.
     *
     * @return True when there is code to be executed when the inbox is emptied.
     */
    abstract public boolean isIdler();


}
