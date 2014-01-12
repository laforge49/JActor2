package org.agilewiki.jactor2.core.impl;

import org.agilewiki.jactor2.core.plant.MigrationException;
import org.agilewiki.jactor2.core.reactors.BlockingReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingInbox;

public class BlockingReactorImpl extends UnboundReactorImpl {

    public BlockingReactorImpl(final NonBlockingReactorImpl _parentReactorImpl,
                               final int _initialOutboxSize, final int _initialLocalQueueSize) throws Exception {
        super(_parentReactorImpl, _initialOutboxSize, _initialLocalQueueSize);
    }

    @Override
    public BlockingReactor asReactor() {
        return (BlockingReactor) getReactor();
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
