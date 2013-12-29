package org.agilewiki.jactor2.core.impl;

import org.agilewiki.jactor2.core.plant.MigrationException;
import org.agilewiki.jactor2.core.plant.Scheduler;
import org.agilewiki.jactor2.core.reactors.NonBlockingInbox;
import org.agilewiki.jactor2.core.util.Recovery;

public class BlockingReactorImpl extends UnboundReactorImpl {

    public BlockingReactorImpl(final ReactorImpl _parentReactorImpl,
                               final int _initialOutboxSize, final int _initialLocalQueueSize,
                               final Recovery _recovery, final Scheduler _scheduler,
                               final Runnable _onIdle) throws Exception {
        super(_parentReactorImpl, _initialOutboxSize, _initialLocalQueueSize, _recovery, _scheduler, _onIdle);
    }

    @Override
    protected Inbox createInbox(final int _initialLocalQueueSize) {
        return new NonBlockingInbox(_initialLocalQueueSize);
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
