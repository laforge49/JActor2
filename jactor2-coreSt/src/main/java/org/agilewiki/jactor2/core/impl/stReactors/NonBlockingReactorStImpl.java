package org.agilewiki.jactor2.core.impl.stReactors;

import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Internal implementation of NonBlockingReactor.
 */
public class NonBlockingReactorStImpl extends PoolThreadReactorStImpl {

    /**
     * Create a NonBlockingReactorMtImpl.
     *
     * @param _parentReactor         The parent reactor.
     */
    public NonBlockingReactorStImpl(final NonBlockingReactor _parentReactor) {
        super(_parentReactor);
    }

    @Override
    public NonBlockingReactor asReactor() {
        return (NonBlockingReactor) getReactor();
    }

    /**
     * Returns the atomic reference to the reactor's thread.
     *
     * @return The atomic reference to the reactor's thread.
     */
    @Override
    public AtomicReference<Thread> getThreadReference() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Inbox createInbox() {
        return new CommonInbox();
    }
}
