package org.agilewiki.jactor2.core.impl.mtMessages;

import org.agilewiki.jactor2.core.impl.mtReactors.ReactorMtImpl;
import org.agilewiki.jactor2.core.messages.*;
import org.agilewiki.jactor2.core.messages.alt.AsyncNativeRequest;
import org.agilewiki.jactor2.core.messages.alt.SyncNativeRequest;
import org.agilewiki.jactor2.core.messages.impl.AsyncRequestImpl;
import org.agilewiki.jactor2.core.messages.impl.OneWayResponseProcessor;
import org.agilewiki.jactor2.core.messages.impl.RequestImpl;
import org.agilewiki.jactor2.core.plant.impl.MetricsTimer;
import org.agilewiki.jactor2.core.plant.impl.PlantImpl;
import org.agilewiki.jactor2.core.reactors.CommonReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Internal implementation of AsyncRequest.
 *
 * @param <RESPONSE_TYPE> The type of response.
 */
public class AsyncRequestMtImpl<RESPONSE_TYPE> extends
        RequestMtImpl<RESPONSE_TYPE> implements
        AsyncNativeRequest<RESPONSE_TYPE> {

    private boolean noHungRequestCheck;

    private final AsyncOperation<RESPONSE_TYPE> asyncOperation;

    /**
     * Used by the Timer.
     */
    private volatile long start;

    /**
     * The expected number of responses.
     */
    private int expectedResponses = 16;

    /**
     * Create an AsyncRequestMtImpl and bind it to its operation and target targetReactor.
     *
     * @param _asyncOperation The request being implemented.
     * @param _targetReactor  The targetReactor where this AsyncRequest Objects is passed for processing.
     *                        The thread owned by this targetReactor will process this AsyncRequest.
     */
    public AsyncRequestMtImpl(
            final AsyncOperation<RESPONSE_TYPE> _asyncOperation,
            final Reactor _targetReactor) {
        super(_targetReactor);
        asyncOperation = _asyncOperation;
    }

    public AsyncRequestMtImpl(final Reactor _targetReactor) {
        super(_targetReactor);
        asyncOperation = this;
    }

    @Override
    public String getOpName() {
        return asOperation().getOpName();
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
     * Returns true if no subordinate requests have not yet responded.
     *
     * @return true if no subordinate requests have not yet responded.
     */
    @SuppressWarnings("rawtypes")
    @Override
    public final boolean hasNoPendingResponses() {
        final Object pr = get();
        return (pr == null)
                || ((pr instanceof ConcurrentHashMap) && ((ConcurrentHashMap) pr)
                .isEmpty());
    }

    /**
     * Removes a RequestImpl from the pending requests; returns true if present.
     *
     * @param request The request to be removed
     * @return true if present.
     */
    @SuppressWarnings("rawtypes")
    private boolean pendingRequestsRemove(final RequestImpl<?> request) {
        while (true) {
            final Object pendingRequests = get();
            if (pendingRequests != null) {
                if (pendingRequests instanceof RequestImpl) {
                    if (pendingRequests == request) {
                        if (compareAndSet(pendingRequests, null)) {
                            // We just removed it
                            return true;
                        }
                        // Concurrent modification; try again
                        continue;
                    }
                    // Not there
                } else {
                    return ((ConcurrentHashMap) pendingRequests)
                            .remove(request) != null;
                }
            }
            return false;
        }
    }

    /**
     * Adds a RequestImpl to the pending requests.
     *
     * @param request The request to be added
     */
    private void pendingRequestsAdd(final RequestImpl<?> request) {
        while (true) {
            final Object pendingRequests = get();
            if (pendingRequests == null) {
                if (compareAndSet(null, request)) {
                    // We just added it
                    return;
                }
                // Concurrent modification; try again
            } else {
                if (pendingRequests instanceof RequestImpl) {
                    final ConcurrentHashMap<RequestImpl<?>, Boolean> map = new ConcurrentHashMap<RequestImpl<?>, Boolean>(
                            expectedResponses, 0.75f, 4);
                    map.put((RequestImpl<?>) pendingRequests, Boolean.TRUE);
                    map.put(request, Boolean.TRUE);
                    if (compareAndSet(pendingRequests, map)) {
                        // We just added it
                        return;
                    }
                    // Concurrent modification; try again
                } else {
                    final ConcurrentHashMap<RequestImpl<?>, Boolean> map = (ConcurrentHashMap<RequestImpl<?>, Boolean>) pendingRequests;
                    map.put(request, Boolean.TRUE);
                    // We just added it
                    return;
                }
            }
        }
    }

    /**
     * A safe way to copy pendingRequests.
     *
     * @return A copy of pendingRequests.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private List<RequestImpl<?>> copyPendingRequests() {
        final Object pendingRequests = get();
        if (pendingRequests == null) {
            return Collections.emptyList();
        }
        if (pendingRequests instanceof RequestImpl) {
            // Weird generics issue here.
            return (List) Collections.singletonList(pendingRequests);
        }
        final ConcurrentHashMap<RequestImpl<?>, Boolean> map = (ConcurrentHashMap<RequestImpl<?>, Boolean>) pendingRequests;
        final ArrayList<RequestImpl<?>> result = new ArrayList<RequestImpl<?>>(
                (int) (map.size() * 1.1));
        while (true) {
            result.clear();
            try {
                result.addAll(map.keySet());
                while (result.remove(null)) {
                    // NOP
                }
                return result;
            } catch (final Throwable e) {
                // NOP
            }
        }
    }

    /**
     * Sets the "expected" number of pending responses. This is just a hint.
     *
     * @param responses the "expected" number of pending responses.
     */
    @Override
    public final void setExpectedPendingResponses(final int responses) {
        expectedResponses = responses;
    }

    /**
     * Process the response to this request.
     *
     * @param _response The response to this request.
     */
    @Override
    public void processAsyncResponse(final RESPONSE_TYPE _response) {
        final MetricsTimer timer = targetReactor.getMetricsTimer("AOp."+getOpName());
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
        final MetricsTimer timer = targetReactor.getMetricsTimer("AOp."+getOpName());
        timer.updateNanos(timer.nanos() - start, false);
        processObjectResponse(_response);
    }

    private void pendingCheck() throws Exception {
        if (incomplete && !isCanceled() && hasNoPendingResponses()
                && !noHungRequestCheck) {
            targetReactor.asReactorImpl().error("hung request:\n" + toString());
            close();
            targetReactorImpl.getRecovery().onHungRequest(this);
        }
    }

    @Override
    protected void processRequestMessage() throws Exception {
        start = targetReactor.getMetricsTimer("AOp."+getOpName()).nanos();
        asyncOperation.doAsync(this, this);
        pendingCheck();
    }

    @Override
    public void responseReceived(final RequestImpl<?> request) {
        pendingRequestsRemove(request);
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
            pendingRequestsAdd(requestImpl);
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
        pendingRequestsAdd(requestImpl);
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
        for (final RequestImpl<?> request : copyPendingRequests()) {
            ((RequestMtImpl<?>) request).cancel();
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
        // Note: This will be called outside of our own reactor.
        final RequestMtImpl<?> requestImpl = (RequestMtImpl<?>) _requestImpl;
        if (!pendingRequestsRemove(requestImpl)) {
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
        for (final RequestImpl<?> request : copyPendingRequests()) {
            cancel(request);
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
    public <RT> RequestImpl<RT> send(final SOp<RT> _sOp,
                                     final AsyncResponseProcessor<RT> _asyncResponseProcessor) {
        final RequestImpl<RT> ri = PlantImpl.getSingleton()
                .createSyncRequestImpl(_sOp, _sOp.targetReactor);
        send(ri, _asyncResponseProcessor);
        return ri;
    }

    @Override
    public <RT, RT2> RequestImpl<RT> send(final SOp<RT> _sOp,
                                          final AsyncResponseProcessor<RT2> _dis, final RT2 _fixedResponse) {
        final RequestImpl<RT> ri = PlantImpl.getSingleton()
                .createSyncRequestImpl(_sOp, _sOp.targetReactor);
        send(ri, _dis, _fixedResponse);
        return ri;
    }

    @Override
    public <RT> AsyncRequestImpl<RT> send(final AOp<RT> _aOp,
                                          final AsyncResponseProcessor<RT> _asyncResponseProcessor) {
        final AsyncRequestImpl<RT> ari = PlantImpl.getSingleton()
                .createAsyncRequestImpl(_aOp, _aOp.targetReactor);
        send(ari, _asyncResponseProcessor);
        return ari;
    }

    @Override
    public <RT, RT2> AsyncRequestImpl<RT> send(final AOp<RT> _aOp,
                                               final AsyncResponseProcessor<RT2> _dis, final RT2 _fixedResponse) {
        final AsyncRequestImpl<RT> ari = PlantImpl.getSingleton()
                .createAsyncRequestImpl(_aOp, _aOp.targetReactor);
        send(ari, _dis, _fixedResponse);
        return ari;
    }

    @Override
    public <RT> void send(final SyncNativeRequest<RT> _syncNativeRequest,
                          final AsyncResponseProcessor<RT> _asyncResponseProcessor) {
        send(PlantImpl.getSingleton().createSyncRequestImpl(_syncNativeRequest,
                _syncNativeRequest.getTargetReactor()), _asyncResponseProcessor);
    }

    @Override
    public <RT, RT2> void send(final SyncNativeRequest<RT> _syncNativeRequest,
                               final AsyncResponseProcessor<RT2> _dis, final RT2 _fixedResponse) {
        send(PlantImpl.getSingleton().createSyncRequestImpl(_syncNativeRequest,
                _syncNativeRequest.getTargetReactor()), _dis, _fixedResponse);
    }

    @Override
    public <RT> void send(final AsyncNativeRequest<RT> _asyncNativeRequest,
                          final AsyncResponseProcessor<RT> _asyncResponseProcessor) {
        send(PlantImpl.getSingleton().createAsyncRequestImpl(
                        _asyncNativeRequest, _asyncNativeRequest.getTargetReactor()),
                _asyncResponseProcessor);
    }

    @Override
    public <RT, RT2> void send(
            final AsyncNativeRequest<RT> _asyncNativeRequest,
            final AsyncResponseProcessor<RT2> _dis, final RT2 _fixedResponse) {
        send(PlantImpl.getSingleton().createAsyncRequestImpl(
                        _asyncNativeRequest, _asyncNativeRequest.getTargetReactor()),
                _dis, _fixedResponse);
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
    public void doAsync(final AsyncRequestImpl _asyncRequestImpl,
                        final AsyncResponseProcessor<RESPONSE_TYPE> _asyncResponseProcessor)
            throws Exception {
        if (!_asyncRequestImpl.getTargetReactor().asReactorImpl().isRunning())
            throw new IllegalStateException(
                    "Not thread safe: not called from within an active request");
        processAsyncOperation(_asyncRequestImpl, _asyncResponseProcessor);
    }

    /**
     * The processAsyncOperation method will be invoked by the target Reactor on its own thread.
     *
     * @param _asyncRequestImpl       The request context--may be of a different RESPONSE_TYPE.
     * @param _asyncResponseProcessor Handles the response.
     */
    protected void processAsyncOperation(
            final AsyncRequestImpl _asyncRequestImpl,
            final AsyncResponseProcessor<RESPONSE_TYPE> _asyncResponseProcessor)
            throws Exception {
        throw new IllegalStateException();
    }
}
