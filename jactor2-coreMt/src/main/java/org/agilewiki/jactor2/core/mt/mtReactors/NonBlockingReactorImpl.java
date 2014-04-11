package org.agilewiki.jactor2.core.mt.mtReactors;

import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

/**
 * Internal implementation of NonBlockingReactor.
 */
public class NonBlockingReactorImpl extends PoolThreadReactorMtImpl {

    /**
     * Create a NonBlockingReactorImpl.
     *
     * @param _parentReactor        The parent reactor.
     * @param _initialOutboxSize        The initial buffer size for outgoing messages.
     * @param _initialLocalQueueSize    The initial local queue size.
     */
    public NonBlockingReactorImpl(final NonBlockingReactor _parentReactor,
                                  final int _initialOutboxSize, final int _initialLocalQueueSize) {
        super(_parentReactor, _initialOutboxSize, _initialLocalQueueSize);
    }

    @Override
    public NonBlockingReactor asReactor() {
        return (NonBlockingReactor) getReactor();
    }

    @Override
    protected Inbox createInbox(int _initialLocalQueueSize) {
        return new CommonInbox(_initialLocalQueueSize);
    }
}
