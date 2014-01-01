package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.blades.ExceptionHandler;
import org.agilewiki.jactor2.core.impl.ReactorImpl;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.util.CloserBase;
import org.slf4j.Logger;

abstract public class ReactorBase extends CloserBase implements Reactor {

    public ReactorBase(final ReactorImpl _reactorImpl) throws Exception {
        initialize(_reactorImpl);
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
