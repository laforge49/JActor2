package org.agilewiki.jactor2.core.impl;

import org.agilewiki.jactor2.core.reactors.IsolationReactor;

public class IsolationReactorImpl extends UnboundReactorImpl {

    public IsolationReactorImpl(final NonBlockingReactorImpl _parentReactorImpl,
                                final int _initialOutboxSize, final int _initialLocalQueueSize) throws Exception {
        super(_parentReactorImpl, _initialOutboxSize, _initialLocalQueueSize);
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
