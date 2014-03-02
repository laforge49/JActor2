package org.agilewiki.jactor2.core.reactors.impl;

import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class NonBlockingReactorImpl extends UnboundReactorImpl {

    public NonBlockingReactorImpl(final NonBlockingReactorImpl _parentReactorImpl,
                                  final int _initialOutboxSize, final int _initialLocalQueueSize) {
        super(_parentReactorImpl, _initialOutboxSize, _initialLocalQueueSize);
    }

    @Override
    public NonBlockingReactor asReactor() {
        return (NonBlockingReactor) getReactor();
    }

    @Override
    protected Inbox createInbox(int _initialLocalQueueSize) {
        return new NonBlockingInbox(_initialLocalQueueSize);
    }
}
