package org.agilewiki.pactor.impl;

import java.util.concurrent.Semaphore;

final class Pender implements MessageSource {
    private Semaphore done = new Semaphore(0);
    private transient Object result;

    public Object pend() throws Throwable {
        done.acquire();
        if (result instanceof Throwable)
            throw (Throwable) result;
        return result;
    }

    @Override
    public void incomingResponse(RequestMessage requestMessage, Object response) {
        this.result = response;
        done.release();
    }
}
