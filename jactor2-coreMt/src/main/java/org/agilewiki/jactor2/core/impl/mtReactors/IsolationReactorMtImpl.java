package org.agilewiki.jactor2.core.impl.mtReactors;

import org.agilewiki.jactor2.core.impl.mtRequests.RequestMtImpl;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.impl.ReactorImpl;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Internal implementation of UnboundReactor.
 */
public class IsolationReactorMtImpl extends PoolThreadReactorMtImpl {

    private final Map<IsolationReactorMtImpl, Boolean> resources =
            new ConcurrentHashMap<IsolationReactorMtImpl, Boolean>();

    public final Boolean TRUE = true;

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
            resources.put(isolationReactorMtImpl, TRUE);
        }
    }

    @Override
    public boolean isResource(ReactorImpl _reactorImpl) {
        if (this == _reactorImpl)
            return true;
        if (_reactorImpl instanceof IsolationReactorMtImpl) {
            if (!resources.containsKey(_reactorImpl)) {
                Iterator<IsolationReactorMtImpl> it = resources.keySet().iterator();
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
