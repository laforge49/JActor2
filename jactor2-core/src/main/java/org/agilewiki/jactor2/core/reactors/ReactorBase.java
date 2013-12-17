package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.blades.ExceptionHandler;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.plant.ServiceClosedException;
import org.agilewiki.jactor2.core.util.Closeable;

abstract public class ReactorBase implements Reactor {

    private final ReactorImpl reactorImpl;

    public ReactorBase(final ReactorImpl _reactorImpl) {
        reactorImpl = _reactorImpl;
    }

    @Override
    public Plant getPlant() {
        return reactorImpl.getPlant();
    }

    @Override
    public Facility getFacility() {
        return reactorImpl.getFacility();
    }

    @Override
    public ExceptionHandler setExceptionHandler(ExceptionHandler exceptionHandler) {
        return reactorImpl.setExceptionHandler(exceptionHandler);
    }

    @Override
    public boolean isInboxEmpty() {
        return reactorImpl.isInboxEmpty();
    }

    @Override
    public boolean isClosing() {
        return reactorImpl.isClosing();
    }

    @Override
    public SyncRequest<Void> nullSReq() {
        return reactorImpl.nullSReq();
    }

    @Override
    public void close() throws Exception {
        reactorImpl.close();
    }

    @Override
    public Reactor getReactor() {
        return reactorImpl.getReactor();
    }

    @Override
    public boolean addCloseable(Closeable _closeable) throws ServiceClosedException {
        return reactorImpl.addCloseable(_closeable);
    }

    @Override
    public boolean removeCloseable(Closeable _closeable) {
        return reactorImpl.removeCloseable(_closeable);
    }
}
