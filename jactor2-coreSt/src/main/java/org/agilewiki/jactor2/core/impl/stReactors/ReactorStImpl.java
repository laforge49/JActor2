package org.agilewiki.jactor2.core.impl.stReactors;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.closeable.Closeable;
import org.agilewiki.jactor2.core.closeable.CloseableImpl;
import org.agilewiki.jactor2.core.impl.stCloseable.CloseableStImpl;
import org.agilewiki.jactor2.core.impl.stPlant.PlantConfiguration;
import org.agilewiki.jactor2.core.impl.stPlant.PlantStImpl;
import org.agilewiki.jactor2.core.impl.stPlant.Recovery;
import org.agilewiki.jactor2.core.impl.stRequests.RequestStImpl;
import org.agilewiki.jactor2.core.plant.PlantScheduler;
import org.agilewiki.jactor2.core.reactors.CommonReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.reactors.ReactorClosedException;
import org.agilewiki.jactor2.core.reactors.ReactorImpl;
import org.agilewiki.jactor2.core.requests.ExceptionHandler;
import org.agilewiki.jactor2.core.requests.RequestImpl;
import org.agilewiki.jactor2.core.requests.SyncRequest;

abstract public class ReactorStImpl extends BladeBase implements ReactorImpl {
    private Recovery recovery;

    private PlantScheduler plantScheduler;

    private final CloseableImpl closeableImpl;

    /**
     * A set of CloseableBase objects.
     * Can only be accessed via a request to the facility.
     */
    private Set<Closeable> closeables;

    private final Set<RequestImpl> inProcessRequests = new HashSet<RequestImpl>();

    private volatile boolean running;

    /**
     * The inbox of this ReactorImpl.
     */
    protected Inbox inbox;

    /**
     * The currently active exception handler.
     */
    private ExceptionHandler exceptionHandler;

    /**
     * The request or signal message being processed.
     */
    private RequestStImpl currentRequest;

    /**
     * Set when the reactor reaches end-of-life.
     * Can only be updated via a request to the reactor.
     */
    private volatile boolean shuttingDown;

    private boolean startClosing;

    /**
     * The parent reactor, or null.
     */
    public final NonBlockingReactor parentReactor;

    private String reason;

    PlantConfiguration plantConfiguration;

    /**
     * Create a ReactorMtImpl instance.
     *
     * @param _parentReactor         The parent reactor, or null.
     */
    public ReactorStImpl(final NonBlockingReactor _parentReactor) {
        closeableImpl = new CloseableStImpl(this);
        plantConfiguration = PlantStImpl.getSingleton().getPlantConfiguration();
        final NonBlockingReactorStImpl parentReactorImpl = _parentReactor == null ? null
                : (NonBlockingReactorStImpl) _parentReactor.asReactorImpl();
        recovery = _parentReactor == null ? plantConfiguration.getRecovery()
                : parentReactorImpl.getRecovery();
        plantScheduler = _parentReactor == null ? plantConfiguration
                .getPlantScheduler() : parentReactorImpl.getPlantScheduler();
        parentReactor = _parentReactor;
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
    public void initialize(final Reactor _reactor) {
        super._initialize(_reactor);
        inbox = createInbox();
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
     * Returns the parent reactor.
     *
     * @return The parent reactor, or null.
     */
    @Override
    public NonBlockingReactor getParentReactor() {
        return parentReactor;
    }

    /**
     * Returns the initial size of a send buffer.
     *
     * @return The initial size of a send buffer.
     */
    @Override
    public int getInitialBufferSize() {
        return 0;
    }

    /**
     * Returns the initial size of the local queue.
     *
     * @return The initial size of the local queue.
     */
    @Override
    public int getInitialLocalQueueSize() {
        return 0;
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
     * closes all closeables, closes the inbox, interrupts the processing
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

        if (startClosing)
            return;
        startClosing = true;

        if (closeables != null) {
            final HashSet<Closeable> hs = new HashSet<Closeable>(closeables);
            Iterator<Closeable> cit = hs.iterator();
            while (cit.hasNext()) {
                final Closeable closeable = cit.next();
                try {
                    closeable.close();
                } catch (final Throwable t) {
                    if (closeable != null && PlantStImpl.DEBUG) {
                        warn("Error closing a "
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

        final Iterator<RequestImpl> mit = inProcessRequests.iterator();
        while (mit.hasNext()) {
            final RequestImpl requestImpl = mit.next();
            requestImpl.close();
        }

        try {
            inbox.close();
        } catch (final Exception e) {
        }
    }

    /**
     * Create the appropriate type of inbox.
     *
     * @return An inbox.
     */
    abstract protected Inbox createInbox();

    /**
     * Returns the message currently being processed.
     *
     * @return The message currently being processed, or null.
     */
    public final RequestStImpl getCurrentRequest() {
        return currentRequest;
    }

    /**
     * Assigns the message currently being processed.
     *
     * @param _message The message currently being processed.
     */
    public final void setCurrentRequest(final RequestStImpl _message) {
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
    public void unbufferedAddMessage(final RequestStImpl _message,
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
        inbox.offerLocal(_message);
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
        return false;
    }

    /**
     * Process the event/request/response message by calling its eval method.
     *
     * @param _message The message to be processed.
     */
    protected void processMessage(final RequestStImpl _message) {
        _message.eval();
        if (!_message.isComplete() && !startClosing && !_message.isOneWay()) {
            inProcessRequests.add(_message);
        }
    }

    /**
     * Called when all pending messages that can be processed have been processed.
     */
    abstract protected void notBusy() throws Exception;

    public final void incomingResponse(final RequestStImpl _message,
            final ReactorImpl _responseSource) {
        try {
            final boolean local = this == _responseSource;
            unbufferedAddMessage(_message, local);
        } catch (final Throwable t) {
            error("unable to add response message", t);
        }
    }

    /**
     * Signals the start of a request.
     */
    @Override
    public void requestBegin(final RequestImpl _requestImpl) {
        inbox.requestBegin((RequestStImpl) _requestImpl);
    }

    /**
     * Signals that a request has completed.
     *
     * @param _message The request that has completed
     */
    @Override
    public void requestEnd(final RequestImpl _message) {
        RequestStImpl message = (RequestStImpl) _message;
        if (message.isForeign()) {
            final boolean b = inProcessRequests.remove(message);
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
                    return;
                }
                RequestStImpl request = inbox.poll();
                while (request != null && request._isCanceled()) {
                    request = inbox.poll();
                }
                if (request == null) {
                    try {
                        notBusy();
                    } catch (final Exception e) {
                        error("Exception thrown by onIdle", e);
                    }
                    if (hasWork()) {
                        continue;
                    }
                    break;
                }
                processMessage(request);
            }
        } finally {
            running = false;
        }
    }

    /**
     * A noop request used for synchronizing state.
     *
     * @return null.
     */
    @Override
    public SyncRequest<Void> nullSReq() {
        return new SyncBladeRequest<Void>() {
            @Override
            public Void processSyncRequest() throws Exception {
                return null;
            }
        };
    }

    /**
     * Returns the CloseableSet. Creates it if needed.
     *
     * @return The CloseableSet.
     */
    protected final Set<Closeable> getCloseableSet() {
        if (closeables == null) {
            closeables = new HashSet<Closeable>();
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
        if (startedClosing())
            throw new ReactorClosedException(
                    "call to addCloseable when reactor already started closing: "
                            + reason);
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
    @Override
    public boolean removeCloseable(final Closeable _closeable) {
        if (closeables == null)
            return false;
        if (!closeables.remove(_closeable)) {
            return false;
        }
        _closeable.asCloseableImpl().removeReactor(this);
        return true;
    }

    @Override
    public boolean isSlow() {
        return false;
    }

    @Override
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
     * The time when processing began on the current message.
     */
    @Override
    public double getMessageStartTimeMillis() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setMessageStartTimeMillis(final double messageStartTimeMillis) {
        throw new UnsupportedOperationException();
    }

    /**
     * Log a message at the WARN level.
     *
     * @param msg the message string to be logged
     */
    @Override
    public void warn(final String msg) {
        plantConfiguration.warn(msg);
    }

    /**
     * Log an exception (throwable) at the WARN level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t the exception (throwable) to log
     */
    @Override
    public void warn(final String msg, final Throwable t) {
        plantConfiguration.warn(msg, t);
    }

    /**
     * Log a message at the ERROR level.
     *
     * @param msg the message string to be logged
     */
    @Override
    public void error(final String msg) {
        plantConfiguration.error(msg);
    }

    /**
     * Log an exception (throwable) at the ERROR level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t the exception (throwable) to log
     */
    @Override
    public void error(final String msg, final Throwable t) {
        plantConfiguration.error(msg, t);
    }
}
