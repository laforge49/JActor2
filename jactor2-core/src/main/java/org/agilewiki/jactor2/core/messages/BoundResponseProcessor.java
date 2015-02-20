package org.agilewiki.jactor2.core.messages;

import org.agilewiki.jactor2.core.blades.Blade;
import org.agilewiki.jactor2.core.messages.impl.RequestImpl;

/**
 * A thread-safe wrapper for an AsyncResponseProcessor.
 * When a request is processed, the AsyncResponseProcessor given must only be used by the
 * same thread that is processing the request. In contrast, the processAsyncResponse method
 * of BoundResponseProcessor can be called from any thread.
 *
 * @param <RESPONSE_TYPE>
 */
public class BoundResponseProcessor<RESPONSE_TYPE> implements
        AsyncResponseProcessor<RESPONSE_TYPE> {
    /**
     * The processing on whose thread the wrapped AsyncResponseProcessor object can be used.
     */
    private final Blade targetBlade;

    /**
     * The wrapped AsyncResponseProcessor.
     */
    private final AsyncResponseProcessor<RESPONSE_TYPE> rp;

    /**
     * Create a thread-safe wrapper for a AsyncResponseProcessor.
     *
     * @param _blade The blades which can process the AsyncResponseProcessor.
     * @param _rp    The wrapped AsyncResponseProcessor.
     */
    public BoundResponseProcessor(final Blade _blade,
            final AsyncResponseProcessor<RESPONSE_TYPE> _rp) {
        targetBlade = _blade;
        rp = _rp;
    }

    /**
     * This method processes the response by immediately passing the wrapped response and AsyncResponseProcessor
     * back to the appropriate reactor using a signal.
     *
     * @param rsp The response.
     */
    @Override
    public void processAsyncResponse(final RESPONSE_TYPE rsp) {
        new SIOp<Void>("boundSignal", targetBlade.getReactor()) {
            @Override
            protected Void processSyncOperation(RequestImpl _requestImpl) throws Exception {
                rp.processAsyncResponse(rsp);
                return null;
            }
        }.signal();
    }
}
