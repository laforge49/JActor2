package org.agilewiki.jactor2.core.mt.mtReactors;

import org.agilewiki.jactor2.core.impl.reactorsImpl.MigrationException;
import org.agilewiki.jactor2.core.reactors.BlockingReactor;
import org.agilewiki.jactor2.core.impl.requestsImpl.RequestImpl;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

/**
 * The internal implementation of BlockingReactor.
 */
public class BlockingReactorImpl extends PoolThreadReactorImplBase {

    /**
     * Create a BlockingReactorImpl.
     *
     * @param _parentReactor        The parent reactor.
     * @param _initialOutboxSize        The initial buffer size for outgoing messages.
     * @param _initialLocalQueueSize    The initial local queue size.
     */
    public BlockingReactorImpl(final NonBlockingReactor _parentReactor,
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

    public boolean isSlow() {
        return true;
    }
}
