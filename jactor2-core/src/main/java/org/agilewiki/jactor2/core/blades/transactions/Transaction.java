package org.agilewiki.jactor2.core.blades.transactions;

import org.agilewiki.jactor2.core.blades.IsolationBlade;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;

/**
 * A Transaction.
 */
public interface Transaction<IMMUTABLE> extends IsolationBlade,
        ImmutableSource<IMMUTABLE> {
    void _eval(final ImmutableReference<IMMUTABLE> _root,
               final AsyncRequest<IMMUTABLE> _applyAReq,
               final AsyncResponseProcessor<Void> _dis) throws Exception;
}
