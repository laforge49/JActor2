package org.agilewiki.jactor2.core.reactors.impl;

import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

/**
 * Internal implementation of NonBlockingReactor.
 */
public class NonBlockingReactorImpl extends UnboundReactorImpl {

    /**
     * Create a NonBlockingReactorImpl.
     *
     * @param _parentReactorImpl        The parent reactor.
     * @param _initialOutboxSize        The initial buffer size for outgoing messages.
     * @param _initialLocalQueueSize    The initial local queue size.
     */
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
