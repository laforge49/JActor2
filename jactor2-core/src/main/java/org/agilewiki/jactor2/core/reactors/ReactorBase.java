package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.blades.ExceptionHandler;
import org.agilewiki.jactor2.core.impl.CloserImpl;
import org.agilewiki.jactor2.core.impl.ReactorImpl;
import org.agilewiki.jactor2.core.requests.SyncRequest;

abstract public class ReactorBase implements Closer, Reactor {

    private CloserImpl closerImpl;

    public void initialize(final ReactorImpl _reactorImpl) throws Exception {
        if (_reactorImpl != null)
            closerImpl = _reactorImpl;
        _reactorImpl.initialize(this);
    }

    @Override
    public CloserImpl asCloserImpl() {
        return closerImpl;
    }

    @Override
    public ReactorImpl asReactorImpl() {
        return (ReactorImpl) asCloserImpl();
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
    public boolean addCloseable(CloseableBase _closeable) throws Exception {
        return closerImpl.addCloseable(_closeable);
    }

    @Override
    public boolean removeCloseable(CloseableBase _closeable) {
        return closerImpl.removeCloseable(_closeable);
    }

    @Override
    public void close() throws Exception {
        closerImpl.close();
    }

    @Override
    public ExceptionHandler setExceptionHandler(ExceptionHandler exceptionHandler) {
        return asReactorImpl().setExceptionHandler(exceptionHandler);
    }

    @Override
    public boolean isInboxEmpty() {
        return asReactorImpl().isInboxEmpty();
    }

    @Override
    public SyncRequest<Void> nullSReq() {
        return asReactorImpl().nullSReq();
    }
}
