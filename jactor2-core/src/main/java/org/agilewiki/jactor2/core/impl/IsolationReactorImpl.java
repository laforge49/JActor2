package org.agilewiki.jactor2.core.impl;

import org.agilewiki.jactor2.core.plant.MigrationException;
import org.agilewiki.jactor2.core.plant.Scheduler;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.util.Recovery;

public class IsolationReactorImpl extends UnboundReactorImpl {

    public IsolationReactorImpl(final NonBlockingReactorImpl _parentReactorImpl,
                                final int _initialOutboxSize, final int _initialLocalQueueSize,
                                final Recovery _recovery, final Scheduler _scheduler) throws Exception {
        super(_parentReactorImpl, _initialOutboxSize, _initialLocalQueueSize, _recovery, _scheduler);
    }

    @Override
    public IsolationReactor asReactor() {
        return (IsolationReactor) getReactor();
    }

    @Override
    protected Inbox createInbox(final int _initialLocalQueueSize) {
        return new IsolationInbox(_initialLocalQueueSize);
    }

    @Override
    protected void processMessage(final RequestImpl message) {
        super.processMessage(message);
        try {
            flush(true);
        } catch (final MigrationException me) {
            throw me;
        } catch (final Exception e) {
            logger.error("Exception thrown by flush", e);
        }
    }
}
