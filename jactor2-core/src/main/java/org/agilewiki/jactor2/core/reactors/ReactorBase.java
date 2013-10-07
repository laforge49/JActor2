package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.blades.ExceptionHandler;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.messages.Message;
import org.agilewiki.jactor2.core.messages.MessageSource;
import org.slf4j.Logger;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Base class for targetReactor.
 */
abstract public class ReactorBase implements Reactor, MessageSource, AutoCloseable {

    /**
     * Reactor logger.
     */
    protected final Logger log;

    /**
     * The facility of this targetReactor.
     */
    protected final Facility facility;

    /**
     * The inbox, implemented as a doLocal queue and a concurrent queue.
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
     * Create a targetReactor.
     *
     * @param _facility              The facility of this targetReactor.
     * @param _initialBufferSize     Initial size of the outbox for each unique message destination.
     * @param _initialLocalQueueSize The initial number of slots in the doLocal queue.
     */
    public ReactorBase(final Facility _facility,
                       final int _initialBufferSize,
                       final int _initialLocalQueueSize) throws Exception {
        facility = _facility;
        inbox = createInbox(_initialLocalQueueSize);
        log = _facility.getMessageProcessorLogger();
        outbox = new Outbox(facility, _initialBufferSize);
        addAutoClose();
    }

    /**
     * Add to the facility's AutoClose set.
     */
    protected void addAutoClose() throws Exception {
        facility.addAutoClosableSReq(this).signal();
    }

    /**
     * Create the appropriate type of inbox.
     *
     * @param _initialLocalQueueSize The initial number of slots in the doLocal queue.
     * @return An inbox.
     */
    abstract protected Inbox createInbox(int _initialLocalQueueSize);

    /**
     * Returns the targetReactor logger.
     *
     * @return The targetReactor logger.
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
     * (This method is not thread safe and must be called on the targetReactor's thread.)
     *
     * @return True if there is a message in the inbox that can be processed.
     */
    public final boolean hasWork() {
        return inbox.hasWork();
    }

    /**
     * Returns true when a message has been passed from another thread.
     *
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
                    "Attempt to set an exception handler on an idle targetReactor");
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
     * Add a message directly to the input queue of a Reactor.
     *
     * @param _message A message.
     * @param _local   True when the current thread is bound to the targetReactor.
     */
    public void unbufferedAddMessage(final Message _message, final boolean _local)
            throws Exception {
        if (facility.isClosing()) {
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
        if (facility.isClosing()) {
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
     * Buffers a message in the sending targetReactor for sending later.
     *
     * @param _message Message to be buffered.
     * @param _target  The targetReactor that should eventually receive this message
     * @return True if the message was buffered.
     */
    public boolean buffer(final Message _message, final Reactor _target) {
        return outbox.buffer(_message, _target);
    }

    /**
     * Process the event/request/response message by calling its eval method.
     *
     * @param _message The message to be processed.
     */
    protected void processMessage(final Message _message) {
        _message.eval();
    }

    /**
     * Called when all pending messages have been processed.
     */
    abstract protected void notBusy() throws Exception;

    @Override
    public final void incomingResponse(final Message _message,
                                       final Reactor _responseSource) {
        try {
            ReactorBase responseSource = (ReactorBase) _responseSource;
            boolean local = this == _responseSource;
            if (local || _responseSource == null || !responseSource.buffer(_message, this))
                unbufferedAddMessage(_message, local);
        } catch (final Throwable t) {
            log.error("unable to add response message", t);
        }
    }

    @Override
    public Facility getFacility() {
        return facility;
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
     * Returns true, if this targetReactor is actively processing messages.
     */
    abstract public boolean isRunning();

    /**
     * Returns true when there is code to be executed when the inbox is emptied.
     *
     * @return True when there is code to be executed when the inbox is emptied.
     */
    abstract public boolean isIdler();


}
