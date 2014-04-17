package org.agilewiki.jactor2.core.impl.stReactors;

import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.requests.RequestImpl;

/**
 * Internal implementation of UnboundReactor.
 */
public class IsolationReactorStImpl extends PoolThreadReactorStImpl {

    /**
     * Create an IsolationReactorMtImpl.
     *
     * @param _parentReactor         The parent reactor.
     */
    public IsolationReactorStImpl(final NonBlockingReactor _parentReactor) {
        super(_parentReactor);
    }

    @Override
    public IsolationReactor asReactor() {
        return (IsolationReactor) getReactor();
    }

    @Override
    protected Inbox createInbox() {
        return new IsolationInbox();
    }
}
