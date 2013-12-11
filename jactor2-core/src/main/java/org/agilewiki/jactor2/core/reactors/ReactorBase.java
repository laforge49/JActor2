package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.blades.ExceptionHandler;
import org.agilewiki.jactor2.core.facilities.*;
import org.agilewiki.jactor2.core.messages.Message;
import org.agilewiki.jactor2.core.messages.MessageSource;
import org.agilewiki.jactor2.core.messages.RequestBase;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.util.MessageCloser;
import org.agilewiki.jactor2.core.util.Recovery;
import org.slf4j.Logger;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Base class for targetReactor.
 */
abstract public class ReactorBase extends MessageCloser implements Reactor, MessageSource {

    private volatile boolean running;

    private SchedulableSemaphore timeoutSemaphore;

    public Recovery recovery;

    public Scheduler scheduler;

    public volatile long messageStartTimeMillis;

    /**
     * Reactor logger.
     */
    protected final Logger log;

    /**
     * The facility of this targetReactor.
     */
    protected final Facility facility;

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
     * Set when the reactor reaches end-of-life.
     * Can only be updated via a request to the reactor.
     */
    private volatile boolean shuttingDown;

    private boolean startClosing;

    /**
     * Create a targetReactor.
     *
     * @param _facility              The facility of this targetReactor.
     * @param _initialBufferSize     Initial size of the outbox for each unique message destination.
     * @param _initialLocalQueueSize The initial number of slots in the local queue.
     */
    public ReactorBase(final Facility _facility, final int _initialBufferSize,
                       final int _initialLocalQueueSize) throws Exception {
        facility = _facility;
        inbox = createInbox(_initialLocalQueueSize);
        log = _facility.getLog();
        outbox = new Outbox(this, _initialBufferSize);
        recovery = _facility.recovery;
        scheduler = _facility.scheduler;
        initialize(this);
        addClose();
    }

    /**
     * Returns true, if this targetReactor is actively processing messages.
     *
     * @return True, if this targetReactor is actively processing messages.
     */
    public final boolean isRunning() {
        return running;
    }

    @Override
    public Logger getLog() {
        return log;
    }

    @Override
    protected final boolean startedClosing() {
        return startClosing;
    }

    @Override
    public final boolean isClosing() {
        return shuttingDown;
    }

    @Override
    public void close() throws Exception {
        if (startClosing)
            return;
        startClosing = true;
        closeAll();
    }

    protected void close2() throws Exception {
        if (shuttingDown) {
            return;
        }
        shuttingDown = true;
        try {
            outbox.close();
        } catch (final Exception e) {
        }
        try {
            inbox.close();
        } catch (final Exception e) {
        }
        super.close();
        Plant plant = getFacility().getPlant();
        if (!isRunning() || plant.isForcedExit() || plant.isShuttingDown())
            return;
        timeoutSemaphore = plant.schedulableSemaphore(recovery.getThreadInterruptMillis(this));
        if (currentMessage != null)
            currentMessage.close();
        if (!isRunning() || plant.isForcedExit() || plant.isShuttingDown())
            return;
        boolean timeout = timeoutSemaphore.acquire();
        if (!timeout || !isRunning() || plant.isForcedExit() || plant.isShuttingDown())
            return;
        try {
            if (currentMessage == null)
                log.error("hung thread facility=%s", getFacility().name);
            else {
                log.error("hung thread\n" + currentMessage.toString());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        recovery.hungThread(this);
    }

    /**
     * Add to the facility's AutoClose set.
     */
    protected void addClose() throws Exception {
        facility.addCloseable(this);
    }

    /**
     * Create the appropriate type of inbox.
     *
     * @param _initialLocalQueueSize The initial number of slots in the local queue.
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
    public final void setCurrentMessage(final Message _message) {
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
    public final ExceptionHandler setExceptionHandler(
            final ExceptionHandler _handler) {
        if (!isRunning()) {
            throw new IllegalStateException(
                    "Attempt to set an exception handler on an idle targetReactor");
        }
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
    public void unbufferedAddMessage(final Message _message,
                                     final boolean _local) throws Exception {
        if (isClosing()) {
            if (!_message.isClosed()) {
                try {
                    _message.close();
                } catch (final Throwable t) {
                }
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
        if (isClosing()) {
            final Iterator<Message> itm = _messages.iterator();
            while (itm.hasNext()) {
                final Message message = itm.next();
                if (!message.isClosed()) {
                    try {
                        message.close();
                    } catch (final Throwable t) {
                    }
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
        if (!_message.isClosed() && _message.isForeign() && !startClosing && !_message.isSignal()) {
            try {
                addMessage(_message);
            } catch (ServiceClosedException e) {
            }
        }
    }

    /**
     * Called when all pending messages have been processed.
     */
    abstract protected void notBusy() throws Exception;

    @Override
    public final void incomingResponse(final Message _message,
                                       final Reactor _responseSource) {
        try {
            final ReactorBase responseSource = (ReactorBase) _responseSource;
            final boolean local = this == _responseSource;
            if (local || (_responseSource == null)
                    || !responseSource.buffer(_message, this)) {
                unbufferedAddMessage(_message, local);
            }
        } catch (final Throwable t) {
            log.error("unable to add response message", t);
        }
    }

    @Override
    public Facility getFacility() {
        return facility;
    }

    @Override
    public Reactor getReactor() {
        return this;
    }

    /**
     * Signals the start of a request.
     */
    public void requestBegin() {
        inbox.requestBegin();
    }

    /**
     * Signals that a request has completed.
     *
     * @param _message    The request that has completed
     */
    public void requestEnd(final RequestBase _message) {
        if (_message.isForeign()) {
            boolean b = removeMessage(_message);
        }
        inbox.requestEnd();
    }

    /**
     * Returns the atomic reference to the current thread.
     *
     * @return The atomic reference to the current thread.
     */
    abstract public AtomicReference<PoolThread> getThreadReference();

    /**
     * Returns true when there is code to be executed when the inbox is emptied.
     *
     * @return True when there is code to be executed when the inbox is emptied.
     */
    abstract public boolean isIdler();

    @Override
    public void run() {
        running = true;
        try {
            while (true) {
                if (Thread.interrupted()) {
                    System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                    throw new InterruptedException();
                }
                if (timeoutSemaphore != null) {
                    return;
                }
                final Message message = inbox.poll();
                if (message == null) {
                    try {
                        if (timeoutSemaphore != null) {
                            return;
                        }
                        notBusy();
                    } catch (final InterruptedException ie) {
                        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
                        throw ie;
                    } catch (final MigrationException me) {
                        throw me;
                    } catch (final Exception e) {
                        log.error("Exception thrown by onIdle", e);
                    }
                    if (hasWork()) {
                        continue;
                    }
                    break;
                }
                if (timeoutSemaphore != null) {
                    return;
                }
                messageStartTimeMillis = scheduler.currentTimeMillis();
                processMessage(message);
                messageStartTimeMillis = 0;
            }
        } catch (final InterruptedException ie) {
            System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
            if (timeoutSemaphore == null)
                Thread.currentThread().interrupt();
            else
                log.warn("message running too long" + currentMessage.toString());
        } finally {
            messageStartTimeMillis = 0;
            running = false;
            if (timeoutSemaphore != null) {
                timeoutSemaphore.release();
            }
        }
    }

    public SyncRequest<Void> nullSReq() {
        return new SyncBladeRequest<Void>() {
            @Override
            protected Void processSyncRequest() throws Exception {
                return null;
            }
        };
    }

    public void reactorPoll() throws Exception {
        long currentTimeMillis = scheduler.currentTimeMillis();
        long mst = messageStartTimeMillis;
        if (mst > 0) {
            if (mst + recovery.messageTimeoutMillis() < currentTimeMillis) {
                recovery.messageTimeout(this);
            }
        }
    }
}
