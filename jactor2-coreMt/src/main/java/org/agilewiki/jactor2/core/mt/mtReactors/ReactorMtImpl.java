package org.agilewiki.jactor2.core.mt.mtReactors;

import com.google.common.collect.MapMaker;
import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.closeable.Closeable;
import org.agilewiki.jactor2.core.closeable.CloseableImpl;
import org.agilewiki.jactor2.core.closeable.CloseableImpl1;
import org.agilewiki.jactor2.core.mt.mtPlant.PlantMtImpl;
import org.agilewiki.jactor2.core.plant.PlantImpl;
import org.agilewiki.jactor2.core.plant.SchedulableSemaphore;
import org.agilewiki.jactor2.core.reactors.MigrationException;
import org.agilewiki.jactor2.core.reactors.ReactorImpl;
import org.agilewiki.jactor2.core.requests.RequestImpl;
import org.agilewiki.jactor2.core.mt.mtRequests.RequestSource;
import org.agilewiki.jactor2.core.plant.PlantConfiguration;
import org.agilewiki.jactor2.core.plant.PlantScheduler;
import org.agilewiki.jactor2.core.plant.Recovery;
import org.agilewiki.jactor2.core.reactors.CommonReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.reactors.ReactorClosedException;
import org.agilewiki.jactor2.core.requests.ExceptionHandler;
import org.agilewiki.jactor2.core.requests.SyncRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

abstract public class ReactorMtImpl extends BladeBase implements ReactorImpl, RequestSource {
    /**
     * A reference to the thread that is executing this reactor.
     */
    protected final AtomicReference<Thread> threadReference = new AtomicReference<Thread>();

    private Recovery recovery;

    private PlantScheduler plantScheduler;

    private CloseableImpl closeableImpl;

    /**
     * A set of CloseableBase objects.
     * Can only be accessed via a request to the facility.
     */
    private Set<Closeable> closeables;

    private Set<RequestImpl> inProcessRequests = new HashSet<RequestImpl>();

    private volatile boolean running;

    private SchedulableSemaphore timeoutSemaphore;

    private volatile long messageStartTimeMillis;

    /**
     * The ReactorImpl logger.
     */
    protected final Logger logger;

    /**
     * The inbox of this ReactorImpl.
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
    private RequestImpl currentRequest;

    /**
     * Set when the reactor reaches end-of-life.
     * Can only be updated via a request to the reactor.
     */
    private volatile boolean shuttingDown;

    private boolean startClosing;

    /**
     * The initial size of a send buffer.
     */
    protected int initialBufferSize;

    /**
     * The initial size of the local queue.
     */
    protected int initialLocalQueueSize;

    /**
     * The parent reactor, or null.
     */
    public final NonBlockingReactor parentReactor;

    private String reason;

    /**
     * Create a ReactorMtImpl instance.
     *
     * @param _parentReactor         The parent reactor, or null.
     * @param _initialBufferSize     The initial size of a send buffer.
     * @param _initialLocalQueueSize The initial size of the local queue.
     */
    public ReactorMtImpl(final NonBlockingReactor _parentReactor, final int _initialBufferSize,
                         final int _initialLocalQueueSize) {
        closeableImpl = new CloseableImpl1(this);
        PlantConfiguration plantConfiguration = PlantMtImpl.getSingleton().getPlantConfiguration();
        ReactorImpl parentReactorImpl = _parentReactor == null ? null : _parentReactor.asReactorImpl();
        recovery = _parentReactor == null ? plantConfiguration.getRecovery() : parentReactorImpl.getRecovery();
        plantScheduler = _parentReactor == null ?
                plantConfiguration.getPlantScheduler() : parentReactorImpl.getPlantScheduler();
        initialBufferSize = _initialBufferSize;
        initialLocalQueueSize = _initialLocalQueueSize;
        parentReactor = _parentReactor;
        logger = LoggerFactory.getLogger(Reactor.class);
        if (_parentReactor != null) {
            _parentReactor.addCloseable(this);
        } else {
        }
    }

    /**
     * Initialize the ReactorImpl.
     *
     * @param _reactor The Reactor of this ReactorImpl.
     */
    public void initialize(final Reactor _reactor) {
        super._initialize(_reactor);
        inbox = createInbox(initialLocalQueueSize);
        outbox = new Outbox(initialBufferSize);
    }

    /**
     * Returns the Reactor of this ReactorImpl.
     *
     * @return The Reactor of this ReactorImpl.
     */
    public Reactor asReactor() {
        return getReactor();
    }

    @Override
    public CloseableImpl asCloseableImpl() {
        return closeableImpl;
    }

    /**
     * Returns the atomic reference to the reactor's thread.
     *
     * @return The atomic reference to the reactor's thread.
     */
    public AtomicReference<Thread> getThreadReference() {
        return threadReference;
    }

    /**
     * Returns the parent reactor.
     *
     * @return The parent reactor, or null.
     */
    public NonBlockingReactor getParentReactor() {
        return parentReactor;
    }

    /**
     * Returns the initial size of a send buffer.
     *
     * @return The initial size of a send buffer.
     */
    public int getInitialBufferSize() {
        return initialBufferSize;
    }

    /**
     * Returns the initial size of the local queue.
     *
     * @return The initial size of the local queue.
     */
    public int getInitialLocalQueueSize() {
        return initialLocalQueueSize;
    }

    /**
     * Returns true, if this ReactorImpl is actively processing messages.
     *
     * @return True, if this ReactorImpl is actively processing messages.
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

    /**
     * Removes this ReactorImpl from any closers, closes any requests that are in process,
     * closes all closeables, closes the outbox, closes the inbox, interrupts the processing
     * of the current thread and, if the interrupt is not effective, begins hung thread recovery.
     */
    @Override
    public void close() throws Exception {
        fail(null);
    }

    public String getReasonForFailure() {
        return reason;
    }

    /**
     * Close the reactor;
     *
     * @param _reason The reason why the reactor is being closed,
     *                or null if not a failure.
     */
    public void fail(final String _reason) throws Exception {
        reason = _reason;
        closeableImpl.close();

        if (startClosing)
            return;
        startClosing = true;

        if (closeables != null) {
            HashSet<Closeable> hs = new HashSet<Closeable>(closeables);
            Iterator<Closeable> cit = hs.iterator();
            while (cit.hasNext()) {
                Closeable closeable = cit.next();
                try {
                    closeable.close();
                } catch (final Throwable t) {
                    if (closeable != null && PlantMtImpl.DEBUG) {
                        getLogger().warn("Error closing a " + closeable.getClass().getName(), t);
                    }
                }
            }
            cit = closeables.iterator();
            while (cit.hasNext()) {
                Closeable closeable = cit.next();
                warn("still has closable: " + this + "\n" + closeable);
            }
        }

        shuttingDown = true;

        PlantMtImpl plantImpl = PlantMtImpl.getSingleton();
        if (plantImpl != null &&
                isRunning() &&
                (currentRequest == null || !currentRequest.isComplete())) {
            timeoutSemaphore = plantImpl.schedulableSemaphore(recovery.getThreadInterruptMillis(this));
            Thread thread = (Thread) getThreadReference().get();
            if (thread != null) {
                thread.interrupt();
                boolean timeout = timeoutSemaphore.acquire();
                currentRequest.close();
                if (timeout && isRunning() & PlantImpl.getSingleton() != null) {
                    try {
                        if (currentRequest == null)
                            logger.error("hung thread");
                        else {
                            logger.error("hung thread\n" + currentRequest.toString());
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    recovery.onHungThread(this);
                }
            }
        }

        Iterator<RequestImpl> mit = inProcessRequests.iterator();
        while (mit.hasNext()) {
            RequestImpl requestImpl = mit.next();
            requestImpl.close();
        }

        try {
            outbox.close();
        } catch (final Exception e) {
        }

        try {
            inbox.close();
        } catch (final Exception e) {
        }
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
     * @return The message currently being processed, or null.
     */
    public final RequestImpl getCurrentRequest() {
        return currentRequest;
    }

    /**
     * Assigns the message currently being processed.
     *
     * @param _message The message currently being processed.
     */
    public final void setCurrentRequest(final RequestImpl _message) {
        currentRequest = _message;
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

    /**
     * Returns true when the inbox is not empty.
     *
     * @return True when the inbox is not empty.
     */
    public final boolean isInboxEmpty() {
        return inbox.isEmpty();
    }

    /**
     * Assign an exception handler.
     *
     * @param _handler The new exception handler, or null.
     * @return The old exception handler, or null.
     */
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
     * Returns the current exception handler.
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
     * @param _local   True when the current thread is assigned to the targetReactor.
     */
    public void unbufferedAddMessage(final RequestImpl _message,
                                     final boolean _local) {
        if (isClosing()) {
            if (!_message.isComplete()) {
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
                if (!message.isComplete()) {
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
    abstract protected void afterAdd();

    /**
     * Buffers a message in the sending targetReactor for sending later.
     *
     * @param _message Message to be buffered.
     * @param _target  The reactor that should eventually receive this message
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
        if (!_message.isComplete() && !startClosing && !_message.isOneWay()) {
            inProcessRequests.add(_message);
        }
    }

    /**
     * Called when all pending messages that can be processed have been processed.
     */
    abstract protected void notBusy() throws Exception;

    public final void incomingResponse(final RequestImpl _message,
                                       final ReactorImpl _responseSource) {
        try {
            final ReactorImpl responseSource = _responseSource == null ? null : _responseSource;
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
    public void requestBegin(final RequestImpl _requestImpl) {
        inbox.requestBegin(_requestImpl);
    }

    /**
     * Signals that a request has completed.
     *
     * @param _message The request that has completed
     */
    public void requestEnd(final RequestImpl _message) {
        if (_message.isForeign()) {
            boolean b = inProcessRequests.remove(_message);
        }
        inbox.requestEnd(_message);
    }

    /**
     * Process any pending messages that can be processed.
     */
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
                RequestImpl request = inbox.poll();
                while (request != null && request._isCanceled()) {
                    request = inbox.poll();
                }
                if (request == null) {
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
                messageStartTimeMillis = plantScheduler.currentTimeMillis();
                processMessage(request);
                messageStartTimeMillis = 0;
            }
        } catch (final InterruptedException ie) {
            if (timeoutSemaphore == null)
                Thread.currentThread().interrupt();
            else if (!isClosing())
                logger.warn("message running too long " + currentRequest.toString());
            else if (!currentRequest.isComplete())
                logger.warn("message interrupted on close " + currentRequest.toString());
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

    /**
     * A noop request used for synchronizing state.
     *
     * @return null.
     */
    public SyncRequest<Void> nullSReq() {
        return new SyncBladeRequest<Void>() {
            @Override
            public Void processSyncRequest() throws Exception {
                return null;
            }
        };
    }

    /**
     * Check if the current message has timed out and poll any child reactors for same.
     */
    public void reactorPoll() throws Exception {
        long currentTimeMillis = plantScheduler.currentTimeMillis();
        long mst = messageStartTimeMillis;
        if (mst > 0) {
            if (mst + recovery.getMessageTimeoutMillis(this) < currentTimeMillis) {
                recovery.onMessageTimeout(this);
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

    /**
     * Add a closeable to the list of closeables.
     *
     * @param _closeable A closeable to be closed when this ReactorImpl is closed.
     * @return True when the closeable was added to the list.
     */
    public boolean addCloseable(final Closeable _closeable) {
        if (startedClosing())
            throw new ReactorClosedException("call to addCloseable when reactor already started closing: " +
                    reason);
        if (this == _closeable)
            return false;
        if (!getCloseableSet().add(_closeable))
            return false;
        _closeable.asCloseableImpl().addReactor(this);
        return true;
    }

    /**
     * Remove a closeable from the list of closeables.
     *
     * @param _closeable The closeable to be removed.
     * @return True when the closeable was removed.
     */
    public boolean removeCloseable(final Closeable _closeable) {
        if (closeables == null)
            return false;
        if (!closeables.remove(_closeable)) {
            return false;
        }
        _closeable.asCloseableImpl().removeReactor(this);
        return true;
    }

    public boolean isSlow() {
        return false;
    }

    public boolean isCommonReactor() {
        return asReactor() instanceof CommonReactor;
    }

    /**
     * The Recovery object used by this ReactorImpl.
     */
    public Recovery getRecovery() {
        return recovery;
    }

    public void setRecovery(Recovery recovery) {
        this.recovery = recovery;
    }

    /**
     * The PlantScheduler object used by this ReactorImpl.
     */
    public PlantScheduler getPlantScheduler() {
        return plantScheduler;
    }

    public void setPlantScheduler(PlantScheduler plantScheduler) {
        this.plantScheduler = plantScheduler;
    }

    /**
     * The time when processing began on the current message.
     */
    public long getMessageStartTimeMillis() {
        return messageStartTimeMillis;
    }

    public void setMessageStartTimeMillis(long messageStartTimeMillis) {
        this.messageStartTimeMillis = messageStartTimeMillis;
    }

    /**
     * Log a message at the WARN level.
     *
     * @param msg the message string to be logged
     */
    public void warn(String msg) {
        logger.warn(msg);
    }

    /**
     * Log an exception (throwable) at the WARN level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t the exception (throwable) to log
     */
    public void warn(String msg, Throwable t) {
        logger.warn(msg, t);
    }

    /**
     * Log a message at the ERROR level.
     *
     * @param msg the message string to be logged
     */
    public void error(String msg) {
        logger.error(msg);
    }

    /**
     * Log an exception (throwable) at the ERROR level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t the exception (throwable) to log
     */
    public void error(String msg, Throwable t) {
        logger.error(msg, t);
    }
}
