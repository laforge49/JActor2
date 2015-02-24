package org.agilewiki.jactor2.core.messages.impl;

import org.agilewiki.jactor2.core.messages.*;
import org.agilewiki.jactor2.core.messages.alt.AsyncNativeRequest;
import org.agilewiki.jactor2.core.messages.alt.SyncNativeRequest;

public interface AsyncRequestImpl<RESPONSE_TYPE> extends
        RequestImpl<RESPONSE_TYPE>, AsyncResponseProcessor<RESPONSE_TYPE> {
    /**
     * Process the response to this request.
     *
     * @param _response The response to this request.
     */
    @Override
    void processAsyncResponse(final RESPONSE_TYPE _response);

    /**
     * Disable check for hung request.
     * This must be called when a response must wait for a subsequent request.
     */
    void setNoHungRequestCheck();

    /**
     * Returns true if no subordinate requests have not yet responded.
     *
     * @return true if no subordinate requests have not yet responded.
     */
    boolean hasNoPendingResponses();

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
    ExceptionHandler<RESPONSE_TYPE> setExceptionHandler(
            final ExceptionHandler<RESPONSE_TYPE> _exceptionHandler);

    /**
     * Send a subordinate request, providing the originating request is not canceled.
     *
     * @param _requestImpl       The subordinate request.
     * @param _responseProcessor A callback to handle the result value from the subordinate request.
     * @param <RT>               The type of result value.
     */
    <RT> void send(final RequestImpl<RT> _requestImpl,
            final AsyncResponseProcessor<RT> _responseProcessor);

    /**
     * Send a subordinate request, providing the originating request is not canceled.
     *
     * @param _requestImpl   The subordinate request.
     * @param _dis           The callback to handle a fixed response when the result of
     *                       the subordinate request is received.
     * @param _fixedResponse The fixed response to be used.
     * @param <RT>           The response value type of the subordinate request.
     * @param <RT2>          The fixed response type.
     */
    <RT, RT2> void send(final RequestImpl<RT> _requestImpl,
            final AsyncResponseProcessor<RT2> _dis, final RT2 _fixedResponse);

    /**
     * Cancel a subordinate RequestImpl.
     *
     * @param _requestImpl The subordinate RequestImpl.
     * @return True if the subordinate RequestImpl was canceled.
     */
    boolean cancel(RequestImpl<?> _requestImpl);

    /**
     * Cancel all subordinate RequestImpl's.
     */
    void cancelAll();

    /**
     * Pass a request to its target reactor, providing the originating request is not canceled.
     *
     * @param _sOp                    A synchronous operation, optionally used to define a SyncRequest.
     * @param _asyncResponseProcessor Handles the response.
     * @param <RT>                    The type of response returned.
     */
    <RT> RequestImpl<RT> send(final SOp<RT> _sOp,
            final AsyncResponseProcessor<RT> _asyncResponseProcessor);

    /**
     * Pass a request to its target and then replace its response value,
     * providing the originating request is not canceled.
     * Useful when you do not care about the actual response being passed back.
     *
     * @param _sOp           A synchronous operation, optionally used to define a SyncRequest.
     * @param _dis           The callback to be invoked when a response value is received.
     * @param _fixedResponse The replacement value.
     * @param <RT>           The response value type.
     * @param <RT2>          The replacement value type.
     */
    <RT, RT2> RequestImpl<RT> send(final SOp<RT> _sOp,
            final AsyncResponseProcessor<RT2> _dis, final RT2 _fixedResponse);

    /**
     * Pass a request to its target reactor, providing the originating request is not canceled.
     *
     * @param _aOp                    An asynchronous operation, optionally used to define an AsyncRequest.
     * @param _asyncResponseProcessor Handles the response.
     * @param <RT>                    The type of response returned.
     */
    <RT> AsyncRequestImpl<RT> send(final AOp<RT> _aOp,
            final AsyncResponseProcessor<RT> _asyncResponseProcessor);

    /**
     * Pass a request to its target and then replace its response value,
     * providing the originating request is not canceled.
     * Useful when you do not care about the actual response being passed back.
     *
     * @param _aOp           An asynchronous operation, optionally used to define an AsyncRequest.
     * @param _dis           The callback to be invoked when a response value is received.
     * @param _fixedResponse The replacement value.
     * @param <RT>           The response value type.
     * @param <RT2>          The replacement value type.
     */
    <RT, RT2> AsyncRequestImpl<RT> send(final AOp<RT> _aOp,
            final AsyncResponseProcessor<RT2> _dis, final RT2 _fixedResponse);

    <RT> void send(final SyncNativeRequest<RT> _syncNativeRequest,
            final AsyncResponseProcessor<RT> _asyncResponseProcessor);

    <RT, RT2> void send(final SyncNativeRequest<RT> _syncNativeRequest,
            final AsyncResponseProcessor<RT2> _dis, final RT2 _fixedResponse);

    <RT> void send(final AsyncNativeRequest<RT> _asyncNativeRequest,
            final AsyncResponseProcessor<RT> _asyncResponseProcessor);

    <RT, RT2> void send(final AsyncNativeRequest<RT> _asyncNativeRequest,
            final AsyncResponseProcessor<RT2> _dis, final RT2 _fixedResponse);

    /**
     * An optional callback used to signal that the request has been canceled.
     * This method must be thread-safe, as there is no constraint on which
     * thread is used to call it.
     * The default action of onCancel is to call cancelAll and,
     * if the reactor is not a common reactor, sends a response of null via
     * a bound response processor.
     */
    void onCancel(final AsyncRequestImpl _asyncRequestImpl);

    /**
     * An optional callback used to signal that the request has been closed.
     * This method must be thread-safe, as there is no constraint on which
     * thread is used to call it.
     * By default, onClose does nothing.
     */
    void onClose(final AsyncRequestImpl _asyncRequestImpl);

    /**
     * Sets the "expected" number of pending responses. This is just a hint.
     *
     * @param responses the "expected" number of pending responses.
     */
    void setExpectedPendingResponses(int responses);
}
