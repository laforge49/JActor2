package org.agilewiki.jactor2.core.impl.mtReactors;

import org.agilewiki.jactor2.core.impl.mtMessages.RequestMtImpl;
import org.agilewiki.jactor2.core.reactors.BlockingReactor;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;

/**
 * The internal implementation of BlockingReactor.
 */
public class BlockingReactorMtImpl extends PoolThreadReactorMtImpl {

    /**
     * Create a BlockingReactorMtImpl.
     *
     * @param _parentReactor         The parent reactor.
     * @param _initialOutboxSize     The initial buffer size for outgoing messages.
     * @param _initialLocalQueueSize The initial local queue size.
     */
    public BlockingReactorMtImpl(final IsolationReactor _parentReactor,
            final int _initialOutboxSize, final int _initialLocalQueueSize) {
        super(_parentReactor, _initialOutboxSize, _initialLocalQueueSize);
    }

    @Override
    public BlockingReactor asReactor() {
        return (BlockingReactor) getReactor();
    }

    @Override
    protected Inbox createInbox(final int _initialLocalQueueSize) {
        return new CommonInbox(_initialLocalQueueSize);
    }

    @Override
    protected void processMessage(final RequestMtImpl<?> _message) {
        super.processMessage(_message);
        try {
            flush(true);
        } catch (final MigrationException me) {
            throw me;
        } catch (final Exception e) {
            logger.error("Exception thrown by flush", e);
        }
    }

    @Override
    public boolean isSlow() {
        return true;
    }
}
