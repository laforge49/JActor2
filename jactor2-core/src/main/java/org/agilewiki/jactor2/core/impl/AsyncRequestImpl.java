package org.agilewiki.jactor2.core.impl;

import org.agilewiki.jactor2.core.blades.ExceptionHandler;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.Request;
import org.agilewiki.jactor2.core.messages.SignalResponseProcessor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.reactors.ReactorImpl;

public class AsyncRequestImpl<RESPONSE_TYPE> extends
        RequestImplBase<RESPONSE_TYPE> implements AsyncResponseProcessor<RESPONSE_TYPE> {

    private int pendingResponseCount;

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

    public Request asRequest() {
        return asyncRequest;
    }

    public void setNoHungRequestCheck() {
        noHungRequestCheck = true;
    }

    public int getPendingResponseCount() {
        return pendingResponseCount;
    }

    @Override
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
        if (unClosed && pendingResponseCount == 0 && !noHungRequestCheck) {
            targetReactor.getLog().error("hung request:\n" + toString());
            close();
            targetReactorImpl.recovery.hungResponse(this);
        }
    }

    @Override
    protected void processRequestMessage() throws Exception {
        asyncRequest.processAsyncRequest();
        pendingCheck();
    }

    @Override
    protected void processResponseMessage() {
        ((AsyncRequestImpl) oldMessage).pendingResponseCount -= 1;
        super.processResponseMessage();
        try {
            ((AsyncRequestImpl) oldMessage).pendingCheck();
        } catch (Exception e) {
            oldMessage.processException((ReactorImpl) messageSource, e);
        }
    }

    public <RT> void send(final Request<RT> _request,
                          final AsyncResponseProcessor<RT> _responseProcessor)
            throws Exception {
        if (targetReactorImpl.getCurrentMessage() != this)
            throw new UnsupportedOperationException("send called on inactive request");
        if (_responseProcessor != SignalResponseProcessor.SINGLETON)
            pendingResponseCount += 1;
        _request.asRequestImpl().doSend(targetReactorImpl, _responseProcessor);
    }

    public <RT, RT2> void send(final Request<RT> _request,
                               final AsyncResponseProcessor<RT2> _dis, final RT2 _fixedResponse)
            throws Exception {
        if (targetReactorImpl.getCurrentMessage() != this)
            throw new UnsupportedOperationException("send called on inactive request");
        pendingResponseCount += 1;
        _request.asRequestImpl().doSend(targetReactorImpl,
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
        targetReactor.setExceptionHandler(_exceptionHandler);
        return old;
    }
}
