package org.agilewiki.jactor2.core.impl;

import com.google.common.collect.MapMaker;
import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.blades.ExceptionHandler;
import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.plant.*;
import org.agilewiki.jactor2.core.reactors.Closeable;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.requests.SyncRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Base class for targetReactor.
 */
abstract public class ReactorImpl extends BladeBase implements Closeable, Runnable, MessageSource {
    public Recovery recovery;

    public Scheduler scheduler;

    private CloseableImpl closeableImpl;

    /**
     * A set of CloseableBase objects.
     * Can only be accessed via a request to the facility.
     */
    private Set<Closeable> closeables;

    private Set<RequestImpl> messages = new HashSet<RequestImpl>();

    private volatile boolean running;

    private SchedulableSemaphore timeoutSemaphore;

    public volatile long messageStartTimeMillis;

    /**
     * Reactor logger.
     */
    protected final Logger logger;

    /**
     * The inbox, implemented as a local queue and a concurrent queue.
     */
    protected Inbox inbox;

    /**
     * Holds the buffered messages until they are passed as blocks to their
     * various destinations.
     */
    protected Outbox outbox;

    /**
     * The currently active exception handler.
     */
    private ExceptionHandler exceptionHandler;

    /**
     * The request or signal message being processed.
     */
    private RequestImpl currentMessage;

    /**
     * Set when the reactor reaches end-of-life.
     * Can only be updated via a request to the reactor.
     */
    private volatile boolean shuttingDown;

    private boolean startClosing;

    protected int initialBufferSize;

    protected int initialLocalQueueSize;

    public final NonBlockingReactor parentReactor;

    public ReactorImpl(final NonBlockingReactorImpl _parentReactorImpl, final int _initialBufferSize,
                       final int _initialLocalQueueSize)
            throws Exception {
        closeableImpl = new CloseableImpl(this);
        PlantConfiguration plantConfiguration = PlantImpl.getSingleton().getPlantConfiguration();
        recovery = _parentReactorImpl == null ? plantConfiguration.getRecovery() : _parentReactorImpl.recovery;
        scheduler = _parentReactorImpl == null ? plantConfiguration.getScheduler() : _parentReactorImpl.scheduler;
        initialBufferSize = _initialBufferSize;
        initialLocalQueueSize = _initialLocalQueueSize;
        parentReactor = _parentReactorImpl == null ? null : _parentReactorImpl.asReactor();
        logger = LoggerFactory.getLogger(Reactor.class);
        if (_parentReactorImpl != null) {
            _parentReactorImpl.addCloseable(this);
        } else {

        }
    }

    public void initialize(final Reactor _reactor) throws Exception {
        super._initialize(_reactor);
        inbox = createInbox(initialLocalQueueSize);
        outbox = new Outbox(initialBufferSize);
    }

    public Reactor asReactor() {
        return getReactor();
    }

    @Override
    public CloseableImpl asCloseableImpl() {
        return closeableImpl;
    }

    public Reactor getParentReactor() {
        return parentReactor;
    }

    public int getInitialBufferSize() {
        return initialBufferSize;
    }

    public int getInitialLocalQueueSize() {
        return initialLocalQueueSize;
    }

    /**
     * Returns true, if this targetReactor is actively processing messages.
     *
     * @return True, if this targetReactor is actively processing messages.
     */
    public final boolean isRunning() {
        return running;
    }

    /**
     * Returns the logger.
     *
     * @return A logger.
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * Returns true when the first phase of closing has begun.
     *
     * @return True when the first phase of closing has begun.
     */
    protected final boolean startedClosing() {
        return startClosing;
    }

    public final boolean isClosing() {
        return shuttingDown;
    }

    @Override
    public void close() throws Exception {
        if (startClosing)
            return;
        startClosing = true;
        Iterator<RequestImpl> it = messages.iterator();
        while (it.hasNext()) {
            RequestImpl message = it.next();
            message.close();
        }
        closeAll();
    }

    /**
     * Performs the second phase of closing.
     */
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
        closeableImpl.close();
        PlantImpl plantImpl = PlantImpl.getSingleton();
        if (plantImpl == null)
            return;
        ReactorImpl plantReactorImpl = plantImpl.getInternalReactor().asReactorImpl();

        if (!isRunning())
            return;
        if (currentMessage != null && currentMessage.isClosed())
            return;
        timeoutSemaphore = plantImpl.schedulableSemaphore(recovery.getThreadInterruptMillis(this));
        Thread thread = (Thread) getThreadReference().get();
        if (thread == null)
            return;
        thread.interrupt();
        boolean timeout = timeoutSemaphore.acquire();
        currentMessage.close();
        if (!timeout || !isRunning() || PlantImpl.getSingleton() == null) {
            return;
        }
        try {
            if (currentMessage == null)
                logger.error("hung thread");
            else {
                logger.error("hung thread\n" + currentMessage.toString());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        recovery.hungThread(this);
    }

    /**
     * Create the appropriate type of inbox.
     *
     * @param _initialLocalQueueSize The initial number of slots in the local queue.
     * @return An inbox.
     */
    abstract protected Inbox createInbox(int _initialLocalQueueSize);

    /**
     * Returns the message currently being processed.
     *
     * @return The message currently being processed.
     */
    public final RequestImpl getCurrentMessage() {
        return currentMessage;
    }

    /**
     * Identify the message currently being processed.
     *
     * @param _message The message currently being processed.
     */
    public final void setCurrentMessage(final RequestImpl _message) {
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

    public final boolean isInboxEmpty() {
        return inbox.isEmpty();
    }

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
    public void unbufferedAddMessage(final RequestImpl _message,
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
    public void unbufferedAddMessages(final Queue<RequestImpl> _messages)
            throws Exception {
        if (isClosing()) {
            final Iterator<RequestImpl> itm = _messages.iterator();
            while (itm.hasNext()) {
                final RequestImpl message = itm.next();
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
    public boolean buffer(final RequestImpl _message, final ReactorImpl _target) {
        return outbox.buffer(_message, _target);
    }

    /**
     * Process the event/request/response message by calling its eval method.
     *
     * @param _message The message to be processed.
     */
    protected void processMessage(final RequestImpl _message) {
        _message.eval();
        if (!_message.isClosed() && _message.isForeign() && !startClosing && !_message.isSignal()) {
            messages.add(_message);
        }
    }

    /**
     * Called when all pending messages have been processed.
     */
    abstract protected void notBusy() throws Exception;

    @Override
    public final void incomingResponse(final RequestImpl _message,
                                       final ReactorImpl _responseSource) {
        try {
            final ReactorImpl responseSource = _responseSource==null ? null : _responseSource;
            final boolean local = this == _responseSource;
            if (local || (_responseSource == null)
                    || !responseSource.buffer(_message, this)) {
                unbufferedAddMessage(_message, local);
            }
        } catch (final Throwable t) {
            logger.error("unable to add response message", t);
        }
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
    public void requestEnd(final RequestImpl _message) {
        if (_message.isForeign()) {
            boolean b = messages.remove(_message);
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
                    throw new InterruptedException();
                }
                if (timeoutSemaphore != null) {
                    return;
                }
                final RequestImpl message = inbox.poll();
                if (message == null) {
                    try {
                        if (timeoutSemaphore != null) {
                            return;
                        }
                        notBusy();
                    } catch (final InterruptedException ie) {
                        throw ie;
                    } catch (final MigrationException me) {
                        throw me;
                    } catch (final Exception e) {
                        logger.error("Exception thrown by onIdle", e);
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
            if (timeoutSemaphore == null)
                Thread.currentThread().interrupt();
            else if (!isClosing())
                logger.warn("message running too long " + currentMessage.toString());
            else
                logger.warn("message interrupted on close " + currentMessage.toString());
        } catch (Exception ex) {
            throw ex;
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
            public Void processSyncRequest() throws Exception {
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
        Iterator<Closeable> it = getCloseableSet().iterator();
        while (it.hasNext()) {
            Closeable closeable = it.next();
            if (!(closeable instanceof ReactorImpl))
                continue;
            ReactorImpl reactor = (ReactorImpl) closeable;
            reactor.reactorPoll();
        }
    }

    /**
     * Returns the CloseableSet. Creates it if needed.
     *
     * @return The CloseableSet.
     */
    protected final Set<Closeable> getCloseableSet() {
        if (closeables == null) {
            closeables = Collections.newSetFromMap((Map)
                    new MapMaker().concurrencyLevel(1).weakKeys().makeMap());
        }
        return closeables;
    }

    public boolean addCloseable(final Closeable _closeable) throws Exception {
        if (startedClosing())
            throw new ServiceClosedException();
        if (this == _closeable)
            return false;
        if (!getCloseableSet().add(_closeable))
            return false;
        _closeable.asCloseableImpl().addReactor(this);
        return true;
    }

    public boolean removeCloseable(final Closeable _closeable) {
        if (closeables == null)
            return false;
        if (!closeables.remove(_closeable))
            return false;
        _closeable.asCloseableImpl().removeReactor(this);
        return true;
    }

    protected void closeAll() throws Exception {
        if (closeables == null) {
            close2();
            return;
        }
        Iterator<Closeable> it = closeables.iterator();
        while (it.hasNext()) {
            Closeable closeable = it.next();
            try {
                closeable.close();
            } catch (final Throwable t) {
                if (closeable != null && PlantImpl.DEBUG) {
                    getLogger().warn("Error closing a " + closeable.getClass().getName(), t);
                }
            }
        }
        it = closeables.iterator();
        while (it.hasNext()) {
            Closeable closeable = it.next();
            getLogger().warn("still has closable: " + this + "\n" + closeable);
        }
        close2();
    }
}
