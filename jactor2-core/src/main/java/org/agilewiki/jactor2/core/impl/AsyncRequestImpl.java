package org.agilewiki.jactor2.core.impl;

import org.agilewiki.jactor2.core.reactors.CommonReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.requests.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class AsyncRequestImpl<RESPONSE_TYPE> extends
        RequestImplBase<RESPONSE_TYPE> {

    private Set<RequestImpl> pendingRequests = new HashSet<RequestImpl>();

    private boolean noHungRequestCheck;

    private final AsyncRequest<RESPONSE_TYPE> asyncRequest;

    /**
     * Create an AsyncRequest and bind it to its target targetReactor.
     *
     * @param _targetReactor The targetReactor where this AsyncRequest Objects is passed for processing.
     *                       The thread owned by this targetReactor will process this AsyncRequest.
     */
    public AsyncRequestImpl(final AsyncRequest<RESPONSE_TYPE> _asyncRequest, final Reactor _targetReactor) {
        super(_targetReactor);
        asyncRequest = _asyncRequest;
    }

    public AsyncRequest asRequest() {
        return asyncRequest;
    }

    public void setNoHungRequestCheck() {
        noHungRequestCheck = true;
    }

    public int getPendingResponseCount() {
        return pendingRequests.size();
    }

    public void processAsyncResponse(final RESPONSE_TYPE _response)
            throws Exception {
        processObjectResponse(_response);
    }

    /**
     * Returns an exception as a response instead of throwing it.
     * But regardless of how a response is returned, if the response is an exception it
     * is passed to the exception handler of the blades that did the call or doSend on the request.
     *
     * @param _response An exception.
     */
    public void processAsyncException(final Exception _response)
            throws Exception {
        processObjectResponse(_response);
    }

    private void pendingCheck() throws Exception {
        if (incomplete && !isCanceled() && pendingRequests.size() == 0 && !noHungRequestCheck) {
            targetReactor.asReactorImpl().getLogger().error("hung request:\n" + toString());
            close();
            targetReactorImpl.recovery.onHungRequest(this);
        }
    }

    @Override
    protected void processRequestMessage() throws Exception {
        asyncRequest.processAsyncRequest();
        pendingCheck();
    }

    @Override
    public void responseReceived(RequestImpl request) {
        pendingRequests.remove(request);
    }

    @Override
    public void responseProcessed() {
        try {
            pendingCheck();
        } catch (Exception e) {
            processException((ReactorImpl) requestSource, e);
        }
    }

    public <RT> void send(final Request<RT> _request,
                          final AsyncResponseProcessor<RT> _responseProcessor)
            throws Exception {
        if (targetReactorImpl.getCurrentRequest() != this)
            throw new UnsupportedOperationException("send called on inactive request");
        RequestImpl<RT> requestImpl = _request.asRequestImpl();
        if (_responseProcessor != EventResponseProcessor.SINGLETON)
            pendingRequests.add(requestImpl);
        requestImpl.doSend(targetReactorImpl, _responseProcessor);
    }

    public <RT, RT2> void send(final Request<RT> _request,
                               final AsyncResponseProcessor<RT2> _dis, final RT2 _fixedResponse)
            throws Exception {
        if (targetReactorImpl.getCurrentRequest() != this)
            throw new UnsupportedOperationException("send called on inactive request");
        RequestImpl<RT> requestImpl = _request.asRequestImpl();
        pendingRequests.add(requestImpl);
        requestImpl.doSend(targetReactorImpl,
                new AsyncResponseProcessor<RT>() {
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
    public ExceptionHandler<RESPONSE_TYPE> setExceptionHandler(
            final ExceptionHandler<RESPONSE_TYPE> _exceptionHandler) {
        final ExceptionHandler<RESPONSE_TYPE> old = targetReactorImpl
                .getExceptionHandler();
        targetReactorImpl.setExceptionHandler(_exceptionHandler);
        return old;
    }

    public ExceptionHandler<RESPONSE_TYPE> getExceptionHandler() {
        return targetReactorImpl.getExceptionHandler();
    }

    @Override
    public void close() {
        if (!incomplete) {
            return;
        }
        HashSet<RequestImpl> pr = new HashSet<RequestImpl>(pendingRequests);
        Iterator<RequestImpl> it = pr.iterator();
        while (it.hasNext()) {
            it.next().cancel();
        }
        super.close();
        asRequest().onClose();
    }

    public boolean cancel(RequestImpl _requestImpl) {
        if (!pendingRequests.remove(_requestImpl))
            return false;
        _requestImpl.cancel();
        return true;
    }

    public void cancelAll() {
        Set<RequestImpl> all = new HashSet<RequestImpl>(pendingRequests);
        Iterator<RequestImpl> it = all.iterator();
        while (it.hasNext()) {
            cancel(it.next());
        }
    }

    public void cancel() {
        if (canceled)
            return;
        canceled = true;
        asRequest().onCancel();
        Reactor targetReactor = getTargetReactor();
        if (!(targetReactor instanceof CommonReactor))
            try {
                new BoundResponseProcessor<RESPONSE_TYPE>(targetReactor, asRequest()).processAsyncResponse(null);
            } catch (final Exception e) {
            }
    }
}
