package org.agilewiki.jactor2.core.requests;

public interface AsyncRequestImpl<RESPONSE_TYPE> extends
        RequestImpl<RESPONSE_TYPE> {
    /**
     * Process the response to this request.
     *
     * @param _response    The response to this request.
     */
    void processAsyncResponse(final RESPONSE_TYPE _response);

    /**
     * Disable check for hung request.
     * This must be called when a response must wait for a subsequent request.
     */
    void setNoHungRequestCheck();

    /**
     * Returns a count of the number of subordinate requests which have not yet responded.
     *
     * @return A count of the number of subordinate requests which have not yet responded.
     */
    int getPendingResponseCount();

    /**
     * Returns an exception as a response instead of throwing it.
     * But regardless of how a response is returned, if the response is an exception it
     * is passed to the exception handler of the request that did the call or send on the request.
     *
     * @param _response An exception.
     */
    void processAsyncException(final Exception _response);

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
            final ExceptionHandler<RESPONSE_TYPE> _exceptionHandler);

    /**
     * Send a subordinate request, providing the originating request is not canceled.
     *
     * @param _request              The subordinate request.
     * @param _responseProcessor    A callback to handle the result value from the subordinate request.
     * @param <RT>                  The type of result value.
     */
    <RT> void send(final Request<RT> _request,
            final AsyncResponseProcessor<RT> _responseProcessor);

    /**
     * Send a subordinate request, providing the originating request is not canceled.
     *
     * @param _request              The subordinate request.
     * @param _dis                  The callback to handle a fixed response when the result of
     *                              the subordinate request is received.
     * @param _fixedResponse        The fixed response to be used.
     * @param <RT>                  The response value type of the subordinate request.
     * @param <RT2>                 The fixed response type.
     */
    <RT, RT2> void send(final Request<RT> _request,
            final AsyncResponseProcessor<RT2> _dis, final RT2 _fixedResponse);

    /**
     * Cancel a subordinate RequestImpl.
     *
     * @param _requestImpl The subordinate RequestImpl.
     * @return True if the subordinate RequestImpl was canceled.
     */
    public boolean cancel(RequestImpl _requestImpl);

    /**
     * Cancel all subordinate RequestImpl's.
     */
    public void cancelAll();
}
