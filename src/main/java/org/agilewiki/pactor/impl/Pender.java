package org.agilewiki.pactor.impl;

import java.util.concurrent.Semaphore;

final class Pender implements MessageSource {
    private final Semaphore done = new Semaphore(0);
    private transient Object result;

    public Object pend() throws Exception {
        done.acquire();
        if (result instanceof Exception)
            throw (Exception) result;
        if (result instanceof Error)
            throw (Error) result;
        return result;
    }

    @Override
    public void incomingResponse(final RequestMessage requestMessage,
            final Object response) {
        this.result = response;
        done.release();
    }
}
