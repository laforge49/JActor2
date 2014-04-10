package org.agilewiki.jactor2.core.impl;

import org.agilewiki.jactor2.core.impl.reactorsImpl.MigrationException;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.impl.requestsImpl.RequestImpl;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

/**
 * Internal implementation of UnboundReactor.
 */
public class IsolationReactorImpl extends PoolThreadReactorImplBase {

    /**
     * Create an IsolationReactorImpl.
     *
     * @param _parentReactor        The parent reactor.
     * @param _initialOutboxSize        The initial buffer size for outgoing messages.
     * @param _initialLocalQueueSize    The initial local queue size.
     */
    public IsolationReactorImpl(final NonBlockingReactor _parentReactor,
                                final int _initialOutboxSize, final int _initialLocalQueueSize) {
        super(_parentReactor, _initialOutboxSize, _initialLocalQueueSize);
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
