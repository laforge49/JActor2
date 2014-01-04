package org.agilewiki.jactor2.core.impl;

import org.agilewiki.jactor2.core.plant.Scheduler;
import org.agilewiki.jactor2.core.reactors.NonBlockingInbox;
import org.agilewiki.jactor2.core.util.Recovery;

public class NonBlockingReactorImpl extends UnboundReactorImpl {

    public NonBlockingReactorImpl(final ReactorImpl _parentReactorImpl,
                                  final int _initialOutboxSize, final int _initialLocalQueueSize,
                                  final Recovery _recovery, final Scheduler _scheduler) throws Exception {
        super(_parentReactorImpl, _initialOutboxSize, _initialLocalQueueSize, _recovery, _scheduler);
    }

    @Override
    protected Inbox createInbox(int _initialLocalQueueSize) {
        return new NonBlockingInbox(_initialLocalQueueSize);
    }
}
