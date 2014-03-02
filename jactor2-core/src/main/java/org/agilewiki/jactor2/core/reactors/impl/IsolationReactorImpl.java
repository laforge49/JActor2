package org.agilewiki.jactor2.core.reactors.impl;

import org.agilewiki.jactor2.core.requests.impl.RequestImpl;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;

public class IsolationReactorImpl extends UnboundReactorImpl {

    public IsolationReactorImpl(final NonBlockingReactorImpl _parentReactorImpl,
                                final int _initialOutboxSize, final int _initialLocalQueueSize) {
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
    protected void processMessage(final RequestImpl _message) {
        super.processMessage(_message);
        try {
            flush(true);
        } catch (final MigrationException me) {
            throw me;
        } catch (final Exception e) {
            logger.error("Exception thrown by flush", e);
        }
    }
}
