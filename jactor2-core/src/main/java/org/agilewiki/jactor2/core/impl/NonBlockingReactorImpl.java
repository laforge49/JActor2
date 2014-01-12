package org.agilewiki.jactor2.core.impl;

import org.agilewiki.jactor2.core.reactors.NonBlockingInbox;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class NonBlockingReactorImpl extends UnboundReactorImpl {

    public NonBlockingReactorImpl(final NonBlockingReactorImpl _parentReactorImpl,
                                  final int _initialOutboxSize, final int _initialLocalQueueSize) throws Exception {
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
