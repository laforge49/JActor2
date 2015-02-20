package org.agilewiki.jactor2.core.messages;

import org.agilewiki.jactor2.core.messages.impl.RequestImpl;

/**
 * A synchronous operation.
 */
public interface SyncOperation<RESPONSE_TYPE> extends Operation<RESPONSE_TYPE> {
    /**
     * The doSync method will be invoked by the target Reactor on its own thread.
     *
     * @return The value returned by the target blades.
     */
    RESPONSE_TYPE doSync(final RequestImpl _requestImpl) throws Exception;
}
