package org.agilewiki.jactor2.core.messages;

import org.agilewiki.jactor2.core.blades.ExceptionHandler;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.plant.MigrationException;
import org.agilewiki.jactor2.core.plant.PlantImpl;
import org.agilewiki.jactor2.core.plant.PoolThread;
import org.agilewiki.jactor2.core.plant.ServiceClosedException;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.reactors.ReactorBase;
import org.agilewiki.jactor2.core.reactors.ThreadBoundReactor;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

public abstract class RequestBase<RESPONSE_TYPE> implements Message {

    /**
     * Process the request immediately.
     *
     * @param _source            The targetReactor on whose thread this method was invoked and which
     *                           must be the same as the targetReactor of the target.
     * @param _request           The request to be processed.
     * @param _responseProcessor Passed with this request and then returned with the result, the
     *                           AsyncResponseProcessor is used to process the result on the same thread
     *                           that originally invoked this method. If null, then no response is returned.
     * @param <RESPONSE_TYPE>    The type of value returned.
     */
    public static <RESPONSE_TYPE> void doSend(final Reactor _source,
                                              final RequestBase<RESPONSE_TYPE> _request,
                                              final AsyncResponseProcessor<RESPONSE_TYPE> _responseProcessor)
            throws Exception {
        _request.doSend(_source, _responseProcessor);
    }

    /**
     * Assigned to current time when Facility.DEBUG.
     */
    private Long debugTimestamp;

    /**
     * A request can only be used once.
     */
    protected boolean used;

    /**
     * The targetReactor where this Request Object is passed for processing. The thread
     * owned by this targetReactor will process the Request.
     */
    protected final ReactorBase targetReactor;

    /**
     * The source reactor or pender that will receive the results.
     */
    protected MessageSource messageSource;

    /**
     * The message targeted to the source reactor which, when processed,
     * resulted in this message.
     */
    protected Message oldMessage;

    /**
     * The exception handler that was active in the source targetReactor at the time
     * when this message was created.
     */
    protected ExceptionHandler sourceExceptionHandler;

    /**
     * The application object that will process the results.
     */
    protected AsyncResponseProcessor responseProcessor;

    /**
     * True when a response to this message has not yet been determined.
     */
    protected boolean unClosed = true;

    /**
     * True when the request is, directly or indirectly, from an IsolationReactor that awaits a response.
     */
    private boolean isolated;

    /**
     * The response created when this message is applied to the target blades.
     */
    protected Object response;

    /**
     * Create a RequestBase.
     *
     * @param _targetReactor The targetReactor where this Request Objects is passed for processing.
     *                       The thread owned by this targetReactor will process this Request.
     */
    public RequestBase(final Reactor _targetReactor) {
        if (_targetReactor == null) {
            throw new NullPointerException("targetMessageProcessor");
        }
        targetReactor = (ReactorBase) _targetReactor;
    }

    @Override
    public boolean isForeign() {
        return targetReactor != messageSource;
    }

    @Override
    public boolean isSignal() {
        return responseProcessor == SignalResponseProcessor.SINGLETON;
    }

    /**
     * Returns the Reactor to which this Request is bound and to which this Request is to be passed.
     *
     * @return The target Reactor.
     */
    @Override
    public Reactor getTargetReactor() {
        return targetReactor;
    }

    @Override
    public MessageSource getMessageSource() {
        return messageSource;
    }


    /**
     * Marks the request as having been used, or throws an
     * exception if the request was already used.
     */
    protected void use() {
        if (used) {
            throw new IllegalStateException("Already used");
        }
        used = true;
    }

    /**
     * Passes this Request to the target Reactor without any result being passed back.
     * I.E. The signal method results in a 1-way message being passed.
     * If an exception is thrown while processing this Request,
     * that exception is simply logged as a warning.
     */
    public void signal() throws Exception {
        use();
        responseProcessor = SignalResponseProcessor.SINGLETON;
        targetReactor.unbufferedAddMessage(this, false);
    }

    /**
     * Passes this Request together with the AsyncResponseProcessor to the target Reactor.
     * Responses are passed back via the targetReactor of the source blades and processed by the
     * provided AsyncResponseProcessor and any exceptions
     * raised while processing the request are processed by the exception handler active when
     * the doSend method was called.
     *
     * @param _source            The sourceReactor on whose thread this method was invoked and which
     *                           will buffer this Request and subsequently receive the result for
     *                           processing on the same thread.
     * @param _responseProcessor Passed with this request and then returned with the result, the
     *                           AsyncResponseProcessor is used to process the result on the same thread
     *                           that originally invoked this method. If null, then no response is returned.
     */
    private void doSend(final Reactor _source,
                        final AsyncResponseProcessor<RESPONSE_TYPE> _responseProcessor)
            throws Exception {
        final ReactorBase source = (ReactorBase) _source;
        if (PlantImpl.DEBUG) {
            if (source instanceof ThreadBoundReactor) {
                if (Thread.currentThread() instanceof PoolThread) {
                    throw new IllegalStateException("send from wrong thread");
                }
            } else {
                if (source.getThreadReference().get() != Thread.currentThread()) {
                    throw new IllegalStateException("send from wrong thread");
                }
            }
        }
        if (!source.isRunning()) {
            throw new IllegalStateException(
                    "A valid source sourceReactor can not be idle");
        }
        if ((oldMessage != null) && oldMessage.isIsolated()) {
            isolated = true;
        }
        if (source instanceof IsolationReactor) {
            isolated = true;
        }
        if (targetReactor instanceof IsolationReactor) {
            if (isolated && (_responseProcessor != null)) {
                throw new UnsupportedOperationException(
                        "Isolated requests can not be nested, even indirectly.");
            }
            isolated = true;
        }
        use();
        AsyncResponseProcessor<RESPONSE_TYPE> rp = _responseProcessor;
        if (rp == null) {
            rp = (AsyncResponseProcessor<RESPONSE_TYPE>) SignalResponseProcessor.SINGLETON;
        } else {
            addDebugPending();
        }
        messageSource = source;
        oldMessage = source.getCurrentMessage();
        sourceExceptionHandler = source.getExceptionHandler();
        responseProcessor = rp;
        final boolean local = targetReactor == source;
        if (local || !source.buffer(this, targetReactor)) {
            targetReactor.unbufferedAddMessage(this, local);
        }
    }

    /**
     * Passes this Request to the target Reactor and blocks the current thread until
     * a result is returned. The call method sends the message directly without buffering,
     * as there is no targetReactor. The response message is buffered, though thread migration is
     * not possible.
     *
     * @return The result from applying this Request to the target blades.
     * @throws Exception If the result is an exception, it is thrown rather than being returned.
     */
    public RESPONSE_TYPE call() throws Exception {
        use();
        if (Thread.currentThread() instanceof PoolThread) {
            throw new UnsupportedOperationException(
                    "Use of call on a PoolThread can result in a deadlock");
        }
        if (PlantImpl.DEBUG) {
            addDebugPending();
        }
        messageSource = new Pender();
        responseProcessor = CallResponseProcessor.SINGLETON;
        targetReactor.unbufferedAddMessage(this, false);
        return (RESPONSE_TYPE) ((Pender) messageSource).pend();
    }

    /**
     * track pending requests.
     */
    private void addDebugPending() {
        if (PlantImpl.DEBUG) {
            debugTimestamp = System.nanoTime();
            final Facility targetFacility = targetReactor.getFacility();
            final Map<Long, Set<RequestBase>> pendingRequests = targetFacility.pendingRequests;
            Set<RequestBase> nanoSet = pendingRequests.get(debugTimestamp);
            if (nanoSet == null) {
                nanoSet = Collections
                        .newSetFromMap(new ConcurrentHashMap<RequestBase, Boolean>(8, 0.9f, 1));
            }
            pendingRequests.put(debugTimestamp, nanoSet);
        }
    }

    protected void setResponse(final Object _response,
                               final Reactor _activeReactor) {
        ((ReactorBase) _activeReactor).requestEnd(this);
        unClosed = false;
        response = _response;
        if (PlantImpl.DEBUG) {
            final Facility targetFacility = targetReactor.getFacility();
            final Map<Long, Set<RequestBase>> pendingRequests = targetFacility.pendingRequests;
            final Set<RequestBase> nanoSet = pendingRequests
                    .get(debugTimestamp);
            if (nanoSet != null) {
                nanoSet.remove(this);
                if (nanoSet.isEmpty()) {
                    pendingRequests.remove(debugTimestamp);
                }
            }
        }
    }

    /**
     * The processObjectResponse method accepts the response of a request.
     * <p>
     * This method need not be thread-safe, as it
     * is always invoked from the same light-weight thread (targetReactor) that passed the
     * Request.
     * </p>
     *
     * @param _response The response to a request.
     * @return True when this is the first response.
     */
    protected boolean processObjectResponse(final Object _response)
            throws Exception {
        if (PlantImpl.DEBUG) {
            if (targetReactor instanceof ThreadBoundReactor) {
                if (Thread.currentThread() instanceof PoolThread) {
                    final Exception ex = new IllegalStateException(
                            "response from wrong thread");
                    targetReactor.getLogger().error(
                            "response from wrong thread", ex);
                    throw ex;
                }
            } else {
                if (targetReactor.getThreadReference().get() != Thread
                        .currentThread()) {
                    final Exception ex = new IllegalStateException(
                            "response from wrong thread");
                    targetReactor.getLogger().error(
                            "response from wrong thread", ex);
                    throw ex;
                }
            }
        }
        if (!unClosed) {
            return false;
        }
        setResponse(_response, targetReactor);
        if (responseProcessor != SignalResponseProcessor.SINGLETON) {
            messageSource.incomingResponse(RequestBase.this, targetReactor);
        } else {
            if (_response instanceof Throwable) {
                targetReactor.getLogger().warn("Uncaught throwable",
                        (Throwable) _response);
            }
        }
        return true;
    }

    @Override
    public boolean isClosed() {
        return !unClosed;
    }

    @Override
    public boolean isIsolated() {
        return isolated;
    }

    @Override
    public void close() {
        if (!unClosed) {
            return;
        }
        unClosed = false;
        response = new ServiceClosedException();
        if (messageSource != null)
            messageSource.incomingResponse(this, null);
    }

    /**
     * Process a request or the response.
     */
    @Override
    public void eval() {
        if (unClosed) {
            targetReactor.setExceptionHandler(null);
            targetReactor.setCurrentMessage(this);
            targetReactor.requestBegin();
            try {
                processRequestMessage();
            } catch (MigrationException _me) {
                throw _me;
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            } catch (final Exception e) {
                processException(targetReactor, e);
            }
        } else {
            processResponseMessage();
        }
    }

    /**
     * Process a request.
     */
    abstract protected void processRequestMessage() throws Exception;

    /**
     * Process a response.
     */
    protected void processResponseMessage() {
        final ReactorBase sourceMessageProcessor = (ReactorBase) messageSource;
        sourceMessageProcessor.setExceptionHandler(sourceExceptionHandler);
        sourceMessageProcessor.setCurrentMessage(oldMessage);
        if (response instanceof Exception) {
            oldMessage.processException(sourceMessageProcessor,
                    (Exception) response);
            return;
        }
        try {
            responseProcessor.processAsyncResponse(response);
        } catch (final Exception e) {
            oldMessage.processException(sourceMessageProcessor, e);
        }
    }

    @Override
    public void processException(final Reactor _activeReactor,
                                 final Exception _e) {
        final ReactorBase activeMessageProcessor = (ReactorBase) _activeReactor;
        final ExceptionHandler<RESPONSE_TYPE> exceptionHandler = activeMessageProcessor
                .getExceptionHandler();
        if (exceptionHandler != null) {
            try {
                processObjectResponse(exceptionHandler.processException(_e));
            } catch (final Throwable u) {
                if (!(responseProcessor instanceof SignalResponseProcessor)) {
                    if (!unClosed) {
                        return;
                    }
                    setResponse(u, activeMessageProcessor);
                    messageSource
                            .incomingResponse(this, activeMessageProcessor);
                } else {
                    activeMessageProcessor
                            .getLogger()
                            .error("Thrown by exception handler and uncaught "
                                    + exceptionHandler.getClass().getName(), _e);
                }
            }
        } else {
            if (!unClosed) {
                return;
            }
            setResponse(_e, activeMessageProcessor);
            if (!(responseProcessor instanceof SignalResponseProcessor)) {
                messageSource.incomingResponse(this, activeMessageProcessor);
            } else {
                activeMessageProcessor.getLogger().warn("Uncaught throwable",
                        _e);
            }
        }
    }

    /**
     * Replace the current ExceptionHandler with another.
     * <p>
     * When an event or request message is processed by a targetReactor, the current
     * exception handler is set to null. When a request is sent by a targetReactor, the
     * current exception handler is saved in the outgoing message and restored when
     * the response message is processed.
     * </p>
     *
     * @param _exceptionHandler The exception handler to be used now.
     *                          May be null if the default exception handler is to be used.
     * @return The exception handler that was previously in effect, or null if the
     * default exception handler was in effect.
     */
    public ExceptionHandler<RESPONSE_TYPE> setExceptionHandler(
            final ExceptionHandler<RESPONSE_TYPE> _exceptionHandler) {
        final ExceptionHandler<RESPONSE_TYPE> old = targetReactor
                .getExceptionHandler();
        targetReactor.setExceptionHandler(_exceptionHandler);
        return old;
    }

    /**
     * Process the request immediately.
     *
     * @param _syncRequest The request to be processed.
     * @param <RT>         The type of value returned.
     * @return The response from the request.
     */
    protected <RT> RT local(final SyncRequest<RT> _syncRequest)
            throws Exception {
        return SyncRequest.doLocal(targetReactor, _syncRequest);
    }

    @Override
    public String toString() {
        return "facility=" + targetReactor.getFacility().name +
                ", message=" + getClass().getName() +
                ", isClosed=" + isClosed() +
                ", isSignal=" + isSignal() +
                ", source=" + (messageSource == null ? "null" : messageSource.getClass().getName()) +
                ", target=" + getTargetReactor().getClass().getName() +
                ", this=" + super.toString() +
                (oldMessage == null ? "" : "\n" + oldMessage.toString());
    }

    /**
     * Pender is used by the RequestBase.call method to block the current thread until a
     * result is available and then either return the result or rethrow it if the result
     * is an exception.
     */
    private static final class Pender implements MessageSource {

        /**
         * Used to signal the arrival of a response.
         */
        private final Semaphore done = new Semaphore(0);

        /**
         * The result from the incoming response. May be null or an Exception.
         */
        private transient Object result;

        /**
         * Returns the response, which may be null. But if the response
         * is an exception, then it is thrown.
         *
         * @return The response or null, but not an exception.
         */
        Object pend() throws Exception {
            done.acquire();
            if (result instanceof Exception) {
                throw (Exception) result;
            }
            if (result instanceof Error) {
                throw (Error) result;
            }
            return result;
        }

        @Override
        public void incomingResponse(final Message _message,
                                     final Reactor _responseSource) {
            result = ((RequestBase) _message).response;
            done.release();
        }
    }

    /**
     * A subclass of AsyncResponseProcessor that is used as a place holder when the RequestBase.call
     * method is used.
     */
    final private static class CallResponseProcessor implements
            AsyncResponseProcessor<Object> {
        /**
         * The singleton.
         */
        public static final CallResponseProcessor SINGLETON = new CallResponseProcessor();

        /**
         * Restrict the use of this class to being a singleton.
         */
        private CallResponseProcessor() {
        }

        @Override
        public void processAsyncResponse(final Object response) {
            throw new UnsupportedOperationException();
        }
    }
}
