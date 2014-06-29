package org.agilewiki.jactor2.core.impl.mtRequests;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.agilewiki.jactor2.core.impl.mtReactors.ReactorMtImpl;
import org.agilewiki.jactor2.core.plant.impl.PlantImpl;
import org.agilewiki.jactor2.core.reactors.CommonReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.requests.*;
import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;
import org.agilewiki.jactor2.core.requests.impl.OneWayResponseProcessor;
import org.agilewiki.jactor2.core.requests.impl.RequestImpl;
import org.agilewiki.jactor2.core.util.Timer;

/**
 * Internal implementation of AsyncRequest.
 *
 * @param <RESPONSE_TYPE> The type of response.
 */
public class AsyncRequestMtImpl<RESPONSE_TYPE> extends
        RequestMtImpl<RESPONSE_TYPE> implements AsyncRequestImpl<RESPONSE_TYPE>, AsyncOperation<RESPONSE_TYPE> {

    private final Set<RequestMtImpl<?>> pendingRequests = new HashSet<RequestMtImpl<?>>();

    private boolean noHungRequestCheck;

    private final AsyncOperation<RESPONSE_TYPE> asyncOperation;

    /**
     * Used by the Timer.
     */
    private volatile long start;

    /**
     * Create an AsyncRequestMtImpl and bind it to its operation and target targetReactor.
     *
     * @param _asyncOperation  The request being implemented.
     * @param _targetReactor The targetReactor where this AsyncRequest Objects is passed for processing.
     *                       The thread owned by this targetReactor will process this AsyncRequest.
     */
    public AsyncRequestMtImpl(final AsyncOperation<RESPONSE_TYPE> _asyncOperation,
                              final Reactor _targetReactor) {
        super(_targetReactor);
        asyncOperation = _asyncOperation;
    }

    public AsyncRequestMtImpl(final Reactor _targetReactor) {
        super(_targetReactor);
        asyncOperation = this;
    }

    @Override
    public AsyncOperation<RESPONSE_TYPE> asOperation() {
        return asyncOperation;
    }

    /**
     * Disable check for hung request.
     * This must be called when a response must wait for a subsequent request.
     */
    @Override
    public void setNoHungRequestCheck() {
        noHungRequestCheck = true;
    }

    /**
     * Returns a count of the number of subordinate requests which have not yet responded.
     *
     * @return A count of the number of subordinate requests which have not yet responded.
     */
    @Override
    public int getPendingResponseCount() {
        return pendingRequests.size();
    }

    /**
     * Process the response to this request.
     *
     * @param _response The response to this request.
     */
    @Override
    public void processAsyncResponse(final RESPONSE_TYPE _response) {
        final Timer timer = asyncOperation.getTimer();
        timer.updateNanos(timer.nanos() - start, true);
        processObjectResponse(_response);
    }

    /**
     * Returns an exception as a response instead of throwing it.
     * But regardless of how a response is returned, if the response is an exception it
     * is passed to the exception handler of the request that did the call or send on the request.
     *
     * @param _response An exception.
     */
    @Override
    public void processAsyncException(final Exception _response) {
        final Timer timer = asyncOperation.getTimer();
        timer.updateNanos(timer.nanos() - start, false);
        processObjectResponse(_response);
    }

    private void pendingCheck() throws Exception {
        if (incomplete && !isCanceled() && (pendingRequests.size() == 0)
                && !noHungRequestCheck) {
            targetReactor.asReactorImpl().error("hung request:\n" + toString());
            close();
            targetReactorImpl.getRecovery().onHungRequest(this);
        }
    }

    @Override
    protected void processRequestMessage() throws Exception {
        start = asyncOperation.getTimer().nanos();
        asyncOperation.processAsyncOperation(this, this);
        pendingCheck();
    }

    @Override
    public void responseReceived(final RequestImpl<?> request) {
        pendingRequests.remove(request);
    }

    @Override
    public void responseProcessed() {
        try {
            pendingCheck();
        } catch (final Exception e) {
            processException((ReactorMtImpl) requestSource, e);
        }
    }

    @Override
    public <RT> void send(final RequestImpl<RT> _requestImpl,
                          final AsyncResponseProcessor<RT> _responseProcessor) {
        if (canceled && (_responseProcessor != null)) {
            return;
        }
        if (targetReactorImpl.getCurrentRequest() != this) {
            throw new UnsupportedOperationException(
                    "send called on inactive request");
        }
        final RequestMtImpl<RT> requestImpl = (RequestMtImpl<RT>) _requestImpl;
        if (_responseProcessor != OneWayResponseProcessor.SINGLETON) {
            pendingRequests.add(requestImpl);
        }
        requestImpl.doSend(targetReactorImpl, _responseProcessor);
    }

    @Override
    public <RT, RT2> void send(final RequestImpl<RT> _requestImpl,
                               final AsyncResponseProcessor<RT2> _dis, final RT2 _fixedResponse) {
        if (canceled) {
            return;
        }
        if (targetReactorImpl.getCurrentRequest() != this) {
            throw new UnsupportedOperationException(
                    "send called on inactive request");
        }
        final RequestMtImpl<RT> requestImpl = (RequestMtImpl<RT>) _requestImpl;
        pendingRequests.add(requestImpl);
        requestImpl.doSend(targetReactorImpl, new AsyncResponseProcessor<RT>() {
            @Override
            public void processAsyncResponse(final RT _response)
                    throws Exception {
                _dis.processAsyncResponse(_fixedResponse);
            }
        });
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
    @Override
    public ExceptionHandler<RESPONSE_TYPE> setExceptionHandler(
            final ExceptionHandler<RESPONSE_TYPE> _exceptionHandler) {
        @SuppressWarnings("unchecked")
        final ExceptionHandler<RESPONSE_TYPE> old = (ExceptionHandler<RESPONSE_TYPE>) targetReactorImpl
                .getExceptionHandler();
        targetReactorImpl.setExceptionHandler(_exceptionHandler);
        return old;
    }

    /**
     * Returns the current exception handler.
     *
     * @return The current exception handler, or null.
     */
    @SuppressWarnings("unchecked")
    public ExceptionHandler<RESPONSE_TYPE> getExceptionHandler() {
        return (ExceptionHandler<RESPONSE_TYPE>) targetReactorImpl
                .getExceptionHandler();
    }

    @Override
    public void close() {
        if (!incomplete) {
            return;
        }
        final HashSet<RequestMtImpl<?>> pr = new HashSet<RequestMtImpl<?>>(
                pendingRequests);
        final Iterator<RequestMtImpl<?>> it = pr.iterator();
        while (it.hasNext()) {
            it.next().cancel();
        }
        super.close();
        asOperation().onClose(this);
    }

    /**
     * Cancel a subordinate RequestImpl.
     *
     * @param _requestImpl The subordinate RequestImpl.
     * @return True if the subordinate RequestImpl was canceled.
     */
    @Override
    public boolean cancel(final RequestImpl<?> _requestImpl) {
        final RequestMtImpl<?> requestImpl = (RequestMtImpl<?>) _requestImpl;
        if (!pendingRequests.remove(requestImpl)) {
            return false;
        }
        requestImpl.cancel();
        return true;
    }

    /**
     * Cancel all subordinate RequestImpl's.
     */
    @Override
    public void cancelAll() {
        final Set<RequestImpl<?>> all = new HashSet<RequestImpl<?>>(
                pendingRequests);
        final Iterator<RequestImpl<?>> it = all.iterator();
        while (it.hasNext()) {
            cancel(it.next());
        }
    }

    /**
     * Cancel this request.
     */
    @Override
    public void cancel() {
        if (canceled) {
            return;
        }
        canceled = true;
        asOperation().onCancel(this);
    }

    @Override
    protected void setResponse(final Object _response,
                               final ReactorMtImpl _activeReactor) {
        if ((_response instanceof Throwable)
                || (targetReactor instanceof CommonReactor)) {
            cancelAll();
        }
        super.setResponse(_response, _activeReactor);
    }

    @Override
    public <RT> void send(final SOp<RT> _sOp,
                          final AsyncResponseProcessor<RT> _asyncResponseProcessor) {
        send(PlantImpl.getSingleton().createSyncRequestImpl(_sOp, _sOp.targetReactor), _asyncResponseProcessor);
    }

    @Override
    public <RT, RT2> void send(final SOp<RT> _sOp,
                               final AsyncResponseProcessor<RT2> _dis, final RT2 _fixedResponse) {
        send(PlantImpl.getSingleton().createSyncRequestImpl(_sOp, _sOp.targetReactor), _dis, _fixedResponse);
    }

    @Override
    public <RT> void send(final AOp<RT> _aOp,
                          final AsyncResponseProcessor<RT> _asyncResponseProcessor) {
        send(PlantImpl.getSingleton().createAsyncRequestImpl(_aOp, _aOp.targetReactor), _asyncResponseProcessor);
    }

    @Override
    public <RT, RT2> void send(final AOp<RT> _aOp,
                               final AsyncResponseProcessor<RT2> _dis, final RT2 _fixedResponse) {
        send(PlantImpl.getSingleton().createAsyncRequestImpl(_aOp, _aOp.targetReactor), _dis, _fixedResponse);
    }

    @Override
    public <RT> void asyncDirect(final AOp<RT> _aOp,
                                 final AsyncResponseProcessor<RT> _asyncResponseProcessor)
            throws Exception {
        _aOp.targetReactor.directCheck(getTargetReactor());
        _aOp.processAsyncOperation(this, _asyncResponseProcessor);
    }

    @Override
    public void onCancel(final AsyncRequestImpl _asyncRequestImpl) {
        onCancel();
    }

    /**
     * An optional callback used to signal that the request has been canceled.
     * This method must be thread-safe, as there is no constraint on which
     * thread is used to call it.
     * The default action of onCancel is to call cancelAll and,
     * if the reactor is not a common reactor, sends a response of null via
     * a bound response processor.
     */
    public void onCancel() {
        cancelAll();
        final Reactor targetReactor = getTargetReactor();
        if (!(targetReactor instanceof CommonReactor)) {
            try {
                new BoundResponseProcessor<RESPONSE_TYPE>(targetReactor, this)
                        .processAsyncResponse(null);
            } catch (final Exception e) {
            }
        }
    }

    @Override
    public void onClose(final AsyncRequestImpl _asyncRequestImpl) {
        onClose();
    }

    /**
     * An optional callback used to signal that the request has been closed.
     * This method must be thread-safe, as there is no constraint on which
     * thread is used to call it.
     * By default, onClose does nothing.
     */
    public void onClose() {
    }

    @Override
    public void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl,
                                      final AsyncResponseProcessor<RESPONSE_TYPE> _asyncResponseProcessor)
            throws Exception {
        processAsyncRequest(_asyncResponseProcessor);
    }

    /**
     * The processAsyncRequest method will be invoked by the target Reactor on its own thread.
     */
    public void processAsyncRequest(final AsyncResponseProcessor<RESPONSE_TYPE> _asyncResponseProcessor) throws Exception{
        throw new IllegalStateException();
    }
}
