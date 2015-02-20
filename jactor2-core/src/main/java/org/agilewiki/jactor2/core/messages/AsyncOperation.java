package org.agilewiki.jactor2.core.messages;

import org.agilewiki.jactor2.core.messages.impl.AsyncRequestImpl;

/**
 * An asynchronous operation.
 */
public interface AsyncOperation<RESPONSE_TYPE> extends Operation<RESPONSE_TYPE> {

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
     * The doAsync method will be invoked by the target Reactor on its own thread.
     *
     * @param _asyncRequestImpl       The request context--may be of a different RESPONSE_TYPE.
     * @param _asyncResponseProcessor Handles the response.
     */
    void doAsync(final AsyncRequestImpl _asyncRequestImpl,
                 final AsyncResponseProcessor<RESPONSE_TYPE> _asyncResponseProcessor)
            throws Exception;
}
