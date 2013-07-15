package org.agilewiki.jactor2.util;

import org.agilewiki.jactor2.api.Mailbox;
import org.agilewiki.jactor2.api.BoundRequestBase;
import org.agilewiki.jactor2.api.ResponseProcessor;
import org.agilewiki.jactor2.api.Transport;

/**
 * A thread-safe wrapper for ResponseProcessor.
 * When a boundRequest is processed, the ResponseProcessor given must only be used by the
 * same thread that is processing the boundRequest. In contrast, the processResult method
 * of BoundResponseProcessor can be called from any thread.
 *
 * @param <RESPONSE_TYPE>
 */
public class BoundResponseProcessor<RESPONSE_TYPE> implements
        ResponseProcessor<RESPONSE_TYPE> {
    /**
     * The mailbox on whose thread the wrapped ResponseProcessor object can be used.
     */
    private final Mailbox targetMailbox;

    /**
     * The wrapped ResponseProcessor.
     */
    private final ResponseProcessor<RESPONSE_TYPE> rp;

    /**
     * Create a thread-safe wrapper for a ResponseProcessor.
     *
     * @param _targetMailbox The mailbox on whose thread the wrapped ResponseProcessor
     *                       can be used.
     * @param _rp            The wrapped ResponseProcessor.
     */
    public BoundResponseProcessor(final Mailbox _targetMailbox,
                                  final ResponseProcessor<RESPONSE_TYPE> _rp) {
        targetMailbox = _targetMailbox;
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
        new ContinuationRequest<RESPONSE_TYPE>(targetMailbox, rp, rsp).signal();
    }

    /**
     * This method processes the response by passing the wrapped response and ResponseProcessor
     * via a buffered signal back to the appropriate mailbox.
     *
     * @param source The mailbox of the actor passing the signal.
     * @param rsp    The response.
     */
    public void processResponse(final Mailbox source, final RESPONSE_TYPE rsp)
            throws Exception {
        new ContinuationRequest<RESPONSE_TYPE>(targetMailbox, rp, rsp)
                .signal(source);
    }
}

/**
 * The boundRequest used to pass the response and the wrapped ResponseProcessor back to the
 * original target mailbox.
 *
 * @param <RESPONSE_TYPE> The type of response.
 */
class ContinuationRequest<RESPONSE_TYPE> extends BoundRequestBase<Void> {
    /**
     * The wrapped ResponseProcessor.
     */
    private final ResponseProcessor<RESPONSE_TYPE> rp;

    /**
     * The response.
     */
    private final RESPONSE_TYPE rsp;

    /**
     * Creates the boundRequest used to pass the response and wrapped ResponseProcessor
     * back to the original target mailbox.
     *
     * @param targetMailbox The original target mailbox.
     * @param _rp           The wrapped ResponseProcessor.
     * @param _rsp          The response.
     */
    public ContinuationRequest(final Mailbox targetMailbox,
                               final ResponseProcessor<RESPONSE_TYPE> _rp, final RESPONSE_TYPE _rsp) {
        super(targetMailbox);
        rp = _rp;
        rsp = _rsp;
    }

    /**
     * Called when the signal is received by the original target mailbox, the
     * processResponse method of the wrapped ResponseProcessor can finally be called
     * on the appropriate thread.
     *
     * @param _rp The ResponseProcessor for the signal.
     */
    @Override
    public void processRequest(final Transport<Void> _rp)
            throws Exception {
        rp.processResponse(rsp);
        _rp.processResponse(null);
    }
}