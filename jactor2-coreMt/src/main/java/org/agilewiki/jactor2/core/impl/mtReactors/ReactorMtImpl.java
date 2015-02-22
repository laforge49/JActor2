package org.agilewiki.jactor2.core.impl.mtReactors;

import com.google.common.collect.MapMaker;
import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.impl.mtMessages.RequestMtImpl;
import org.agilewiki.jactor2.core.impl.mtMessages.RequestSource;
import org.agilewiki.jactor2.core.impl.mtPlant.PlantConfiguration;
import org.agilewiki.jactor2.core.impl.mtPlant.PlantMtImpl;
import org.agilewiki.jactor2.core.impl.mtPlant.Recovery;
import org.agilewiki.jactor2.core.impl.mtPlant.SchedulableSemaphore;
import org.agilewiki.jactor2.core.messages.ExceptionHandler;
import org.agilewiki.jactor2.core.messages.SOp;
import org.agilewiki.jactor2.core.messages.impl.RequestImpl;
import org.agilewiki.jactor2.core.plant.impl.MetricsTimer;
import org.agilewiki.jactor2.core.plant.impl.PlantImpl;
import org.agilewiki.jactor2.core.plant.impl.PlantScheduler;
import org.agilewiki.jactor2.core.reactors.CommonReactor;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.reactors.ReactorClosedException;
import org.agilewiki.jactor2.core.reactors.closeable.Closeable;
import org.agilewiki.jactor2.core.reactors.closeable.impl.CloseableImpl;
import org.agilewiki.jactor2.core.reactors.closeable.impl.CloseableImplImpl;
import org.agilewiki.jactor2.core.reactors.impl.ReactorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

abstract public class ReactorMtImpl extends BladeBase implements ReactorImpl,
        RequestSource {
    /**
     * A reference to the thread that is executing this reactor.
     */
    protected final AtomicReference<Thread> threadReference = new AtomicReference<Thread>();

    private Recovery recovery;

    private PlantScheduler plantScheduler;

    private final CloseableImpl closeableImpl;

    /**
     * A set of CloseableBase objects.
     * Can only be accessed via a request to the facility.
     */
    private Set<Closeable> closeables;

    private final Set<RequestImpl<?>> inProcessRequests = new HashSet<RequestImpl<?>>();

    private volatile boolean running;

    private SchedulableSemaphore timeoutSemaphore;

    private volatile double messageStartTimeMillis;

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
    private ExceptionHandler<?> exceptionHandler;

    /**
     * The request or signal message being processed.
     */
    private RequestMtImpl<?> currentRequest;

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
    public final IsolationReactor parentReactor;

    private String reason;

    /**
     * Create a ReactorMtImpl instance.
     *
     * @param _parentReactor         The parent reactor, or null.
     * @param _initialBufferSize     The initial size of a send buffer.
     * @param _initialLocalQueueSize The initial size of the local queue.
     */
    public ReactorMtImpl(final IsolationReactor _parentReactor,
                         final int _initialBufferSize, final int _initialLocalQueueSize) {
        closeableImpl = new CloseableImplImpl(this);
        final PlantConfiguration plantConfiguration = PlantMtImpl
                .getSingleton().getPlantConfiguration();
        @SuppressWarnings("resource")
        final IsolationReactorMtImpl parentReactorImpl = _parentReactor == null ? null
                : (IsolationReactorMtImpl) _parentReactor.asReactorImpl();
        recovery = _parentReactor == null ? plantConfiguration.getRecovery()
                : parentReactorImpl.getRecovery();
        plantScheduler = _parentReactor == null ? plantConfiguration
                .getPlantScheduler() : parentReactorImpl.getPlantScheduler();
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
    @Override
    public void initialize(final Reactor _reactor) throws Exception {
        super._initialize(_reactor);
        inbox = createInbox(initialLocalQueueSize);
        outbox = new Outbox(initialBufferSize);
    }

    /**
     * Returns the Reactor of this ReactorImpl.
     *
     * @return The Reactor of this ReactorImpl.
     */
    @Override
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
    @Override
    public IsolationReactor getParentReactor() {
        return parentReactor;
    }

    /**
     * Returns the initial size of a send buffer.
     *
     * @return The initial size of a send buffer.
     */
    @Override
    public int getInitialBufferSize() {
        return initialBufferSize;
    }

    /**
     * Returns the initial size of the local queue.
     *
     * @return The initial size of the local queue.
     */
    @Override
    public int getInitialLocalQueueSize() {
        return initialLocalQueueSize;
    }

    /**
     * Returns true, if this ReactorImpl is actively processing messages.
     *
     * @return True, if this ReactorImpl is actively processing messages.
     */
    @Override
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
    @Override
    public void fail(final String _reason) throws Exception {
        reason = _reason;
        closeableImpl.close();

        if (startClosing) {
            return;
        }
        startClosing = true;

        if (closeables != null) {
            final HashSet<Closeable> hs = new HashSet<Closeable>(closeables);
            Iterator<Closeable> cit = hs.iterator();
            while (cit.hasNext()) {
                final Closeable closeable = cit.next();
                try {
                    closeable.close();
                } catch (final Throwable t) {
                    if ((closeable != null) && PlantMtImpl.DEBUG) {
                        getLogger().warn(
                                "Error closing a "
                                        + closeable.getClass().getName(), t);
                    }
                }
            }
            cit = closeables.iterator();
            while (cit.hasNext()) {
                final Closeable closeable = cit.next();
                warn("still has closable: " + this + "\n" + closeable);
            }
        }

        shuttingDown = true;

        final PlantMtImpl plantImpl = PlantMtImpl.getSingleton();
        if ((plantImpl != null) && isRunning()
                && ((currentRequest == null) || !currentRequest.isComplete())) {
            timeoutSemaphore = plantImpl.schedulableSemaphore(recovery
                    .getThreadInterruptMillis(this));
            final Thread thread = getThreadReference().get();
            if (thread != null) {
                thread.interrupt();
                boolean timeout = false;
                try {
                    timeout = timeoutSemaphore.acquire();
                } catch (InterruptedException ie) {
                }
                currentRequest.close();
                if (timeout
                        && (isRunning() & (PlantImpl.getSingleton() != null))) {
                    try {
                        if (currentRequest == null) {
                            logger.error("hung thread");
                        } else {
                            logger.error("hung thread\n"
                                    + currentRequest.toString());
                        }
                    } catch (final Exception ex) {
                        ex.printStackTrace();
                    }
                    recovery.onHungThread(this);
                }
            }
        }

        final Iterator<RequestImpl<?>> mit = inProcessRequests.iterator();
        while (mit.hasNext()) {
            mit.next().close();
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
    public final RequestMtImpl<?> getCurrentRequest() {
        return currentRequest;
    }

    /**
     * Assigns the message currently being processed.
     *
     * @param _message The message currently being processed.
     */
    public final void setCurrentRequest(final RequestMtImpl<?> _message) {
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
    @Override
    public final boolean isInboxEmpty() {
        return inbox.isEmpty();
    }

    /**
     * Assign an exception handler.
     *
     * @param _handler The new exception handler, or null.
     * @return The old exception handler, or null.
     */
    @Override
    public final ExceptionHandler<?> setExceptionHandler(
            final ExceptionHandler<?> _handler) {
        if (!isRunning()) {
            throw new IllegalStateException(
                    "Attempt to set an exception handler on an idle targetReactor");
        }
        final ExceptionHandler<?> rv = this.exceptionHandler;
        this.exceptionHandler = _handler;
        return rv;
    }

    /**
     * Returns the current exception handler.
     *
     * @return The current exception handler, or null.
     */
    public final ExceptionHandler<?> getExceptionHandler() {
        return exceptionHandler;
    }

    /**
     * Add a message directly to the input queue of a Reactor.
     *
     * @param _message A message.
     * @param _local   True when the current thread is assigned to the targetReactor.
     */
    public void unbufferedAddMessage(final RequestMtImpl<?> _message,
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
    public void unbufferedAddMessages(final Queue<RequestMtImpl<?>> _messages)
            throws Exception {
        if (isClosing()) {
            final Iterator<RequestMtImpl<?>> itm = _messages.iterator();
            while (itm.hasNext()) {
                final RequestMtImpl<?> message = itm.next();
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
    public boolean buffer(final RequestMtImpl<?> _message,
                          final ReactorMtImpl _target) {
        return outbox.buffer(_message, _target);
    }

    /**
     * Process the event/request/response message by calling its eval method.
     *
     * @param _message The message to be processed.
     */
    protected void processMessage(final RequestMtImpl<?> _message) {
        _message.eval();
        if (!_message.isComplete() && !startClosing && !_message.isOneWay()) {
            inProcessRequests.add(_message);
        }
    }

    /**
     * Called when all pending messages that can be processed have been processed.
     */
    abstract protected void notBusy() throws Exception;

    @Override
    public final void incomingResponse(final RequestImpl<?> _message,
                                       final ReactorImpl _responseSource) {
        final RequestMtImpl<?> message = (RequestMtImpl<?>) _message;
        try {
            @SuppressWarnings("resource")
            final ReactorMtImpl responseSource = _responseSource == null ? null
                    : (ReactorMtImpl) _responseSource;
            final boolean local = this == _responseSource;
            if (local || (_responseSource == null)
                    || !responseSource.buffer(message, this)) {
                unbufferedAddMessage(message, local);
            }
        } catch (final Throwable t) {
            logger.error("unable to add response message", t);
        }
    }

    /**
     * Signals the start of a request.
     */
    public void requestBegin(final RequestImpl<?> _requestImpl) {
        inbox.requestBegin((RequestMtImpl<?>) _requestImpl);
    }

    /**
     * Signals that a request has completed.
     *
     * @param _message The request that has completed
     */
    public void requestEnd(final RequestImpl<?> _message) {
        final RequestMtImpl<?> message = (RequestMtImpl<?>) _message;
        if (message.isForeign()) {
            inProcessRequests.remove(_message);
        }
        inbox.requestEnd(message);
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
                RequestMtImpl<?> request = inbox.poll();
                while ((request != null) && request._isCanceled()) {
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
            if (timeoutSemaphore == null) {
                Thread.currentThread().interrupt();
            } else if (!isClosing()) {
                logger.warn("message running too long "
                        + currentRequest.toString());
            } else if (!currentRequest.isComplete()) {
                logger.warn("message interrupted on close "
                        + currentRequest.toString());
            }
        } catch (final Exception ex) {
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
    @Override
    public SOp<Void> nullSOp() {
        return new SOp<Void>("null", getReactor()) {
            @Override
            protected Void processSyncOperation(RequestImpl _requestImpl) throws Exception {
                return null;
            }
        };
    }

    /**
     * Check if the current message has timed out and poll any child reactors for same.
     */
    public void reactorPoll() throws Exception {
        final double currentTimeMillis = plantScheduler.currentTimeMillis();
        final double mst = messageStartTimeMillis;
        if (mst > 0) {
            int timerMillis = recovery.getMessageTimeoutMillis(this);
            if (currentRequest != null) {
                int localTimeout = currentRequest.getMessageTimeoutMillis();
                if (localTimeout > -1)
                    timerMillis = localTimeout;
            }
            if ((mst + timerMillis) < currentTimeMillis) {
                recovery.onMessageTimeout(this);
            }
        }
        final Iterator<Closeable> it = getCloseableSet().iterator();
        while (it.hasNext()) {
            final Closeable closeable = it.next();
            if (!(closeable instanceof ReactorMtImpl)) {
                continue;
            }
            final ReactorMtImpl reactor = (ReactorMtImpl) closeable;
            reactor.reactorPoll();
        }
    }

    /**
     * Returns the CloseableSet. Creates it if needed.
     *
     * @return The CloseableSet.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected final Set<Closeable> getCloseableSet() {
        if (closeables == null) {
            closeables = Collections.newSetFromMap((Map) new MapMaker()
                    .concurrencyLevel(1).weakKeys().makeMap());
        }
        return closeables;
    }

    /**
     * Add a closeable to the list of closeables.
     *
     * @param _closeable A closeable to be closed when this ReactorImpl is closed.
     * @return True when the closeable was added to the list.
     */
    @Override
    public boolean addCloseable(final Closeable _closeable) {
        if (startedClosing()) {
            throw new ReactorClosedException(
                    "call to addCloseable when reactor already started closing: "
                            + reason);
        }
        if (this == _closeable) {
            return false;
        }
        if (!getCloseableSet().add(_closeable)) {
            return false;
        }
        _closeable.asCloseableImpl().addReactor(this);
        return true;
    }

    /**
     * Remove a closeable from the list of closeables.
     *
     * @param _closeable The closeable to be removed.
     * @return True when the closeable was removed.
     */
    @Override
    public boolean removeCloseable(final Closeable _closeable) {
        if (closeables == null) {
            return false;
        }
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

    public void setRecovery(final Recovery recovery) {
        this.recovery = recovery;
    }

    /**
     * The PlantScheduler object used by this ReactorImpl.
     */
    public PlantScheduler getPlantScheduler() {
        return plantScheduler;
    }

    public void setPlantScheduler(final PlantScheduler plantScheduler) {
        this.plantScheduler = plantScheduler;
    }

    /**
     * Log a message at the WARN level.
     *
     * @param msg the message string to be logged
     */
    @Override
    public void warn(final String msg) {
        logger.warn(msg);
    }

    /**
     * Log an exception (throwable) at the WARN level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t   the exception (throwable) to log
     */
    @Override
    public void warn(final String msg, final Throwable t) {
        logger.warn(msg, t);
    }

    /**
     * Log a message at the ERROR level.
     *
     * @param msg the message string to be logged
     */
    @Override
    public void error(final String msg) {
        logger.error(msg);
    }

    /**
     * Log an exception (throwable) at the ERROR level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t   the exception (throwable) to log
     */
    @Override
    public void error(final String msg, final Throwable t) {
        logger.error(msg, t);
    }

    @Override
    public void addResource(ReactorImpl _reactorImpl) {
    }

    @Override
    public boolean isResource(ReactorImpl _reactorImpl) {
        return true;
    }

    public MetricsTimer getMetricsTimer(final String _name) {
        return recovery.getMetricsTimer(_name);
    }
}
