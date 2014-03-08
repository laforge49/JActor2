package org.agilewiki.jactor2.core.requests.impl;

import org.agilewiki.jactor2.core.plant.ReactorPoolThread;
import org.agilewiki.jactor2.core.plant.impl.PlantImpl;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.reactors.ReactorClosedException;
import org.agilewiki.jactor2.core.reactors.impl.IsolationReactorImpl;
import org.agilewiki.jactor2.core.reactors.impl.MigrationException;
import org.agilewiki.jactor2.core.reactors.impl.ReactorImpl;
import org.agilewiki.jactor2.core.reactors.impl.ThreadBoundReactorImpl;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.ExceptionHandler;

import java.util.concurrent.Semaphore;

/**
 * Base class for internal reactor implementations.
 *
 * @param <RESPONSE_TYPE>
 */
public abstract class RequestImplBase<RESPONSE_TYPE> implements RequestImpl<RESPONSE_TYPE> {

    /**
     * Assigned to current time when Facility.DEBUG.
     */
    private Long debugTimestamp;

    /**
     * A request can only be used once.
     */
    protected boolean used;

    /**
     * The reactor where this Request Object is passed for processing. The thread
     * owned by this targetReactor will process the Request.
     */
    protected final Reactor targetReactor;

    /**
     * The reactor impl where this Request Object is passed for processing. The thread
     * owned by this reactor impl will process the Request.
     */
    protected final ReactorImpl targetReactorImpl;

    /**
     * The source reactor or Pender that will receive the results.
     */
    protected RequestSource requestSource;

    /**
     * The request targeted to the source reactor which, when processed,
     * resulted in this message.
     */
    protected RequestImpl oldMessage;

    /**
     * The exception handler that was active in the source reactor
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
    protected boolean incomplete = true;

    /**
     * True when this reactor impl is closed.
     */
    protected boolean closed = false;

    /**
     * True when the request is, directly or indirectly, from an IsolationReactor that awaits a response.
     */
    private boolean isolated;

    /**
     * The response created when this request impl is evaluated.
     */
    protected Object response;

    /**
     * True when this request impl has been canceled.
     */
    protected boolean canceled;

    /**
     * Create a RequestImplBase.
     *
     * @param _targetReactor The targetReactor where this Request Object is passed for processing.
     *                       The thread owned by this reactor will process this Request.
     */
    public RequestImplBase(final Reactor _targetReactor) {
        if (_targetReactor == null) {
            throw new NullPointerException("targetMessageProcessor");
        }
        targetReactor = _targetReactor;
        targetReactorImpl = targetReactor.asReactorImpl();
    }

    @Override
    public boolean isForeign() {
        return targetReactor != requestSource;
    }

    @Override
    public boolean isOneWay() {
        return responseProcessor == OneWayResponseProcessor.SINGLETON ||
                responseProcessor == SignalResponseProcessor.SINGLETON;
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
    public ReactorImpl getTargetReactorImpl() {
        return targetReactorImpl;
    }

    public Reactor getTargetReactor() {
        return targetReactor;
    }

    @Override
    public RequestSource getRequestSource() {
        return requestSource;
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
    public void signal() {
        use();
        responseProcessor = SignalResponseProcessor.SINGLETON;
        targetReactorImpl.unbufferedAddMessage(this, false);
    }

    /**
     * Passes this RequestImpl together with the AsyncResponseProcessor to the target Reactor.
     * Responses are passed back via the source reactor and processed by the
     * provided AsyncResponseProcessor. Any exceptions
     * raised while processing the request are processed by the exception handler active when
     * the doSend method was called.
     *
     * @param _source            The source reactor impl on whose thread this method was invoked and which
     *                           will buffer this Request and subsequently receive the result for
     *                           processing on the same thread.
     * @param _responseProcessor Passed with this request and then returned with the result, the
     *                           AsyncResponseProcessor is used to process the result on the same thread
     *                           that originally invoked this method. If null, then no response is returned.
     */
    public void doSend(final ReactorImpl _source,
                       final AsyncResponseProcessor<RESPONSE_TYPE> _responseProcessor) {
        final ReactorImpl source = (ReactorImpl) _source;
        if (PlantImpl.DEBUG && source.getThreadReference().get() != Thread.currentThread()) {
            throw new IllegalStateException("send from wrong thread");
        }
        if (!source.isRunning()) {
            throw new IllegalStateException(
                    "A valid source sourceReactor can not be idle");
        }
        if ((oldMessage != null) && oldMessage.isIsolated()) {
            isolated = true;
        }
        if (source instanceof IsolationReactorImpl) {
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
            rp = (AsyncResponseProcessor<RESPONSE_TYPE>) OneWayResponseProcessor.SINGLETON;
        }
        requestSource = source;
        oldMessage = source.getCurrentRequest();
        sourceExceptionHandler = source.getExceptionHandler();
        responseProcessor = rp;
        final boolean local = targetReactor == source;
        if (local || !source.buffer(this, targetReactorImpl)) {
            targetReactorImpl.unbufferedAddMessage(this, local);
        }
    }

    /**
     * Passes this Request to the target Reactor and blocks the current thread until
     * a result is returned. The call method sends the message directly without buffering,
     * as there is no source reactor. The response message is buffered, though thread migration is
     * not possible.
     *
     * @return The response value from applying this Request to the target reactor.
     * @throws Exception If the result is an exception, it is thrown rather than being returned.
     */
    public RESPONSE_TYPE call() throws Exception {
        use();
        if (Thread.currentThread() instanceof ReactorPoolThread) {
            throw new UnsupportedOperationException(
                    "Use of call on a ReactorPoolThread can result in a deadlock");
        } else if (PlantImpl.DEBUG && ThreadBoundReactorImpl.threadReactor() != null)
            throw new UnsupportedOperationException(
                    "Use of call on a Thread bound to a reactor can result in a deadlock " + ThreadBoundReactorImpl.threadReactor());
        requestSource = new Pender();
        responseProcessor = CallResponseProcessor.SINGLETON;
        targetReactorImpl.unbufferedAddMessage(this, false);
        return (RESPONSE_TYPE) ((Pender) requestSource).pend();
    }

    /**
     * Assigns a response value.
     *
     * @param _response         The response value.
     * @param _activeReactor    The responding reactor.
     */
    protected void setResponse(final Object _response,
                               final ReactorImpl _activeReactor) {
        _activeReactor.requestEnd(this);
        incomplete = false;
        response = _response;
    }

    /**
     * The processObjectResponse method accepts the response value of a request.
     * <p>
     * This method need not be thread-safe, as it
     * is always invoked from the same light-weight thread (target reactor) that received the
     * Request.
     * </p>
     *
     * @param _response The response to a request.
     * @return True when this is the first response.
     */
    protected boolean processObjectResponse(final Object _response) {
        if (PlantImpl.DEBUG && targetReactorImpl.getThreadReference().get() != Thread
                .currentThread()) {
            final IllegalStateException ex = new IllegalStateException(
                    "response from wrong thread");
            targetReactor.asReactorImpl().getLogger().error(
                    "response from wrong thread", ex);
            throw ex;
        }
        if (!incomplete) {
            return false;
        }
        setResponse(_response, targetReactorImpl);
        if (!isOneWay()) {
            requestSource.incomingResponse(RequestImplBase.this, targetReactorImpl);
        } else {
            if (_response instanceof Throwable) {
                targetReactor.asReactorImpl().getLogger().warn("Uncaught throwable",
                        (Throwable) _response);
            }
        }
        return true;
    }

    @Override
    public boolean isCanceled() throws ReactorClosedException {
        if (closed)
            throw new ReactorClosedException();
        return canceled;
    }

    @Override
    public boolean _isCanceled() {
        return canceled;
    }

    @Override
    public boolean isComplete() {
        return !incomplete;
    }

    @Override
    public boolean isIsolated() {
        return isolated;
    }

    @Override
    public void close() {
        if (!incomplete) {
            return;
        }
        incomplete = false;
        closed = true;
        response = new ReactorClosedException();
        if (requestSource != null)
            requestSource.incomingResponse(this, null);
    }

    /**
     * Cancel this request.
     */
    public void cancel() {
        if (canceled)
            return;
        canceled = true;
    }

    /**
     * Process a request or the response.
     */
    @Override
    public void eval() {
        if (incomplete) {
            targetReactorImpl.setExceptionHandler(null);
            targetReactorImpl.setCurrentRequest(this);
            targetReactorImpl.requestBegin(this);
            try {
                processRequestMessage();
            } catch (MigrationException _me) {
                throw _me;
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            } catch (final RuntimeException re) {
                processException(targetReactorImpl, new ReactorClosedException());
                targetReactor.getRecovery().onRuntimeException(this, re);
            } catch (final Exception e) {
                processException(targetReactorImpl, e);
            } catch (final StackOverflowError soe) {
                processException(targetReactorImpl, new ReactorClosedException());
                targetReactor.getRecovery().onStackOverflowError(this, soe);
            }
        } else {
            processResponseMessage();
        }
    }

    /**
     * Process a request.
     */
    abstract protected void processRequestMessage() throws Exception;

    @Override
    public void responseReceived(RequestImpl request) {
    }

    @Override
    public void responseProcessed() {
    }

    /**
     * Process a response.
     */
    protected void processResponseMessage() {
        oldMessage.responseReceived(this);
        final ReactorImpl sourceMessageProcessor = (ReactorImpl) requestSource;
        sourceMessageProcessor.setExceptionHandler(sourceExceptionHandler);
        sourceMessageProcessor.setCurrentRequest(oldMessage);
        if (response instanceof Exception) {
            oldMessage.processException(sourceMessageProcessor,
                    (Exception) response);
            oldMessage.responseProcessed();
            return;
        }
        try {
            responseProcessor.processAsyncResponse(response);
        } catch (final Exception e) {
            oldMessage.processException(sourceMessageProcessor, e);
        }
        oldMessage.responseProcessed();
    }

    @Override
    public void processException(final ReactorImpl _activeReactor,
                                 final Exception _e) {
        final ReactorImpl activeMessageProcessor = _activeReactor;
        final ExceptionHandler<RESPONSE_TYPE> exceptionHandler = activeMessageProcessor
                .getExceptionHandler();
        if (exceptionHandler != null) {
            try {
                exceptionHandler.processException(_e, new AsyncResponseProcessor() {
                    @Override
                    public void processAsyncResponse(Object _response) {
                        processObjectResponse(_response);
                    }
                });
            } catch (final Throwable u) {
                if (!isOneWay()) {
                    if (!incomplete) {
                        return;
                    }
                    setResponse(u, activeMessageProcessor);
                    requestSource
                            .incomingResponse(this, activeMessageProcessor);
                } else {
                    activeMessageProcessor
                            .getLogger()
                            .error("Thrown by exception handler and uncaught "
                                    + exceptionHandler.getClass().getName(), _e);
                }
            }
        } else {
            if (!incomplete) {
                return;
            }
            setResponse(_e, activeMessageProcessor);
            if (!isOneWay()) {
                requestSource.incomingResponse(this, activeMessageProcessor);
            } else {
                activeMessageProcessor.getLogger().warn("Uncaught throwable",
                        _e);
            }
        }
    }

    @Override
    public String toString() {
        return "message=" + asRequest() +
                ", isComplete=" + isComplete() +
                ", isOneWay=" + isOneWay() +
                ", source=" + (requestSource == null ? "null" : requestSource) +
                ", target=" + getTargetReactor().asReactorImpl() +
                ", this=" + super.toString() +
                (oldMessage == null ? "" : "\n" + oldMessage.toString());
    }

    /**
     * Pender is used by the RequestImplBase.call method to block the current thread until a
     * result is available and then either return the result or rethrow it if the result
     * is an exception.
     */
    private static final class Pender implements RequestSource {

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
        public void incomingResponse(final RequestImpl _message,
                                     final ReactorImpl _responseSource) {
            result = ((RequestImplBase) _message).response;
            done.release();
        }
    }

    /**
     * A subclass of AsyncResponseProcessor that is used as a place holder when the RequestImplBase.call
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
