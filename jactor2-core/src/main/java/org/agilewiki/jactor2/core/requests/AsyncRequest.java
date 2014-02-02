package org.agilewiki.jactor2.core.requests;

import org.agilewiki.jactor2.core.impl.AsyncRequestImpl;
import org.agilewiki.jactor2.core.impl.ReactorImpl;
import org.agilewiki.jactor2.core.impl.RequestSource;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.reactors.ReactorClosedException;

public abstract class AsyncRequest<RESPONSE_TYPE> implements Request<RESPONSE_TYPE>,
        AsyncResponseProcessor<RESPONSE_TYPE> {

    private final AsyncRequestImpl<RESPONSE_TYPE> asyncRequestImpl;

    /**
     * Create an AsyncRequest and bind it to its target targetReactor.
     *
     * @param _targetReactor The targetReactor where this AsyncRequest Objects is passed for processing.
     *                       The thread owned by this targetReactor will process this AsyncRequest.
     */
    public AsyncRequest(final Reactor _targetReactor) {
        asyncRequestImpl = new AsyncRequestImpl<RESPONSE_TYPE>(this, _targetReactor);
    }

    /**
     * The processAsyncRequest method will be invoked by the target Reactor on its own thread
     * when the AsyncRequest is dequeued from the target inbox for processing.
     */
    abstract public void processAsyncRequest() throws Exception;

    @Override
    public AsyncRequestImpl<RESPONSE_TYPE> asRequestImpl() {
        return asyncRequestImpl;
    }

    @Override
    public Reactor getTargetReactor() {
        return asyncRequestImpl.getTargetReactor();
    }

    @Override
    public Reactor getSourceReactor() {
        RequestSource requestSource = asRequestImpl().getRequestSource();
        if (requestSource instanceof ReactorImpl)
            return ((ReactorImpl) requestSource).asReactor();
        return null;
    }

    protected void setNoHungRequestCheck() {
        asyncRequestImpl.setNoHungRequestCheck();
    }

    public int getPendingResponseCount() {
        return asyncRequestImpl.getPendingResponseCount();
    }

    @Override
    public void processAsyncResponse(final RESPONSE_TYPE _response)
            throws Exception {
        asyncRequestImpl.processAsyncResponse(_response);
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
        asyncRequestImpl.processAsyncException(_response);
    }

    public <RT> void send(final Request<RT> _request,
                          final AsyncResponseProcessor<RT> _responseProcessor)
            throws Exception {
        asyncRequestImpl.send(_request, _responseProcessor);
    }

    public <RT, RT2> void send(final Request<RT> _request,
                               final AsyncResponseProcessor<RT2> _dis, final RT2 _fixedResponse)
            throws Exception {
        asyncRequestImpl.send(_request, _dis, _fixedResponse);
    }

    @Override
    public void signal() throws Exception {
        asyncRequestImpl.signal();
    }

    @Override
    public RESPONSE_TYPE call() throws Exception {
        return asyncRequestImpl.call();
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
        return asyncRequestImpl.setExceptionHandler(_exceptionHandler);
    }

    public boolean cancel(final Request _request) {
        return asyncRequestImpl.cancel(_request.asRequestImpl());
    }

    public void cancelAll() {
        asyncRequestImpl.cancelAll();
    }

    @Override
    public boolean isCanceled() throws ReactorClosedException {
        return asyncRequestImpl.isCanceled();
    }

    @Override
    public void onCancel() {}

    @Override
    public void onClose() {}
}
