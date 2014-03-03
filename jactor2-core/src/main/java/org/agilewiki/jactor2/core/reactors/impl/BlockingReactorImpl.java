package org.agilewiki.jactor2.core.reactors.impl;

import org.agilewiki.jactor2.core.reactors.BlockingReactor;
import org.agilewiki.jactor2.core.requests.impl.RequestImpl;

/**
 * The internal implementation of BlockingReactor.
 */
public class BlockingReactorImpl extends UnboundReactorImpl {

    /**
     * Create a BlockingReactorImpl.
     *
     * @param _parentReactorImpl        The parent reactor.
     * @param _initialOutboxSize        The initial buffer size for outgoing messages.
     * @param _initialLocalQueueSize    The initial local queue size.
     */
    public BlockingReactorImpl(final NonBlockingReactorImpl _parentReactorImpl,
                               final int _initialOutboxSize, final int _initialLocalQueueSize) {
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
