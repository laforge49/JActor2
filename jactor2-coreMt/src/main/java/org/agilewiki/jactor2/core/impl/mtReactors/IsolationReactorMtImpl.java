package org.agilewiki.jactor2.core.impl.mtReactors;

import org.agilewiki.jactor2.core.impl.mtRequests.RequestMtImpl;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.impl.ReactorImpl;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Internal implementation of UnboundReactor.
 */
public class IsolationReactorMtImpl extends PoolThreadReactorMtImpl {

    private final Set<IsolationReactorMtImpl> resources =
            Collections.newSetFromMap(new ConcurrentHashMap<IsolationReactorMtImpl, Boolean>());

    /**
     * Create an IsolationReactorMtImpl.
     *
     * @param _parentReactor         The parent reactor.
     * @param _initialOutboxSize     The initial buffer size for outgoing messages.
     * @param _initialLocalQueueSize The initial local queue size.
     */
    public IsolationReactorMtImpl(final NonBlockingReactor _parentReactor,
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
    public void addResource(ReactorImpl _reactorImpl) {
        if (_reactorImpl instanceof IsolationReactorMtImpl) {
            IsolationReactorMtImpl isolationReactorMtImpl = (IsolationReactorMtImpl) _reactorImpl;
            if (isolationReactorMtImpl.isResource(this)) {
                throw new IllegalStateException("circular resources");
            }
            resources.add(isolationReactorMtImpl);
        }
    }

    @Override
    public boolean isResource(ReactorImpl _reactorImpl) {
        if (this == _reactorImpl)
            return true;
        if (_reactorImpl instanceof IsolationReactorMtImpl) {
            if (!resources.contains(_reactorImpl)) {
                Iterator<IsolationReactorMtImpl> it = resources.iterator();
                while (it.hasNext()) {
                    IsolationReactorMtImpl i = it.next();
                    if (i.isResource(_reactorImpl))
                        return true;
                }
                return false;
            }
        }
        return true;
    }
}
