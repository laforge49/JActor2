package org.agilewiki.jactor2.core.blades.transactions;

import org.agilewiki.jactor2.core.blades.IsolationBlade;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class ImmutableReference<IMMUTABLE> implements IsolationBlade {
    private IsolationReactor reactor;
    protected IMMUTABLE immutable = null;

    public ImmutableReference(final IMMUTABLE _immutable) {
        reactor = new IsolationReactor();
        immutable = _immutable;
    }

    public ImmutableReference(final IMMUTABLE _immutable, final NonBlockingReactor _parentReactor) {
        reactor = new IsolationReactor(_parentReactor);
        immutable = _immutable;
    }

    ImmutableReference() {
    }

    public IsolationReactor getReactor() {
        return reactor;
    }

    public IMMUTABLE getImmutable() {
        return immutable;
    }
}
