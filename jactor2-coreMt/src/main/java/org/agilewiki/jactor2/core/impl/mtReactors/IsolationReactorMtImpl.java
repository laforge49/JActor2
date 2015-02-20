package org.agilewiki.jactor2.core.impl.mtReactors;

import org.agilewiki.jactor2.core.impl.mtMessages.RequestMtImpl;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.impl.ReactorImpl;

import java.util.Collections;
import java.util.HashSet;
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
    public IsolationReactorMtImpl(final IsolationReactor _parentReactor,
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
        if (isResource(_reactorImpl))
            return;
        IsolationReactorMtImpl isolationReactorMtImpl = (IsolationReactorMtImpl) _reactorImpl;
        if (isolationReactorMtImpl.isResource(this)) {
            throw new IllegalStateException("circular resources");
        }
        resources.add(isolationReactorMtImpl);
    }

    @Override
    public boolean isResource(ReactorImpl _reactorImpl) {
        if (!(_reactorImpl instanceof IsolationReactorMtImpl))
            return true;
        if (this == _reactorImpl)
            return true;
        if (resources.contains(_reactorImpl))
            return true;
        Set<IsolationReactorMtImpl> rs = new HashSet<IsolationReactorMtImpl>(resources.size());
        Set<IsolationReactorMtImpl> visited = new HashSet<IsolationReactorMtImpl>(resources.size());
        while (rs.size() > 0) {
            IsolationReactorMtImpl i = rs.iterator().next();
            if (!visited.contains(i)) {
                if (i.resources.contains(_reactorImpl)) {
                    resources.add((IsolationReactorMtImpl) _reactorImpl);
                    return true;
                }
                rs.addAll(i.resources);
                visited.add(i);
            }
            rs.remove(i);
        }
        return false;
    }
}
