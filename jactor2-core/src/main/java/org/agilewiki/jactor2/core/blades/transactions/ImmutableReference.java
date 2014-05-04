package org.agilewiki.jactor2.core.blades.transactions;

import org.agilewiki.jactor2.core.blades.IsolationBlade;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.requests.Request;

public class ImmutableReference<Immutable> implements IsolationBlade {
    private IsolationReactor reactor;
    protected Immutable immutable = null;

    public ImmutableReference(final Immutable _immutable) {
        reactor = new IsolationReactor();
        immutable = _immutable;
    }

    public ImmutableReference(final Immutable _immutable, final NonBlockingReactor _parentReactor) {
        reactor = new IsolationReactor(_parentReactor);
        immutable = _immutable;
    }

    ImmutableReference() {
    }

    public IsolationReactor getReactor() {
        return reactor;
    }

    public Immutable getImmutable() {
        return immutable;
    }

    /**
     * Send a two-way message using the blade's reactor as the source.
     *
     * @param _request        The request to be passed.
     */
    protected <RESPONSE_TYPE> void send(
            final Request<RESPONSE_TYPE> _request) {
        _request.asRequestImpl().doSend(getReactor().asReactorImpl(), null);
    }
}
