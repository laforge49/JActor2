package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.closeable.Closeable;
import org.agilewiki.jactor2.core.closeable.CloseableImpl;
import org.agilewiki.jactor2.core.impl.ReactorImpl;
import org.agilewiki.jactor2.core.plant.Recovery;
import org.agilewiki.jactor2.core.requests.SyncRequest;

/**
 * Base class for reactors.
 */
abstract public class ReactorBase implements Reactor {

    private ReactorImpl reactorImpl;

    /**
     * Initialize the reactor.
     *
     * @param _reactorImpl    The object used to implement the reactor.
     */
    public void initialize(final ReactorImpl _reactorImpl) {
        if (_reactorImpl != null)
            reactorImpl = _reactorImpl;
        _reactorImpl.initialize(this);
    }

    @Override
    public ReactorImpl asReactorImpl() {
        return reactorImpl;
    }

    @Override
    public CloseableImpl asCloseableImpl() {
        return reactorImpl.asCloseableImpl();
    }

    @Override
    public Reactor getReactor() {
        return this;
    }

    @Override
    public Reactor getParentReactor() {
        return asReactorImpl().getParentReactor();
    }

    @Override
    public boolean addCloseable(Closeable _closeable) throws Exception {
        return reactorImpl.addCloseable(_closeable);
    }

    @Override
    public boolean removeCloseable(Closeable _closeable) {
        return reactorImpl.removeCloseable(_closeable);
    }

    @Override
    public void close() throws Exception {
        reactorImpl.close();
    }

    @Override
    public boolean isInboxEmpty() {
        return asReactorImpl().isInboxEmpty();
    }

    @Override
    public SyncRequest<Void> nullSReq() {
        return asReactorImpl().nullSReq();
    }

    @Override
    public Recovery getRecovery() {
        return asReactorImpl().recovery;
    }

    @Override
    public void setRecovery(final Recovery _recovery) {
        asReactorImpl().recovery = _recovery;
    }
}
