package org.agilewiki.jactor2.core.requests;

import org.agilewiki.jactor2.core.reactors.CommonReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

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
}
