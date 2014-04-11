package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.closeable.Closeable;
import org.agilewiki.jactor2.core.closeable.CloseableImpl;
import org.agilewiki.jactor2.core.impl.plantImpl.PlantImpl;
import org.agilewiki.jactor2.core.plant.Recovery;
import org.agilewiki.jactor2.core.impl.reactorsImpl.ReactorImpl;
import org.agilewiki.jactor2.core.requests.SyncRequest;

/**
 * Base class for reactors.
 */
abstract public class ReactorBase implements Reactor {
    /**
     * Returns the reactor of the current thread.
     *
     * @return The reactor of the current thread, or null.
     */
    public static Reactor getCurrentReactor() {
        return PlantImpl.getSingleton().getCurrentReactorImpl().asReactor();
    }

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
    public boolean addCloseable(Closeable _closeable) {
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
    public void fail(final String _reason) throws Exception {
        reactorImpl.fail(_reason);
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
        return asReactorImpl().getRecovery();
    }

    @Override
    public void setRecovery(final Recovery _recovery) {
        asReactorImpl().setRecovery(_recovery);
    }
}
