package org.agilewiki.jactor2.core.processing;

import org.agilewiki.jactor2.core.threading.ModuleContext;
import org.agilewiki.jactor2.core.threading.MigrationException;
import org.agilewiki.jactor2.core.messaging.ExceptionHandler;
import org.agilewiki.jactor2.core.messaging.Message;
import org.agilewiki.jactor2.core.messaging.MessageSource;
import org.slf4j.Logger;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Base class for message processors.
 */
abstract public class MessageProcessorBase implements MessageProcessor, MessageSource, AutoCloseable {

    /**
     * MessageProcessor logger.
     */
    protected final Logger log;

    /**
     * The context of this message processor.
     */
    protected final ModuleContext moduleContext;

    /**
     * The inbox, implemented as a local queue and a concurrent queue.
     */
    protected final Inbox inbox;

    /**
     * Holds the buffered messages until they are passed as blocks to their
     * various destinations.
     */
    protected final Outbox outbox;

    /**
     * The currently active exception handler.
     */
    private ExceptionHandler exceptionHandler;

    /**
     * The request or signal message being processed.
     */
    private Message currentMessage;

    /**
     * Create a message processor.
     *
     * @param _moduleContext             The context of this message processor.
     * @param _initialBufferSize     Initial size of the outbox for each unique message destination.
     * @param _initialLocalQueueSize The initial number of slots in the local queue.
     */
    public MessageProcessorBase(final ModuleContext _moduleContext,
                                final int _initialBufferSize,
                                final int _initialLocalQueueSize) {
        moduleContext = _moduleContext;
        inbox = createInbox(_initialLocalQueueSize);
        log = _moduleContext.getMessageProcessorLogger();
        outbox = new Outbox(moduleContext, _initialBufferSize);
        _moduleContext.addAutoClosable(this);
    }

    /**
     * Create the appropriate type of inbox.
     *
     * @param _initialLocalQueueSize The initial number of slots in the local queue.
     * @return An inbox.
     */
    abstract protected Inbox createInbox(int _initialLocalQueueSize);

    /**
     * Returns the message processor logger.
     *
     * @return The message processor logger.
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
     * (This method is not thread safe and must be called on the message processor's thread.)
     *
     * @return True if there is a message in the inbox that can be processed.
     */
    public final boolean hasWork() {
        return inbox.hasWork();
    }

    /**
     * Returns true when a message has been passed from another thread.
     * @return True when a message has been passed from another thread.
     */
    public boolean hasConcurrent() {
        return inbox.hasConcurrent();
    }

    @Override
    public final boolean isInboxEmpty() {
        return inbox.isEmpty();
    }

    @Override
    public void close() {
        try {
            outbox.close();
        } catch (Exception e) {
        }
        try {
            inbox.close();
        } catch (Exception e) {
        }
    }

    @Override
    public final ExceptionHandler setExceptionHandler(
            final ExceptionHandler _handler) {
        if (!isRunning())
            throw new IllegalStateException(
                    "Attempt to set an exception handler on an idle message processor");
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
     * Add a message directly to the input queue of a MessageProcessor.
     *
     * @param _message A message.
     * @param _local   True when the current thread is bound to the message processor.
     */
    public void unbufferedAddMessage(final Message _message, final boolean _local)
            throws Exception {
        if (moduleContext.isClosing()) {
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
        if (moduleContext.isClosing()) {
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
     * Buffers a message in the sending message processor for sending later.
     *
     * @param _message Message to be buffered.
     * @param _target  The message processor that should eventually receive this message
     * @return True if the message was buffered.
     */
    public boolean buffer(final Message _message, final MessageProcessor _target) {
        return outbox.buffer(_message, _target);
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
                                       final MessageProcessor _responseSource) {
        try {
            unbufferedAddMessage(_message, this == _responseSource);
        } catch (final Throwable t) {
            log.error("unable to add response message", t);
        }
    }

    @Override
    public ModuleContext getModuleContext() {
        return moduleContext;
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
     * Returns true, if this message processor is actively processing messages.
     */
    abstract public boolean isRunning();

    /**
     * Returns true when there is code to be executed when the inbox is emptied.
     *
     * @return True when there is code to be executed when the inbox is emptied.
     */
    abstract public boolean isIdler();


}
