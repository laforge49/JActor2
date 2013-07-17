package org.agilewiki.jactor2.util;

import org.agilewiki.jactor2.api.Actor;
import org.agilewiki.jactor2.api.EventBase;
import org.agilewiki.jactor2.api.ResponseProcessor;

/**
 * A thread-safe wrapper for ResponseProcessor.
 * When a request is processed, the ResponseProcessor given must only be used by the
 * same thread that is processing the request. In contrast, the processResult method
 * of BoundResponseProcessor can be called from any thread.
 *
 * @param <RESPONSE_TYPE>
 */
public class BoundResponseProcessor<RESPONSE_TYPE> implements
        ResponseProcessor<RESPONSE_TYPE> {
    /**
     * The mailbox on whose thread the wrapped ResponseProcessor object can be used.
     */
    private final Actor targetActor;

    /**
     * The wrapped ResponseProcessor.
     */
    private final ResponseProcessor<RESPONSE_TYPE> rp;

    /**
     * Create a thread-safe wrapper for a ResponseProcessor.
     *
     * @param _actor The mailbox on whose thread the wrapped ResponseProcessor
     *               can be used.
     * @param _rp    The wrapped ResponseProcessor.
     */
    public BoundResponseProcessor(final Actor _actor,
                                  final ResponseProcessor<RESPONSE_TYPE> _rp) {
        targetActor = _actor;
        rp = _rp;
    }

    /**
     * This method processes the response by immediately passing the wrapped response and ResponseProcessor
     * via an unbuffered signal back to the appropriate mailbox.
     *
     * @param rsp The response.
     */
    @Override
    public void processResponse(final RESPONSE_TYPE rsp) throws Exception {
        new ContinuationEvent<RESPONSE_TYPE>(rp, rsp).signal(targetActor);
    }
}

/**
 * The request used to pass the response and the wrapped ResponseProcessor back to the
 * original target mailbox.
 *
 * @param <RESPONSE_TYPE> The type of response.
 */
class ContinuationEvent<RESPONSE_TYPE> extends EventBase<Void, Actor> {
    /**
     * The wrapped ResponseProcessor.
     */
    private final ResponseProcessor<RESPONSE_TYPE> rp;

    /**
     * The response.
     */
    private final RESPONSE_TYPE rsp;

    /**
     * Creates the request used to pass the response and wrapped ResponseProcessor
     * back to the original target mailbox.
     *
     * @param _rp  The wrapped ResponseProcessor.
     * @param _rsp The response.
     */
    public ContinuationEvent(final ResponseProcessor<RESPONSE_TYPE> _rp, final RESPONSE_TYPE _rsp) {
        rp = _rp;
        rsp = _rsp;
    }

    @Override
    public void processSignal(Actor _targetActor) throws Exception {
        rp.processResponse(rsp);
    }
}