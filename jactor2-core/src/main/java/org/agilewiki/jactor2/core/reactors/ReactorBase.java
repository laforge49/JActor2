package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.blades.ExceptionHandler;
import org.agilewiki.jactor2.core.impl.ReactorImpl;
import org.agilewiki.jactor2.core.requests.SyncRequest;

abstract public class ReactorBase extends CloserBase implements Reactor {

    public void initialize(final ReactorImpl _reactorImpl) throws Exception {
        super.initialize(_reactorImpl);
        _reactorImpl.initialize(this);
    }

    @Override
    public ReactorImpl asReactorImpl() {
        return (ReactorImpl) asCloserImpl();
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

    @Override
    public Reactor getParentReactor() {
        return asReactorImpl().getParentReactor();
    }
}
